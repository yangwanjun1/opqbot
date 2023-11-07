package io.github.yangwanjun1.core;

import lombok.extern.slf4j.Slf4j;
import io.github.yangwanjun1.annotation.Opq;
import io.github.yangwanjun1.annotation.OpqListener;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.event.ExitGroupEvent;
import io.github.yangwanjun1.event.InviteHandlerEvent;
import io.github.yangwanjun1.event.impl.GroupMessageEvent;
import io.github.yangwanjun1.event.impl.FriendMessageEvent;
import io.github.yangwanjun1.event.impl.RedBagMessageEvent;
import io.github.yangwanjun1.event.impl.TemporarilyMessageEvent;
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
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Opq.class);
        beans.forEach((key, obj) -> {
            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                OpqListener listener = method.getAnnotation(OpqListener.class);
                if (listener == null) {
                    continue;
                }
                Class<? extends OpqRequest> type = listener.type();
                //防止其他未知事件
                if (FriendMessageEvent.class.equals(type)) {
                    initLoad(SourceType.FRIEND, obj, method);
                } else if (GroupMessageEvent.class.equals(type)) {
                    initLoad(SourceType.GROUP, obj, method);
                } else if (TemporarilyMessageEvent.class.equals(type)) {
                    initLoad(SourceType.TEMPORARILY, obj, method);
                } else if (RedBagMessageEvent.class.equals(type)) {
                    initLoad(SourceType.MONEY,obj,method);
                } else if (InviteHandlerEvent.class.equals(type)) {
                    initLoad(SourceType.FROM_INVITE,obj,method);
                }else if (ExitGroupEvent.class.equals(type)){
                    initLoad(SourceType.FROM_REMOVE,obj,method);
                }
            }
        });
    }

    private void initLoad(SourceType type, Object obj, Method method){
        if (!listenerEventMap.containsKey(type)) {
            listenerEventMap.put(type, new HashMap<>());
        }
        add(type, obj, method);
    }
    private void add(SourceType fromType, Object obj, Method method){
        Map<Object, List<Method>> map = listenerEventMap.getOrDefault(fromType,new HashMap<>());
        List<Method> list = map.getOrDefault(obj, new ArrayList<>());
        list.add(method);
        map.put(obj,list);
        listenerEventMap.put(fromType,map);
    }
    public static Map<Object,List<Method>> getEvent(SourceType fromType){
        return listenerEventMap.getOrDefault(fromType,Collections.emptyMap());
    }
}
