package io.github.yangwanjun1.event;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.yangwanjun1.constants.OptionType;
import io.github.yangwanjun1.data.CgiRequest;
import io.github.yangwanjun1.data.UserData;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.Getter;

import java.util.Map;

/**
 * 好友请求事件
 */
@Getter
public class FriendRequestEvent extends OtherEvent {
    private final String msgAdditional;//备注 [问题1:你是谁？\n回答:四个零 或者是验证消息，可自己判断]
    private final Long reqTime;//添加时间，改时间为秒
    private final String reqUid;//请求人的uid
    private final String src;//来源
    private final String srcId;//来源id
    private UserData requester;
    public UserData getRequester() {
        if (requester == null) {
            requester = request(reqUid);
        }
        return requester;
    }
    public FriendRequestEvent(JsonNode jsonNodeEvent, long currentQQ) {
        super(jsonNodeEvent.get("GroupCode").asLong(),currentQQ);
        this.msgAdditional = jsonNodeEvent.get("MsgAdditional").asText();
        this.reqTime = jsonNodeEvent.get("ReqTime").asLong();
        this.reqUid = jsonNodeEvent.get("ReqUid").asText();
        this.src = jsonNodeEvent.get("Src").asText();
        this.srcId = jsonNodeEvent.get("SrcId").asText();
    }

    public void handlerFriendRequest(OptionType opCode){
        CgiRequest cgiRequest = new CgiRequest();
        cgiRequest.setOpCode(opCode.getType());
        cgiRequest.setReqUid(reqUid);
        Map<String, Object> map = Map.of("CgiCmd","SystemMsgAction.Friend","CgiRequest",cgiRequest);
        sendMsg(getSelfId(), OpqUtils.toJsonString(map),String.class);
    }

}
