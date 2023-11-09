package io.github.yangwanjun1.event;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.yangwanjun1.constants.OptionType;
import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.CgiRequest;
import io.github.yangwanjun1.data.GroupRequestBody;
import io.github.yangwanjun1.data.ResultData;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.Getter;

/**
 * 群通知事件
 */
@Getter
public class GroupNoticeEvent implements OpqRequest {
    /**
     * 事件类型 1 申请进群 2 被邀请进群 13退出群聊(针对管理员群主的推送事件) 15取消管理员 3设置管理员
     * 当前推送事件仅为未处理的群通知信息，如果一直未处理会把所有未处理的事件推送过来
     * 可自己过略
     */
    private final int eventType;
    private final String groupName; //群名
    private final String actorUid; //邀请人的uid
    private final String actorUidNick; //邀请人昵称
    private final String msgAdditional;//备注信息
    private final Long msgSeq;
    private final String reqUid;//被邀请的uid
    private final String reqUidNick; //被邀请的昵称（一般为bot昵称）
    private final Long groupCode; // 群号
    private final long selfId; // 群号

    public GroupNoticeEvent(JsonNode jsonNodeEvent, JsonNode eventType,long selfId) {
        this.eventType = eventType.asInt();
        this.groupName = jsonNodeEvent.get("GroupName").asText();
        this.actorUid = jsonNodeEvent.get("ActorUid").asText();
        this.actorUidNick = jsonNodeEvent.get("ActorUidNick").asText();
        this.msgAdditional = jsonNodeEvent.get("MsgAdditional").asText();
        this.msgSeq = jsonNodeEvent.get("MsgSeq").asLong();
        this.reqUid = jsonNodeEvent.get("ReqUid").asText();
        this.reqUidNick = jsonNodeEvent.get("ReqUidNick").asText();
        this.groupCode = jsonNodeEvent.get("GroupCode").asLong();
        this.selfId = selfId;
    }

    /**
     * 根据事件类型进行处理
     * 如果 eventType群可管理事件（即【bot为管理员可处理加群申请，否则仅可以处理被邀请事件】），
     */
    public ResultData handlerNotice(OptionType opCode){
        GroupRequestBody body = new GroupRequestBody();
        body.setCgiRequest(new CgiRequest());
        body.getCgiRequest().setOpCode(opCode.getType());
        body.getCgiRequest().setMsgSeq(msgSeq);
        body.getCgiRequest().setMsgType(1);
        body.getCgiRequest().setGroupCode(groupCode);
        String string = OpqUtils.toJsonString(body);
        return sendMsg(selfId,string, ResultData.class);
    }
}
