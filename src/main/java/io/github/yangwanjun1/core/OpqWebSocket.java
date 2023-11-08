package io.github.yangwanjun1.core;

import io.github.yangwanjun1.event.OpqListenerEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.ApplicationContext;

import java.net.URI;

@Slf4j
public class OpqWebSocket extends WebSocketClient {
    private ApplicationContext context;
    @Getter
    private static String host;

    public void setContext(ApplicationContext context) {
        this.context = context;
        host = getURI().getHost()+":"+getURI().getPort();
    }

    public OpqWebSocket(URI serverUri,Draft_6455 draft6455) {
        super(serverUri,draft6455);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("opq连接成功");
    }

    @Override
    public void onMessage(String message) {
        if (EventHandlerAdapter.eventIsEmpty()){
            return;
        }
        context.publishEvent(new OpqListenerEvent(message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
    }
}

