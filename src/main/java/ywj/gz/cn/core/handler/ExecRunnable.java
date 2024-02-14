package ywj.gz.cn.core.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import ywj.gz.cn.annotation.QQListener;
import ywj.gz.cn.body.pojo.AtUinLists;
import ywj.gz.cn.constants.Action;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.event.QQBotEvent;
import ywj.gz.cn.event.impl.GroupEvent;
import ywj.gz.cn.util.MsgUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class ExecRunnable implements Runnable{

    private final QQBotEvent event;
    private final Map<Object, List<Method>> objectListMap;
    private final SourceType fromType;

    public ExecRunnable(QQBotEvent event, Map<Object, List<Method>> objectListMap, SourceType fromType) {
        this.event = event;
        this.objectListMap = objectListMap;
        this.fromType = fromType;
    }

    @Override
    public void run() {
        try {
            switch (fromType) {
                case FRIEND,TEMPORARILY -> {
                    if (handlerMatcher(event,objectListMap)){
                        return;
                    }
                    handlerNoMatcher(event,objectListMap);
                }
                case GROUP -> {
                    GroupEvent groupEvent = (GroupEvent) event;
                    if (MsgUtils.isAtMe(groupEvent.getAtUinLists(),event.getSelfId())){
                        handlerAtMeMatcher(groupEvent,objectListMap);
                        return;
                    }
                    if (handlerMatcher(event, objectListMap)){
                        return;
                    }
                    handlerNoMatcher(event, objectListMap);
                }
                case NOTICE, RED_BAG,FROM_REMOVE,FROM_INVITE ,FRIEND_REQUEST -> handlerNoMatcher(event,objectListMap);
                default -> log.warn("未知事件:{}",event.getClass());
            }
        }catch (Exception e){
            log.error("异常信息:{}",e.getMessage());
        }
    }
    /**
     * 处理at文本
     */
    private String handlerAtText(QQBotEvent botEvent) {
        if (!StringUtils.hasText(botEvent.getContent())) {
            return botEvent.getContent();
        }
        StringBuilder sb = new StringBuilder(botEvent.getContent());
        if (botEvent instanceof GroupEvent groupEvent){
            if (ObjectUtils.isEmpty(groupEvent.getAtUinLists())){
                return groupEvent.getContent();
            }
            for (AtUinLists uinList : groupEvent.getAtUinLists()) {
                if (uinList.getUin() == groupEvent.getSelfId()) {//消除机器人昵称
                    String str = "@" + uinList.getNick();
                    int i = sb.indexOf(str);
                    sb.replace(i, i + (str).length(), "");
                    break;
                }
            }
        }
        return sb.toString().strip();
    }
    /**
     * 处理at我，并且有正则
     */
    public void handlerAtMeMatcher(GroupEvent botEvent, Map<Object, List<Method>> objectListMap){
        for (Map.Entry<Object, List<Method>> entry : objectListMap.entrySet()) {
            //获取正则表达式
            List<Method> list = listFilter(entry.getValue(),true, Action.AT);
            if (invokeMethod(list,botEvent,entry.getKey(),true,true)){
                return ;
            }
        }
        handlerAtMe(botEvent,objectListMap);
    }

    /**
     * 处理at我的消息
     */
    public void handlerAtMe(GroupEvent botEvent, Map<Object, List<Method>> objectListMap){
        for (Map.Entry<Object, List<Method>> entry : objectListMap.entrySet()) {
            List<Method> list = listFilter(entry.getValue(),false,Action.AT);
            if (invokeMethod(list,botEvent,entry.getKey(),false,true)) {
                return ;
            }
        }
    }

    /**
     * 处理正则消息
     */
    public boolean handlerMatcher(QQBotEvent botEvent, Map<Object, List<Method>> objectListMap){
        for (Map.Entry<Object, List<Method>> entry : objectListMap.entrySet()) {
            List<Method> list = listFilter(entry.getValue(),true,Action.NONE);
            if (invokeMethod(list,botEvent,entry.getKey(),true,false)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 处理没有正则消息
     */
    public void handlerNoMatcher(QQBotEvent botEvent, Map<Object, List<Method>> objectListMap){
        for (Map.Entry<Object, List<Method>> entry : objectListMap.entrySet()) {
            List<Method> list = listFilter(entry.getValue(),false,Action.NONE);
            if (invokeMethod(list,botEvent,entry.getKey(),false,false)) {
                return ;
            }
        }
    }

    /**
     * 仅执行一个方法
     */
    private boolean invokeMethod(List<Method> list,QQBotEvent botEvent,Object targetObj,boolean isMatcher,boolean isAtMe){
        if (list.isEmpty()){
            return false;
        }
        if (isAtMe){
            String text = handlerAtText(botEvent);
            GroupEvent groupEvent = (GroupEvent) botEvent;
            List<AtUinLists> atUinLists = groupEvent.getAtUinLists().stream().filter(a->a.getUin() != botEvent.getSelfId()).toList();
            groupEvent.setAtUinLists(atUinLists);
            groupEvent.setContent(text);
        }
        if (isMatcher){
            for (Method m : list) {
                QQListener listener = m.getAnnotation(QQListener.class);
                Matcher matcher = Pattern.compile(listener.matcher()).matcher(botEvent.getContent());
                if (matcher.find()) {
                    return invoke(botEvent, targetObj, m, matcher);
                }
            }
            return false;
        }
        return invoke(botEvent, targetObj, list.get(0), null);
    }


    private Object[] loadParams(Method m, QQBotEvent event, Matcher matcher) {
        Parameter[] parameters = m.getParameters();
        int length = parameters.length;
        Object[] objects = new Object[length];
        for (int i = 0; i < length; i++) {
            Class<?> type = parameters[i].getType();
            if (QQBotEvent.class.isAssignableFrom(type)) {
                objects[i] = event;
            } else if (Matcher.class.equals(type)) {
                objects[i] = matcher;
            } else {
                objects[i] = null;
            }
        }
        return objects;
    }
    private List<Method> listFilter(List<Method> listMethod,Boolean isMatcher,Action action){
        return listMethod.stream().filter(m -> {
            QQListener listener = m.getAnnotation(QQListener.class);
            if (isMatcher){
                return !listener.matcher().isEmpty() && action == listener.action();
            }
            return listener.matcher().isEmpty() && listener.action() == action;
        }).toList();
    }

    private boolean invoke(QQBotEvent event, Object obj, Method m, Matcher matcher) {
        try {
            m.invoke(obj, loadParams(m, event, matcher));
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
