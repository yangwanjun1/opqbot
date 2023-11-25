package io.github.yangwanjun1.core;

import io.github.yangwanjun1.event.Bot;
import io.github.yangwanjun1.event.OpqListenerEvent;
import io.github.yangwanjun1.utils.OpqUtils;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
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

    @Resource(name = "botManager")
    private BotManager botManager;
    private static Integer port;
    public static void setPort(int reversePort){
        port = reversePort;
    }
    public static String getHost(Long qq){
        return host.get(qq);
    }
    private void addHost(@NonNull WebSocketSession session,Long qq){
        if (!host.containsKey(qq)) {
            String address = Objects.requireNonNull(session.getRemoteAddress()).getAddress().getHostAddress();
            String ip = address + ":" + port;
            host.put(qq, ip);
            botManager.add(qq,new Bot(qq,ip,session));
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
        ApplicationContext context = EventHandlerAdapter.getContext();
        if (context != null) {
            context.publishEvent(new OpqListenerEvent(payload));
        }
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

    private void removeId(WebSocketSession session){
        long qq = botManager.removeSession(session);
        host.remove(qq);
    }
}
