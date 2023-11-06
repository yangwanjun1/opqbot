package opq.bot.frame.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import opq.bot.frame.constants.SourceType;
import opq.bot.frame.data.ResultData;
import opq.bot.frame.data.UserData;
import opq.bot.frame.utils.OpqUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ExitGroupEvent extends OtherEvent{
    @JsonProperty("Uid")
    private String uid; //被邀请人uid
    private UserData userInfo;
    private Long groupId; //被邀请人qq
    private Long selfId; //被邀请人qq
    public UserData getUserInfo() {
        if (userInfo == null) {
            userInfo = request(uid);
        }
        return userInfo;
    }
    /**
     * 发送群消息
     */
    public ResultData sendGroupMsg(String content){
        String body = OpqUtils.msgBody(SourceType.GROUP.getType(),content,groupId,null,null);
        return sendMsg(getSelfId(),body, ResultData.class);
    }

    @Override
    protected long getSelfId() {
        return selfId;
    }
}
