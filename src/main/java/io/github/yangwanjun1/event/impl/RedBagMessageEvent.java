package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.*;
import io.github.yangwanjun1.event.OpqMessageEvent;
import io.github.yangwanjun1.utils.OpqUtils;

/**
 * 红包事件
 */
public class RedBagMessageEvent extends OpqMessageEvent {

    public RedBagMessageEvent(EventData eventData,long selfId,long senderUin) {
        super(eventData,selfId);
    }

    public OnRedResult openRedBag(){
        this.getRedBag().setFromType(SourceType.ON_RED_BAG.getType());
        RedBody obj = new RedBody();
        obj.setCgiRequest(this.getRedBag());
        OnRedResult result = sendMsg(getSelfId(), OpqUtils.toJsonString(obj), OnRedResult.class);
        if (this.getRedBag().getRedType() == SourceType.RED_BAG.getType()) {
            sendMsg(getSelfId(), OpqUtils.msgBody(SourceType.GROUP.getType(),this.getRedBag().getWishing(),getGroup().getGroupCode(),null,null), ResultData.class);
        }
        return result;
    }
}
