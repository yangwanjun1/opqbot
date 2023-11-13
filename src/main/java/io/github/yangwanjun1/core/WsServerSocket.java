package io.github.yangwanjun1.core;

import io.github.yangwanjun1.event.Bot;
import io.github.yangwanjun1.event.OpqListenerEvent;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WsServerSocket extends TextWebSocketHandler {
    private static final Map<Long,String> host = new ConcurrentHashMap<>();
    private static final Map<WebSocketSession,Long> id = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Long, Bot> botManager = new ConcurrentHashMap<>();
    private static Integer port;
    public static void setPort(int reversePort){
        port = reversePort;
    }
    public static String getHost(Long qq){
        return host.get(qq);
    }
    private static void addHost(@NonNull WebSocketSession session,Long qq){
        if (!host.containsKey(qq)) {
            String address = Objects.requireNonNull(session.getRemoteAddress()).getAddress().getHostAddress();
            String ip = address + ":" + port;
            host.put(qq, ip);
            botManager.put(qq,new Bot(qq,ip));
            id.put(session,qq);
        }
    }
    /**
     * 接收消息
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,@NonNull TextMessage message) throws Exception {
        if (EventHandlerAdapter.eventIsEmpty()){
            return;
        }
        String payload = message.getPayload();
        long currentQQ = OpqUtils.getMapper().readTree(payload).get("CurrentQQ").asLong();
        addHost(session,currentQQ);
        EventHandlerAdapter.getContext().publishEvent(new OpqListenerEvent(payload));
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
        removeId(session);
    }

    /**
     * 异常处理
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("client {} connect error,msg:{}",Objects.requireNonNull(session.getRemoteAddress()).getAddress(),exception.getMessage());
        removeId(session);
    }

    private static void removeId(WebSocketSession session){
        Long qq = id.get(session);
        getBotManager().remove(qq);
        host.remove(qq);
    }
}
