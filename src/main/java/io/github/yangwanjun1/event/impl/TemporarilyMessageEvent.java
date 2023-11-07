package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.constants.SourceType;

/**
 * 临时消息
 */
public class TemporarilyMessageEvent extends PrivateMsgEventSuper {

    @Override
    public int getType() {
        return SourceType.TEMPORARILY.getType();
    }
}
