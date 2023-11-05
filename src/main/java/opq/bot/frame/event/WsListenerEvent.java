package opq.bot.frame.event;

import org.springframework.context.ApplicationEvent;

public class WsListenerEvent extends ApplicationEvent {
    public WsListenerEvent(Object source) {
        super(source);
    }
}
