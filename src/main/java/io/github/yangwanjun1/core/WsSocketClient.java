package io.github.yangwanjun1.core;

import io.github.yangwanjun1.event.OpqListenerEvent;
import jakarta.websocket.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URI;

@Log4j2
@ClientEndpoint
public class WsSocketClient {

    private final ApplicationContext context;
    @Setter
    private String welcome;
    @Getter
    private static String host;
    @Getter
    private final URI uri;
    @Getter
    private Session session;

    public WsSocketClient(URI serverUri, String welcome, ApplicationContext context){
        this.welcome = welcome;
        this.context = context;
        this.uri = serverUri;
        host = uri.getHost()+":"+uri.getPort();
    }
    private void init() throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this,uri);
    }

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        log.info("{}",welcome);
    }

    @OnMessage
    public void onMessage(String message,Session session){
        if (EventHandlerAdapter.eventIsEmpty()){
            return;
        }
        context.publishEvent(new OpqListenerEvent(message));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason){
    }

    @OnError
    public void onError(Throwable e){
        log.error("连接出错，{}",e.getMessage());
    }

}