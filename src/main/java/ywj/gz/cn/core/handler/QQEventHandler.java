package ywj.gz.cn.core.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.WebSocketSession;
import ywj.gz.cn.body.pojo.EventData;
import ywj.gz.cn.body.pojo.MessageData;
import ywj.gz.cn.config.QQConfigProperties;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.core.*;
import ywj.gz.cn.design.EventFactory;
import ywj.gz.cn.event.MessageIntercept;
import ywj.gz.cn.event.QQBotEvent;

import java.util.Map;

public class QQEventHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final QQConfigProperties properties;
    private final CompressImage compress;
    private final CacheImage cacheImage;
    private Map<String, MessageIntercept> beans;
    private final BotThreadPoll threadPoll;
    public QQEventHandler(QQConfigProperties properties, CompressImage compress, CacheImage cacheImage, BotThreadPoll threadPoll) {
        this.properties = properties;
        this.compress = compress;
        this.cacheImage = cacheImage;
        this.threadPoll = threadPoll;
    }

    /**
     * 服务端socket
     */
    public void handler(String message, ApplicationContext context, @NonNull WebSocketSession session) {
        try {
            MessageData data = mapper.readValue(message, MessageData.class);
            Host.noContainsKey(data.getCurrentQQ(),session,properties);
            handlerMessage(data,context);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 客户端socket
     */
    public void handler(String message, ApplicationContext context) {
        try {
            handlerMessage(mapper.readValue(message, MessageData.class),context);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理消息
     */
    public void handlerMessage(MessageData message, ApplicationContext context) {
        EventData eventData = message.getCurrentPacket().getEventData();
        // 过滤机器人消息
        if (properties.getFilterBot() && message.getCurrentQQ() == eventData.getMsgHead().getSenderUin()) {
            return;
        }
        SourceType fromType = judge(eventData);
        QQBotEvent event = EventFactory.instance(fromType,cacheImage,compress,message);
        if (event == null){
            return;
        }
        beans = beans == null ? context.getBeansOfType(MessageIntercept.class) : beans;
        if (!messageIntercept(message) && !EventHandlerAdapter.getEvent(fromType).isEmpty()) {
            threadPoll.execute(new ExecRunnable(event,EventHandlerAdapter.getEvent(fromType),fromType));
        }
    }
    /**
     * 拦截器
     */
    public boolean messageIntercept(MessageData message){
        for (MessageIntercept value : beans.values()) {
            if (value.preHandler(message)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断事件类型
     */
    public SourceType judge(EventData eventData){
        if (eventData.getMsgHead() == null){
            Integer status = eventData.getStatus();
            Integer msgType = eventData.getMsgType();
            if (status == null || status != 1) {
                return SourceType.NONE;
            }
            if (msgType == null){
                return SourceType.FRIEND_REQUEST;
            }
            return eventData.getUin() == null ? SourceType.NOTICE : SourceType.NONE;
        }
        Integer msgType = eventData.getMsgHead().getMsgType();
        SourceType sourceType = EventFactory.convertType(msgType);
        SourceType fromType = EventFactory.convertType(eventData.getMsgHead().getFromType());
        if (eventData.getEvent() != null){
            if (sourceType == SourceType.FROM_INVITE && fromType == SourceType.GROUP) {
                return SourceType.FROM_INVITE;
            }
            if (sourceType == SourceType.FROM_REMOVE && fromType == SourceType.GROUP) {
                return SourceType.FROM_REMOVE;
            }
        }
        if (eventData.getMsgBody() == null){
            return SourceType.NONE;
        }
        if (eventData.getMsgBody().getRedBag() != null){
            return SourceType.RED_BAG;
        }
        return fromType;
    }


}
