package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    @JsonProperty("AdminUid")
    private String adminUid; //处理人Uid
    @JsonProperty("Uid")
    private String uid;   //进群者Uid  //退群者Uid
    @JsonProperty("Invitee")
    private String invitee;//被邀请人Uin
    @JsonProperty("Invitor")
    private String invitor;//邀请人Uin
    @JsonProperty("Tips")
    private String tips;
}
