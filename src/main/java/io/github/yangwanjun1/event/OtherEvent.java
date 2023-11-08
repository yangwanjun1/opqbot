package io.github.yangwanjun1.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.ResultData;
import io.github.yangwanjun1.utils.OpqUtils;
import io.github.yangwanjun1.data.UserData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public abstract class OtherEvent implements OpqRequest {
    private final Long groupId; //被邀请人qq
    private final Long selfId; //被邀请人qq

    public UserData request(String uid){
        String body = OpqUtils.toJsonString(OpqUtils.queryUin(uid));
        try {
            String query = sendMsg(selfId, body, String.class);
            JsonNode node = OpqUtils.getMapper().readTree(query).get("ResponseData").get(0);
            if (Objects.isNull(node)){
                return null;
            }
            return  OpqUtils.toBean(node.toString(), UserData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送群消息
     */
    public void sendGroupMsg(String content){
        String body = OpqUtils.msgBody(SourceType.GROUP.getType(),content,groupId,null,null);
        sendMsg(getSelfId(), body, ResultData.class);
    }
}
