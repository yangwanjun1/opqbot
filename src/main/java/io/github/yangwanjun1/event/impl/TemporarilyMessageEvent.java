package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.EventData;

/**
 * 临时消息
 */
public class TemporarilyMessageEvent extends PrivateMsgEventSuper {

    public TemporarilyMessageEvent(EventData eventData, long currentQQ) {
        super(eventData,currentQQ);
    }

    @Override
    public int getType() {
        return SourceType.TEMPORARILY.getType();
    }
}
