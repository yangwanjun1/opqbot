package opq.bot.frame.core;

import lombok.extern.slf4j.Slf4j;
import opq.bot.frame.annotation.Opq;
import opq.bot.frame.annotation.OpqListener;
import opq.bot.frame.constants.SourceType;
import opq.bot.frame.event.OpqMessageEvent;
import opq.bot.frame.event.impl.GroupMessageEvent;
import opq.bot.frame.event.impl.FriendMessageEvent;
import opq.bot.frame.event.impl.RedBagMessageEvent;
import opq.bot.frame.event.impl.TemporarilyMessageEvent;
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
                if (Objects.isNull(listener)) {
                    continue;
                }
                Class<? extends OpqMessageEvent> type = listener.type();
                //防止其他未知事件
                if (FriendMessageEvent.class.equals(type)) {
                    if (!listenerEventMap.containsKey(SourceType.FRIEND)) {
                        listenerEventMap.put(SourceType.FRIEND, new HashMap<>());
                    }
                    add(SourceType.FRIEND, obj, method);
                } else if (GroupMessageEvent.class.equals(type)) {
                    if (!listenerEventMap.containsKey(SourceType.GROUP)) {
                        listenerEventMap.put(SourceType.GROUP, new HashMap<>());
                    }
                    add(SourceType.GROUP, obj, method);
                } else if (TemporarilyMessageEvent.class.equals(type)) {
                    if (!listenerEventMap.containsKey(SourceType.TEMPORARILY)) {
                        listenerEventMap.put(SourceType.TEMPORARILY, new HashMap<>());
                    }
                    add(SourceType.TEMPORARILY, obj, method);
                } else if (RedBagMessageEvent.class.equals(type)) {
                    if (!listenerEventMap.containsKey(SourceType.MONEY)) {
                        listenerEventMap.put(SourceType.MONEY, new HashMap<>());
                    }
                    add(SourceType.MONEY, obj, method);
                }
            }
        });
    }
    private void add(SourceType fromType, Object obj, Method method){
        Map<Object, List<Method>> map = listenerEventMap.get(fromType);
        List<Method> list = map.getOrDefault(obj, new ArrayList<>());
        list.add(method);
        map.put(obj,list);
        listenerEventMap.put(fromType,map);
    }
    public static Map<Object,List<Method>> getEvent(SourceType fromType){
        return listenerEventMap.getOrDefault(fromType,Collections.emptyMap());
    }
}
