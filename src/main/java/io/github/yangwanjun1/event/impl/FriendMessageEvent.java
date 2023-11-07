package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.constants.SourceType;

public class FriendMessageEvent extends PrivateMsgEventSuper {
    @Override
    public int getType() {
        return SourceType.FRIEND.getType();
    }
}
