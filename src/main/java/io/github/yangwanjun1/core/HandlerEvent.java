package io.github.yangwanjun1.core;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.yangwanjun1.data.*;
import io.github.yangwanjun1.event.*;
import lombok.extern.slf4j.Slf4j;
import io.github.yangwanjun1.annotation.OpqListener;
import io.github.yangwanjun1.constants.Action;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.event.impl.FriendMessageEvent;
import io.github.yangwanjun1.event.impl.GroupMessageEvent;
import io.github.yangwanjun1.event.impl.RedBagMessageEvent;
import io.github.yangwanjun1.event.impl.TemporarilyMessageEvent;
import io.github.yangwanjun1.utils.OpqUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.yangwanjun1.core.OpqThreadPoll.getThreadPoll;
import static io.github.yangwanjun1.utils.OpqUtils.*;

@Component
@Slf4j
public class HandlerEvent {

    private Pattern pattern = Pattern.compile("^(\\d+)\\b");

    @EventListener
    public void handler(OpqListenerEvent obj) {
        try {
            String message = obj.getSource().toString();
            JsonNode object = OpqUtils.getMapper().readTree(message);
            //处理其他事件（登录 网络变化事 好友相关事件 为空）
            JsonNode jsonNodeEvent = object.get("CurrentPacket").get("EventData");
            if (jsonNodeEvent.get("MsgHead") == null) {
                return;
            }
            JsonNode node = jsonNodeEvent.get("MsgHead").get("MsgType");
            SourceType fromType = convertType(jsonNodeEvent.get("MsgHead").get("FromType").asInt());
            if (node != null && otherEvent(object, convertType(node.asInt()), fromType)) {
                return;
            }
            if (jsonNodeEvent.get("MsgBody") == null) {
                return;
            }
            MessageData data = OpqUtils.toBean(object.toString(), MessageData.class);
            EventData eventData = data.getCurrentPacket().getEventData();
            long senderUin = eventData.getMsgHead().getSenderUin();
            long currentQQ = data.getCurrentQQ();
            if (senderUin == currentQQ) {
                return;
            }
            Map<Object, List<Method>> map = EventHandlerAdapter.getEvent(fromType);
            if (eventData.getMsgBody()==null) {
                return;
            }
            if (eventData.getMsgBody().getRedBag() != null) {
                RedBagMessageEvent event = getBagMessageEvent(eventData, data, senderUin);
                getThreadPoll().execute(() -> EventHandlerAdapter.getEvent(SourceType.MONEY).forEach((key, value) -> handlerRedBag(event, key, value)));
                return;
            }
            execPoll(map, data, fromType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execPoll(Map<Object, List<Method>> map, MessageData data, SourceType fromType) {
        getThreadPoll().execute(() -> map.forEach((key, value) -> invokeObj(key, value, data, fromType)));
    }

    private boolean otherEvent(JsonNode object, SourceType msgType, SourceType fromType) {
        if (msgType == SourceType.NONE) {
            return false;
        }
        JsonNode node = object.get("CurrentPacket").get("EventData").get("Event");
        if (Objects.isNull(node)) {
            return false;
        }
        long currentQQ = object.get("CurrentQQ").asLong();
        long groupId = object.get("CurrentPacket").get("EventData").get("MsgHead").get("FromUin").asLong();
        if (msgType == SourceType.FROM_INVITE && fromType == SourceType.GROUP) {//邀请已经处理事件
            InviteHandlerEvent event = new InviteHandlerEvent();
            event.setInvitor(node.get("AdminUid").asText());
            event.setInvitee(node.get("Uid").asText());
            event.setSelfId(currentQQ);
            event.setGroupId(groupId);
            execOther(SourceType.FROM_INVITE, event);
            return true;
        }
        if (msgType == SourceType.FROM_REMOVE && fromType == SourceType.GROUP){
            ExitGroupEvent event = new ExitGroupEvent();
            event.setGroupId(groupId);
            event.setSelfId(currentQQ);
            event.setUid(object.get("CurrentPacket").get("EventData").get("Event").get("Uid").asText());
            execOther(SourceType.FROM_REMOVE, event);
            return true;
        }
        return false;
    }

    public void execOther(SourceType type, OtherEvent otherEvent) {
        Map<Object, List<Method>> map = EventHandlerAdapter.getEvent(type);
        getThreadPoll().execute(() ->map.forEach((key, value) -> invite(otherEvent, key, value)));
    }

    private void invite(OtherEvent event, Object obj, List<Method> methodList) {
        methodList.forEach(m -> {
            try {
                m.invoke(obj, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static RedBagMessageEvent getBagMessageEvent(EventData eventData, MessageData data, long senderUin) {
        GroupInfo group = eventData.getMsgHead().getGroupInfo();
        RedBagMessageEvent event = new RedBagMessageEvent();
        event.setRedBag(eventData.getMsgBody().getRedBag());
        event.setMsgId(data.getCurrentPacket().getEventData().getMsgHead().getMsgUid());
        event.setUserInfo(new UserInfo(senderUin, Objects.isNull(group) ? null : group.getGroupCard()));
        event.setSelfId(data.getCurrentQQ());
        event.setRedBag(eventData.getMsgBody().getRedBag());
        event.setGroup(group);
        return event;
    }

    private void invokeObj(Object obj, List<Method> methodList, MessageData data, SourceType fromType) {
        EventData eventData = data.getCurrentPacket().getEventData();
        GroupInfo group = eventData.getMsgHead().getGroupInfo();
        OpqMessageEvent event = switch (fromType) {
            case FRIEND -> new FriendMessageEvent();
            case GROUP -> {
                GroupMessageEvent event1 = new GroupMessageEvent();
                event1.setGroup(group);
                yield event1;
            }
            case TEMPORARILY -> new TemporarilyMessageEvent();
            default -> null;
        };
        if (Objects.isNull(event)) {
            log.error("listener must hava a event parameter");
            return;
        }
        event.setUserInfo(new UserInfo(eventData.getMsgHead().getSenderUin(), Objects.isNull(group) ? null : group.getGroupCard()));
        event.setMsgId(data.getCurrentPacket().getEventData().getMsgHead().getMsgUid());
        event.setVideo(eventData.getMsgBody().getVideo());
        event.setVoice(eventData.getMsgBody().getVoice());
        event.setImages(eventData.getMsgBody().getImages());
        event.setContent(eventData.getMsgBody().getContent());
        event.setAtUinLists(eventData.getMsgBody().getAtUinLists());
        long time = eventData.getMsgHead().getMsgTime();
        event.setSendTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("UTC+8")));
        event.setMsgTime(LocalDateTime.now(ZoneId.of("UTC+8")));
        event.setSelfId(data.getCurrentQQ());
        if (isAtMe(event.getAtUinLists(), event.getSelfId()) && !isAtALL(event.getAtUinLists())) {
            handlerAt(event, obj, methodList);
        } else {
            handlerNone(event, obj, methodList);
        }
    }

    private void handlerRedBag(OpqMessageEvent event, Object obj, List<Method> methodList) {
        List<Method> list = methodList.stream().filter(m -> {
            OpqListener listener = m.getAnnotation(OpqListener.class);
            return listener.type().isAssignableFrom(RedBagMessageEvent.class);
        }).toList();
        execResult(list, event, obj);
    }

    private List<Method> filter(List<Method> list, Action action, boolean matcher) {
        return list.stream().filter(m -> {
            OpqListener listener = m.getAnnotation(OpqListener.class);
            return listener.action() == action && (matcher == (!listener.matcher().isEmpty()));
        }).toList();
    }


    private void handlerAt(OpqMessageEvent event, Object obj, List<Method> methodList) {
        List<Method> list = filter(methodList, Action.AT, true);
        StringBuilder sb = new StringBuilder(event.getContent());
        for (AtUinLists uinList : Optional.ofNullable(event.getAtUinLists()).orElse(Collections.emptyList())) {
            if (uinList.getUin() == event.getSelfId()) {
                String str = "@" + uinList.getNick();
                int i = sb.indexOf(str);
                sb.replace(i, i + (str).length(), "");
                break;
            }
        }
        event.setContent(sb.isEmpty() ? null : sb.toString().strip());
        event.setAtUinLists(Optional.ofNullable(
                        event.getAtUinLists()).orElse(Collections.emptyList())
                .stream().filter(a -> a.getUin() != event.getSelfId()).toList()
        );
        if (!list.isEmpty() && execResult(list, event, obj)) {
            return;
        }
        List<Method> noMatcher = filter(methodList, Action.AT, false);
        noMatcher.forEach(m -> execInvoke(m, event, obj, null));
    }

    private void handlerNone(OpqMessageEvent event, Object obj, List<Method> methodList) {
        List<Method> list = filter(methodList, Action.NONE, true);
        if (!list.isEmpty() && execResult(list, event, obj)) {
            return;
        }
        List<Method> noMatcher = filter(methodList, Action.NONE, false);
        noMatcher.forEach(m -> execInvoke(m, event, obj, null));
    }

    private boolean execResult(List<Method> list, OpqMessageEvent event, Object obj) {
        for (Method m : list) {
            if (execInvoke(m, event, obj, event.getContent())) {
                return true;
            }
        }
        return false;
    }

    public boolean execInvoke(Method m, OpqMessageEvent event, Object obj, String content) {
        try {
            OpqListener listener = m.getAnnotation(OpqListener.class);
            String matcher = listener.matcher();
            if (!matcher.isEmpty()) {
                Matcher matchered = Pattern.compile(matcher).matcher(content.strip());
                if (!matchered.find()) {//有正则，但不匹配
                    return false;
                }
                m.invoke(obj, loadParams(m, event, matchered));
                return true;
            }
            m.invoke(obj, loadParams(m, event, null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public Object[] loadParams(Method m, OpqMessageEvent event, Matcher matcher) {
        Parameter[] parameters = m.getParameters();
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            if (OpqMessageEvent.class.isAssignableFrom(type)) {
                objects[i] = event;
            } else if (Matcher.class.equals(type)) {
                objects[i] = matcher;
            } else {
                objects[i] = null;
            }
        }
        return objects;
    }

    public SourceType convertType(int type) {
        return switch (type) {
            case 1 -> SourceType.FRIEND;
            case 2 -> SourceType.GROUP;
            case 3 -> SourceType.TEMPORARILY;
            case 33 -> SourceType.FROM_INVITE;
            case 34 -> SourceType.FROM_REMOVE;
            default -> SourceType.NONE;
        };
    }

}
