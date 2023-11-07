package io.github.yangwanjun1.event;

import org.springframework.context.ApplicationEvent;

public final class OpqListenerEvent extends ApplicationEvent {
    public OpqListenerEvent(Object source) {
        super(source);
    }
}
