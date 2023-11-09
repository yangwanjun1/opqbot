package io.github.yangwanjun1.event;

import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.QueryUinResult;
import io.github.yangwanjun1.data.ResultData;
import io.github.yangwanjun1.data.UserData;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Objects;

@AllArgsConstructor
@Getter
public abstract class OtherEvent implements OpqRequest {
    private final Long groupId; //被邀请人qq
    private final Long selfId; //被邀请人qq

    public UserData request(String uid){
        String body = OpqUtils.toJsonString(OpqUtils.queryUin(uid));
        QueryUinResult queryUinResult = sendMsg(selfId, body, QueryUinResult.class);
        if (Objects.isNull(queryUinResult) || queryUinResult.getCgiBaseResponse().getRet() != 0){
            return new UserData();
        }
        return queryUinResult.getResponseData().get(0);
    }
    /**
     * 发送群消息
     */
    public void sendGroupMsg(String content){
        Assert.isTrue(groupId!=0,"不存在的群");
        String body = OpqUtils.msgBody(SourceType.GROUP.getType(),content,groupId,null,null);
        sendMsg(getSelfId(), body, ResultData.class);
    }
}
