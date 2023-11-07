package io.github.yangwanjun1.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.utils.OpqUtils;
import io.github.yangwanjun1.data.UserData;

import java.util.Objects;

public abstract class OtherEvent implements OpqRequest {

    protected abstract long getSelfId();

    public UserData request(String uid){
        String body = OpqUtils.toJsonString(OpqUtils.queryUin(uid));
        try {
            String query = sendMsg(getSelfId(), body, String.class);
            JsonNode node = OpqUtils.getMapper().readTree(query).get("ResponseData").get(0);
            if (Objects.isNull(node)){
                return null;
            }
            return  OpqUtils.toBean(node.toString(), UserData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
