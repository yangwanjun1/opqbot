package opq.bot.frame.event;

import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.springframework.context.ApplicationEvent;

@Getter
public class WsListenerEvent extends ApplicationEvent{
    private WebSocketClient client;
    public WsListenerEvent(Object source) {
        super(source);
        client = (WebSocketClient) source;
    }

}
