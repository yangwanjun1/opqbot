package io.github.yangwanjun1.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.yangwanjun1.annotation.OpqListener;
import io.github.yangwanjun1.constants.Action;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.AtUinLists;
import io.github.yangwanjun1.data.EventData;
import io.github.yangwanjun1.data.MessageData;
import io.github.yangwanjun1.event.*;
import io.github.yangwanjun1.event.impl.FriendMessageEvent;
import io.github.yangwanjun1.event.impl.GroupMessageEvent;
import io.github.yangwanjun1.event.impl.RedBagMessageEvent;
import io.github.yangwanjun1.event.impl.TemporarilyMessageEvent;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.yangwanjun1.core.OpqThreadPoll.getThreadPoll;
import static io.github.yangwanjun1.utils.OpqUtils.isAtALL;
import static io.github.yangwanjun1.utils.OpqUtils.isAtMe;

@Component
@Slf4j
public class HandlerEvent {
    @EventListener
    public void handler(OpqListenerEvent obj) {
        try {
            JsonNode object = OpqUtils.getMapper().readTree(obj.getSource().toString());
            JsonNode jsonNodeEvent = object.get("CurrentPacket").get("EventData");
            JsonNode msgHead = jsonNodeEvent.get("MsgHead");
            if (isNull(msgHead)) {
                handlerEvent(jsonNodeEvent,object.get("CurrentQQ").asLong());
                return;
            }
            SourceType fromType = convertType(msgHead.get("FromType").asInt());
            JsonNode msgBody = jsonNodeEvent.get("MsgBody");
            if (otherEvent(object,msgHead.get("MsgType"), fromType) || isNull(msgBody)) {
                return;
            }
            MessageData data = OpqUtils.toBean(object.toString(), MessageData.class);
            EventData eventData = data.getCurrentPacket().getEventData();
            long senderUin = eventData.getMsgHead().getSenderUin();
            long currentQQ = data.getCurrentQQ();
            if (senderUin == currentQQ) {
                return;
            }
             if (eventData.getMsgBody().getRedBag() != null) {
                RedBagMessageEvent event = new RedBagMessageEvent(eventData,currentQQ,senderUin);
                getThreadPoll().execute(() -> EventHandlerAdapter.getEvent(SourceType.MONEY).forEach((key, value) -> handlerRedBag(event, key, value)));
                return;
            }
            execPoll(EventHandlerAdapter.getEvent(fromType), data, fromType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void handlerEvent(JsonNode jsonNodeEvent, long currentQQ) {
        JsonNode status = jsonNodeEvent.get("Status");
        JsonNode msgType = jsonNodeEvent.get("MsgType");
        if (isNull(status) || status.asInt() !=1){
            return;
        }
        if (isNull(msgType)) {
            FriendRequestEvent event = new FriendRequestEvent(jsonNodeEvent,currentQQ);
            EventHandlerAdapter.getEvent(SourceType.FRIEND_REQUEST).forEach((k,v)-> invokeNotice(event,k,v));
        }
        else if (isNull(jsonNodeEvent.get("Uin"))){//过滤登录和网络变化事件
            GroupNoticeEvent event = new GroupNoticeEvent(jsonNodeEvent,msgType.asInt(),currentQQ);
            EventHandlerAdapter.getEvent(SourceType.NOTICE).forEach((k,v)-> invokeNotice(event,k,v));
        }
    }


    private boolean isNull(JsonNode node){
        return node == null || node.isNull();
    }
    private void execPoll(Map<Object, List<Method>> map, MessageData data, SourceType fromType) {
        getThreadPoll().execute(() -> map.forEach((key, value) -> invokeObj(key, value, data, fromType)));
    }

    private boolean otherEvent(JsonNode object,JsonNode node, SourceType fromType) {
        SourceType msgType = convertType(isNull(node) ? -1 : node.asInt());
        JsonNode eventData = object.get("CurrentPacket").get("EventData");
        JsonNode eventBody = eventData.get("Event");
        if (msgType == SourceType.NONE || isNull(eventBody)) {
            return false;
        }
        long currentQQ = object.get("CurrentQQ").asLong();
        long groupId = eventData.get("MsgHead").get("FromUin").asLong();
        if (msgType == SourceType.FROM_INVITE && fromType == SourceType.GROUP) {
            return execOther(SourceType.FROM_INVITE, new InviteHandlerEvent(eventBody,currentQQ,groupId));
        }
        if (msgType == SourceType.FROM_REMOVE && fromType == SourceType.GROUP){
            return execOther(SourceType.FROM_REMOVE, new ExitGroupEvent(eventBody.get("Uid").asText(),groupId,currentQQ));
        }
        return false;
    }

    public boolean execOther(SourceType type, OtherEvent otherEvent) {
        Map<Object, List<Method>> map = EventHandlerAdapter.getEvent(type);
        getThreadPoll().execute(() ->map.forEach((key, value) -> {
            try {
                invite(otherEvent, key, value);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }));
        return true;
    }

    private void invite(OtherEvent event, Object obj, List<Method> methodList) throws InvocationTargetException, IllegalAccessException {
        for (Method m : methodList) {
            m.invoke(obj, event);
        }
    }
    private void invokeNotice(OpqRequest event, Object obj, List<Method> methodList) {
        methodList.forEach(m->{
            try {
                m.invoke(obj, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }


    private void invokeObj(Object obj, List<Method> methodList, MessageData data, SourceType fromType){
        OpqMessageEvent event = switch (fromType) {
            case FRIEND -> new FriendMessageEvent( data.getCurrentPacket().getEventData(),data.getCurrentQQ());
            case GROUP -> new GroupMessageEvent(data.getCurrentPacket().getEventData(),data.getCurrentQQ());
            case TEMPORARILY -> new TemporarilyMessageEvent( data.getCurrentPacket().getEventData(),data.getCurrentQQ());
            default -> null;
        };
        Assert.notNull(event,"Unknown event");
        if (isAtMe(event.getAtUinLists(), event.getSelfId()) && !isAtALL(event.getAtUinLists())) {
            handlerAt(event, obj, methodList);
        } else {
            handlerNone(event, obj, methodList);
        }
    }

    /**
     * 处理红包事件
     */
    private void handlerRedBag(OpqMessageEvent event, Object obj, List<Method> methodList) {
        List<Method> list = methodList.stream().filter(m -> {
            OpqListener listener = m.getAnnotation(OpqListener.class);
            return listener.type().isAssignableFrom(RedBagMessageEvent.class);
        }).toList();
        execResult(list, event, obj);
    }

    /**
     * 过滤事件
     */
    private List<Method> filter(List<Method> list, Action action, boolean matcher) {
        return list.stream().filter(m -> {
            OpqListener listener = m.getAnnotation(OpqListener.class);
            return listener.action() == action && (matcher == (!listener.matcher().isEmpty()));
        }).toList();
    }

    /**
     * 处理at事件
     */

    private void handlerAt(OpqMessageEvent event, Object obj, List<Method> methodList){
        List<Method> list = filter(methodList, Action.AT, true);
        StringBuilder sb = new StringBuilder(event.getContent());
        for (AtUinLists uinList : Optional.ofNullable(event.getAtUinLists()).orElse(Collections.emptyList())) {
            if (uinList.getUin() == event.getSelfId()) {//消除机器人昵称
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
        for (Method m : noMatcher) {
            execInvoke(m, event, obj, null);
        }
    }

    /**
     * 执行没有条件的事件
     */
    private void handlerNone(OpqMessageEvent event, Object obj, List<Method> methodList) {
        List<Method> list = filter(methodList, Action.NONE, true);
        if (!list.isEmpty() && execResult(list, event, obj)) {
            return;
        }
        List<Method> noMatcher = filter(methodList, Action.NONE, false);
        for (Method m : noMatcher) {
            execInvoke(m, event, obj, null);
        }
    }

    /**
     * 处理opq对象事件
     */
    private boolean execResult(List<Method> list, OpqMessageEvent event, Object obj){
        for (Method m : list) {
            if (execInvoke(m, event, obj, event.getContent())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行事件
     */
    public boolean execInvoke(Method m, OpqMessageEvent event, Object obj, String content) {
        OpqListener listener = m.getAnnotation(OpqListener.class);
        String matcher = listener.matcher();
        try {
            if (!matcher.isEmpty()) {
                Matcher matchered = Pattern.compile(matcher).matcher(Optional.ofNullable(content).orElse("").strip());
                if (matchered.find()) {//有正则，但不匹配
                    m.invoke(obj, loadParams(m, event, matchered));
                    return true;
                }
                return false;
            }
            m.invoke(obj, loadParams(m, event, null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    /**
     * 加载参数
     */
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

    /**
     * 获取事件类型
     */
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
