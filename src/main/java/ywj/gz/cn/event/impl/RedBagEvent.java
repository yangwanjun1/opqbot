package ywj.gz.cn.event.impl;

import lombok.Getter;
import ywj.gz.cn.body.pojo.*;
import ywj.gz.cn.body.receive.CommonlyResponseBody;
import ywj.gz.cn.body.receive.ResponseData;
import ywj.gz.cn.body.send.SendMsgBody;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.event.QQBotEvent;

import java.util.Map;

/**
 * 当前版本私人红包为空，私人转账时有记录
 */
@Getter
public class RedBagEvent extends QQBotEvent {
    private final GroupInfo group;//群信息
    private final RedBag redBag;//红包
    private final String senderUid;//消息来源uid
    private final Long senderUin;
    private final SourceType type;//红包来源 好友 群
    public RedBagEvent(MessageData message, CompressImage compress, CacheImage cacheImage) {
        super(message.getCurrentQQ(),message.getCurrentPacket().getEventData().getMsgHead().getMsgUid(),compress,cacheImage);
        EventData data = message.getCurrentPacket().getEventData();
        this.group = data.getMsgHead().getGroupInfo();
        this.senderUid = data.getMsgHead().getSenderUid();
        this.senderUin = data.getMsgHead().getSenderUin();
        this.redBag = data.getMsgBody().getRedBag();
        type = group == null ? SourceType.FRIEND : SourceType.GROUP;
    }
    public User getUserInfo() {
        return queryUserInfo(senderUid);
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public void setContent(String text) {

    }

    /**
     * 打开群组红包(当前仅支持 手气红包 和 口令红包)
     */
    public ResponseData openRedBag(){
        if (type == SourceType.FRIEND){
            return new ResponseData();
        }
        redBag.setFromType(SourceType.FRIEND.getType());
        CommonlyResponseBody send = send(Map.of("CgiCmd","OpenREDBAG","CgiRequest",redBag), CommonlyResponseBody.class);
        if (redBag.getRedType() == SourceType.RED_BAG.getType()){//口令红包
            sendMsg(redBag.getWishing());
        }
        return send.getResponseData();
    }

    public void sendMsg(String content){
        SendMsgBody body = super.msgBody(SourceType.GROUP.getType(), content, getGroup().getGroupCode(), null,null,null);
        send(body);
    }
    @Override
    public OptionType getOptionType() {
        return OptionType.NONE;
    }

}
