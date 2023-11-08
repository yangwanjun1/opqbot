package io.github.yangwanjun1.core;

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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
    @Resource(name = "opqProperties")
    private OpqProperties properties;
    @EventListener
    public void handler(OpqListenerEvent obj) {
        try {
            String message = obj.getSource().toString();
            JsonNode object = OpqUtils.getMapper().readTree(message);
            JsonNode jsonNodeEvent = object.get("CurrentPacket").get("EventData");
            JsonNode msgHead = jsonNodeEvent.get("MsgHead");
            if (isNull(msgHead)) {
                //处理其他事件（登录，上下线 网络变化事 好友相关事件 为空）
                handlerEvent(jsonNodeEvent,object.get("CurrentQQ").asLong());
                return;
            }
            JsonNode node = msgHead.get("MsgType");
            SourceType fromType = convertType(msgHead.get("FromType").asInt());
            JsonNode msgBody = jsonNodeEvent.get("MsgBody");
            if (otherEvent(object,node, fromType) || isNull(msgBody)) {
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
        }
        catch (Exception e) {
            if (!properties.getCloseException()){
                e.printStackTrace();
            }else{
                log.error("{}",e.getMessage());
            }
        }
    }

    private void handlerEvent(JsonNode jsonNodeEvent, long currentQQ) {
        JsonNode status = jsonNodeEvent.get("Status");
        JsonNode msgType = jsonNodeEvent.get("MsgType");
        if (!isNull(status) && status.asInt() == 1 && !isNull(msgType)){
            GroupNoticeEvent event = new GroupNoticeEvent(jsonNodeEvent,msgType,currentQQ);
            EventHandlerAdapter.getEvent(SourceType.NOTICE).forEach((k,v)->{
                invokeNotice(event,k,v);
            });
        } else if (isNull(msgType)) {
//            status = 1 Src=验证信息 SrcId=3041（此时bot状态为加好友需要验证）
        }
    }


    private boolean isNull(JsonNode node){
        return node == null || node.isNull();
    }
    private void execPoll(Map<Object, List<Method>> map, MessageData data, SourceType fromType) {
        getThreadPoll().execute(() -> map.forEach((key, value) -> invokeObj(key, value, data, fromType)));
    }

    private boolean otherEvent(JsonNode object,JsonNode node, SourceType fromType) {
        SourceType msgType = convertType(node == null ? -1 : node.asInt());
        JsonNode eventData = object.get("CurrentPacket").get("EventData");
        JsonNode eventBody = eventData.get("Event");
        if (msgType == SourceType.NONE || isNull(eventBody)) {
            return false;
        }
        long currentQQ = object.get("CurrentQQ").asLong();
        long groupId = eventData.get("MsgHead").get("FromUin").asLong();
        if (msgType == SourceType.FROM_INVITE && fromType == SourceType.GROUP) {//邀请已经处理事件
            return execOther(SourceType.FROM_INVITE, new InviteHandlerEvent(eventBody,currentQQ,groupId));
        }
        if (msgType == SourceType.FROM_REMOVE && fromType == SourceType.GROUP){//退群事件
            return execOther(SourceType.FROM_REMOVE, new ExitGroupEvent(eventBody.get("Uid").asText(),groupId,currentQQ));
        }
        return false;
    }

    public boolean execOther(SourceType type, OtherEvent otherEvent) {
        Map<Object, List<Method>> map = EventHandlerAdapter.getEvent(type);
        getThreadPoll().execute(() ->map.forEach((key, value) -> invite(otherEvent, key, value)));
        return true;
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
    private void invokeNotice(GroupNoticeEvent event, Object obj, List<Method> methodList) {
        methodList.forEach(m -> {
            try {
                m.invoke(obj, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

    }


    private void invokeObj(Object obj, List<Method> methodList, MessageData data, SourceType fromType) {
        OpqMessageEvent event = switch (fromType) {
            case FRIEND -> new FriendMessageEvent( data.getCurrentPacket().getEventData(),data.getCurrentQQ());
            case GROUP -> new GroupMessageEvent(data.getCurrentPacket().getEventData(),data.getCurrentQQ());
            case TEMPORARILY -> new TemporarilyMessageEvent( data.getCurrentPacket().getEventData(),data.getCurrentQQ());
            default -> null;
        };
        if (event == null) {
            log.error("listener must hava a event parameter");
            return;
        }
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

    private void handlerAt(OpqMessageEvent event, Object obj, List<Method> methodList) {
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
        noMatcher.forEach(m -> execInvoke(m, event, obj, null));
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
        noMatcher.forEach(m -> execInvoke(m, event, obj, null));
    }

    /**
     * 处理opq对象事件
     */
    private boolean execResult(List<Method> list, OpqMessageEvent event, Object obj) {
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
        try {
            OpqListener listener = m.getAnnotation(OpqListener.class);
            String matcher = listener.matcher();
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
            throw new RuntimeException(e);
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
