package ywj.gz.cn.event.impl;

import lombok.Getter;
import ywj.gz.cn.body.pojo.EventData;
import ywj.gz.cn.body.pojo.MessageData;
import ywj.gz.cn.body.pojo.User;
import ywj.gz.cn.body.send.CgiRequest;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SendType;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.event.QQBotEvent;

import java.util.Map;

/**
 * 好友添加请求事件
 */
@Getter
public class RequestEvent extends QQBotEvent {
    private final String msgAdditional;//备注 [问题1:你是谁？\n回答:四个零 或者是验证消息，可自己判断]
    private final Long reqTime;//添加时间，单位：秒
    private final String reqUid;//请求人的uid
    private final String src;//来源
    private final Long srcId;//来源id

    public RequestEvent(MessageData message) {
        super(message.getCurrentQQ(),message.getCurrentPacket().getEventData().getMsgHead().getMsgUid(),null,null);
        EventData data = message.getCurrentPacket().getEventData();
        this.msgAdditional = data.getMsgAdditional();
        this.reqTime = data.getReqTime();
        this.reqUid = data.getReqUid();
        this.src = data.getSrc();
        this.srcId = data.getSrcId();
    }
    public User getUserInfo() {
        return queryUserInfo(reqUid);
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public void setContent(String text) {

    }

    /**
     * 处理好友请求
     */
    public void handlerFriendRequest(OptionType opCode){
        CgiRequest cgiRequest = new CgiRequest();
        cgiRequest.setOpCode(opCode.getType());
        cgiRequest.setReqUid(reqUid);
        Map<String, Object> map = Map.of("CgiCmd", SendType.HANDLER_FRIEND_REQUEST.getType(),"CgiRequest",cgiRequest);
        send(map);
    }
    @Override
    public OptionType getOptionType() {
        return OptionType.NONE;
    }
}
