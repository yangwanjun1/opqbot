package opq.bot.frame.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import opq.bot.frame.constants.SourceType;
import opq.bot.frame.data.ResultData;
import opq.bot.frame.data.UserData;
import opq.bot.frame.utils.OpqUtils;

/**
 * 进群事件
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InviteHandlerEvent extends OtherEvent {
    @JsonProperty("Invitee")
    private String invitee; //被邀请人uid
    private Long selfId; //被邀请人qq
    private Long groupId; //被邀请人qq
    @JsonProperty("Invitor")
    private String invitor;//邀请人uid
    @JsonProperty("Tips")
    private String tips;
    private UserData inviteeInfo;
    private UserData invitorInfo;

    /**
     * 发送群消息
     */
    public ResultData sendGroupMsg(String content){
        String body = OpqUtils.msgBody(SourceType.GROUP.getType(),content,groupId,null,null);
        return sendMsg(getSelfId(),body, ResultData.class);
    }
    public UserData getInviteeInfo() {
        if (inviteeInfo == null) {
            inviteeInfo = request(invitee);
        }
        return inviteeInfo;
    }

    public UserData getInvitorInfo() {
        if (invitorInfo == null) {
            invitorInfo = request(invitor);
        }
        return invitorInfo;
    }

    @Override
    protected long getSelfId() {
        return selfId;
    }
}
