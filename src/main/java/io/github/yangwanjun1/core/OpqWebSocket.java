package io.github.yangwanjun1.core;

import io.github.yangwanjun1.event.OpqListenerEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.ApplicationContext;

import java.net.URI;

@Slf4j
public class OpqWebSocket extends WebSocketClient {
    private ApplicationContext context;
    @Setter
    private String welcome;
    @Getter
    private static String host;

    public OpqWebSocket(URI serverUri, Draft_6455 draft6455, String welcome, ApplicationContext context) {
        super(serverUri,draft6455);
        this.welcome = welcome;
        this.context = context;
        host = getURI().getHost()+":"+getURI().getPort();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("{}",welcome);
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
        log.error("连接出错，{}",ex.getMessage());
    }
}

