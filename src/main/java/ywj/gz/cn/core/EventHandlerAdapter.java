package ywj.gz.cn.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import ywj.gz.cn.annotation.QQBot;
import ywj.gz.cn.annotation.QQListener;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.event.QQBotEvent;
import ywj.gz.cn.event.impl.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class EventHandlerAdapter implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;
    private static Map<SourceType,Map<Object, List<Method>>> listenerEventMap;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        listenerEventMap = new ConcurrentHashMap<>();
        Map<Class<? extends QQBotEvent>,SourceType> map = Map.of(
                FriendEvent.class,SourceType.FRIEND,
                GroupEvent.class,SourceType.GROUP,
                TemporarilyEvent.class,SourceType.TEMPORARILY,
                RedBagEvent.class,SourceType.RED_BAG,
                InviteEvent.class,SourceType.FROM_INVITE,
                ExitGroupEvent.class,SourceType.FROM_REMOVE,
                GroupNoticeEvent.class,SourceType.NOTICE,
                RequestEvent.class,SourceType.FRIEND_REQUEST
        );
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(QQBot.class);
        beans.forEach((key, obj) -> {
            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                QQListener listener = method.getAnnotation(QQListener.class);
                if (Objects.nonNull(listener) && map.containsKey(listener.type())) {
                    initLoad(map.get(listener.type()),obj,method);
                }
            }
        });
    }
    private void initLoad(SourceType type, Object obj, Method method){
        Map<Object, List<Method>> map = listenerEventMap.getOrDefault(type,new HashMap<>());
        List<Method> list = map.getOrDefault(obj, new ArrayList<>());
        list.add(method);
        map.put(obj,list);
        listenerEventMap.put(type,map);
    }
    public static Map<Object,List<Method>> getEvent(SourceType fromType){
        return listenerEventMap.getOrDefault(fromType,Collections.emptyMap());
    }

}
