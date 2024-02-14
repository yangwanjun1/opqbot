package ywj.gz.cn.core.ws;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ywj.gz.cn.config.QQConfigProperties;
import ywj.gz.cn.core.BotThreadPoll;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.core.EventHandlerAdapter;
import ywj.gz.cn.core.handler.QQEventHandler;

import java.util.Objects;

@Slf4j
public class WsServerSocket extends TextWebSocketHandler {
    private final QQEventHandler eventHandler;
    public WsServerSocket(QQConfigProperties properties, BotThreadPoll threadPoll, CompressImage compress, CacheImage cacheImage) {
        this.eventHandler = new QQEventHandler(properties,compress,cacheImage,threadPoll);
    }

    /**
     * 接收消息
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,@NonNull TextMessage message) throws Exception {
        eventHandler.handler(message.getPayload(), EventHandlerAdapter.getContext(),session);
    }
    /**
     * 连接成功
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("{} client connected ...", Objects.requireNonNull(session.getRemoteAddress()).getAddress());
    }

    /**
     * 连接关闭
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("{} client close code={}",Objects.requireNonNull(session.getRemoteAddress()).getAddress(),status.getCode());
    }

    /**
     * 异常处理
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("client {} connect error,msg:{}",Objects.requireNonNull(session.getRemoteAddress()).getAddress(),exception.getMessage());
    }

}
