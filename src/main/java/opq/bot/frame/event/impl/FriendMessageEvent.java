package opq.bot.frame.event.impl;

import opq.bot.frame.constants.SourceType;

public class FriendMessageEvent extends PrivateMsgEventSuper {
    @Override
    public int getType() {
        return SourceType.FRIEND.getType();
    }
}
