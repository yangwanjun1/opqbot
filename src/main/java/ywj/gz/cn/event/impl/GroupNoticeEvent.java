package ywj.gz.cn.event.impl;

import lombok.Getter;
import ywj.gz.cn.body.pojo.*;
import ywj.gz.cn.body.receive.CommonlyResponseBody;
import ywj.gz.cn.body.send.CgiRequest;
import ywj.gz.cn.body.send.SendMsgBody;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SendType;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.event.QQBotEvent;

/**
 * 群通知事件
 */
@Getter
public class GroupNoticeEvent extends QQBotEvent {
    /**
     * 事件类型 1:申请进群 2:被邀请进群 13:退出群聊(针对管理员群主的推送事件) 15:取消管理员 3:设置管理员
     * 当前推送事件仅为未处理的群通知信息，如果一直未处理会把所有未处理的事件推送过来
     * 可自己过略
     */
    private final int eventType;
    private final String groupName;
    private final String actorUid;
    private final String actorUidNick; //邀请人昵称
    private final String msgAdditional;//备注信息
    private final Long msgSeq;
    private final String reqUid;
    private final String reqUidNick; //被邀请的昵称
    private final Long groupCode; // 群号
    public GroupNoticeEvent(MessageData message) {
        super(message.getCurrentQQ(),message.getCurrentPacket().getEventData().getMsgHead().getMsgUid(),null,null);
        EventData data = message.getCurrentPacket().getEventData();
        this.groupName = data.getGroupName();
        this.eventType = data.getMsgType();
        this.actorUid = data.getActorUid();
        this.actorUidNick = data.getActorUidNick();
        this.msgAdditional = data.getMsgAdditional();
        this.msgSeq = data.getMsgSeq();
        this.reqUid = data.getReqUid();
        this.reqUidNick = data.getReqUidNick();
        this.groupCode = data.getGroupCode();
    }
    public User getActorInfo() {
        return queryUserInfo(actorUid);
    }
    public User getReqUidInfo() {
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
     * 根据事件类型进行处理
     * 如果 eventType群可管理事件（即【bot为管理员可处理加群申请，否则仅可以处理被邀请事件】），
     */
    public CommonlyResponseBody handlerNotice(OptionType opCode){
        CgiRequest request = new CgiRequest();
        request.setOpCode(opCode.getType());
        request.setMsgSeq(msgSeq);
        request.setMsgType(OptionType.GROUP_AGREE.getType());
        request.setGroupCode(groupCode);
        SendMsgBody body = new SendMsgBody(SendType.SYSTEM_GROUP, request);
        return send(body, CommonlyResponseBody.class);
    }
    @Override
    public OptionType getOptionType() {
        return OptionType.GROUP_IMAGE;
    }
}