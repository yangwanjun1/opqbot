package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.constants.SendType;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.*;
import io.github.yangwanjun1.event.OpqMessageEvent;
import io.github.yangwanjun1.utils.OpqUtils;

/**
 * 红包事件
 */
public class RedBagMessageEvent extends OpqMessageEvent {

    public RedBagMessageEvent(EventData eventData,long selfId) {
        super(eventData,selfId, false);
    }

    public ResultData openRedBag(){
        this.getRedBag().setFromType(SourceType.ON_RED_BAG.getType());
        RedBody obj = new RedBody(SendType.OPEN_REDBAG.getType(),this.getRedBag());
        ResultData result = sendMsg(getSelfId(), OpqUtils.toJsonString(obj), ResultData.class);
        if (this.getRedBag().getRedType() == SourceType.RED_BAG.getType()) {//口令红包
            sendMsg(getSelfId(), OpqUtils.msgBody(SourceType.GROUP.getType(),this.getRedBag().getWishing(),getGroup().getGroupCode(),null,null), ResultData.class);
        }
        return result;
    }
}
