package opq.bot.frame.event.impl;

import opq.bot.frame.constants.SourceType;

/**
 * 临时消息
 */
public class TemporarilyMessageEvent extends PrivateMsgEventSuper {

    @Override
    public int getType() {
        return SourceType.TEMPORARILY.getType();
    }
}
