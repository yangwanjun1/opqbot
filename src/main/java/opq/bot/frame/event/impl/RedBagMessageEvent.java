package opq.bot.frame.event.impl;

import opq.bot.frame.constants.SourceType;
import opq.bot.frame.data.*;
import opq.bot.frame.event.OpqMessageEvent;
import opq.bot.frame.utils.OpqUtils;

import static opq.bot.frame.utils.OpqUtils.msgBody;

public class RedBagMessageEvent extends OpqMessageEvent {

    public OnRedResult openRedBag(){
        this.getRedBag().setFromType(SourceType.ON_RED_BAG.getType());
        String body = OpqUtils.toJsonString(this.getRedBag());
        OnRedResult result = sendMsg(getSelfId(), body, OnRedResult.class);
        if (this.getRedBag().getRedType() == SourceType.RED_BAG.getType()) {
            sendMsg(getSelfId(),msgBody(SourceType.GROUP.getType(),this.getRedBag().getWishing(),getGroup().getGroupCode(),null,null),ResultData.class);
        }
        return result;
    }
}
