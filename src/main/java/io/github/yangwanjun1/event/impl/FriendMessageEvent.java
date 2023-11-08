package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.EventData;

public class FriendMessageEvent extends PrivateMsgEventSuper {
    public FriendMessageEvent(EventData eventData, long currentQQ) {
        super(eventData,currentQQ);
    }

    @Override
    public int getType() {
        return SourceType.FRIEND.getType();
    }
}
