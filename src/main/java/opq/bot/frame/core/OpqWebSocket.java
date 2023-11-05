package opq.bot.frame.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import opq.bot.frame.event.OpqListenerEvent;
import opq.bot.frame.event.WsListenerEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.ApplicationContext;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class OpqWebSocket {
    private ApplicationContext context;
    @Getter
    private static String host;
    @Getter
    private  WebSocketClient client;
    public void setContext(ApplicationContext context) {
        this.context = context;
        host = client.getURI().getHost()+":"+client.getURI().getPort();
    }

    public void init(String ws) throws URISyntaxException {
            client = new WebSocketClient(new URI(ws)){
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                log.info("connected successful");
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
                log.info("connected close try connected:{}",reason);
                context.publishEvent(new WsListenerEvent(client));
            }

            @Override
            public void onError(Exception ex) {
                log.info("An error occurred:{}",ex.getMessage());
            }
        };
    }
}
