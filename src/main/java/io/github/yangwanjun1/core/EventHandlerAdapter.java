package io.github.yangwanjun1.core;

import io.github.yangwanjun1.annotation.Opq;
import io.github.yangwanjun1.annotation.OpqListener;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.event.ExitGroupEvent;
import io.github.yangwanjun1.event.FriendRequestEvent;
import io.github.yangwanjun1.event.GroupNoticeEvent;
import io.github.yangwanjun1.event.InviteHandlerEvent;
import io.github.yangwanjun1.event.impl.FriendMessageEvent;
import io.github.yangwanjun1.event.impl.GroupMessageEvent;
import io.github.yangwanjun1.event.impl.RedBagMessageEvent;
import io.github.yangwanjun1.event.impl.TemporarilyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class EventHandlerAdapter implements ApplicationContextAware {
    private static Map<SourceType,Map<Object, List<Method>>> listenerEventMap;

    public static boolean eventIsEmpty(){
        return listenerEventMap.isEmpty();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        listenerEventMap = new ConcurrentHashMap<>();
        Map<Class<? extends OpqRequest>,SourceType> map = Map.of(
                FriendMessageEvent.class,SourceType.FRIEND,
                GroupMessageEvent.class,SourceType.GROUP,
                TemporarilyMessageEvent.class,SourceType.TEMPORARILY,
                RedBagMessageEvent.class,SourceType.MONEY,
                InviteHandlerEvent.class,SourceType.FROM_INVITE,
                ExitGroupEvent.class,SourceType.FROM_REMOVE,
                GroupNoticeEvent.class,SourceType.NOTICE,
                FriendRequestEvent.class,SourceType.FRIEND_REQUEST
        );
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Opq.class);
        beans.forEach((key, obj) -> {
            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                OpqListener listener = method.getAnnotation(OpqListener.class);
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
