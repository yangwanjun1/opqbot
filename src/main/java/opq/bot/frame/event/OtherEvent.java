package opq.bot.frame.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import opq.bot.frame.core.OpqRequest;
import opq.bot.frame.data.UserData;
import opq.bot.frame.utils.OpqUtils;
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
