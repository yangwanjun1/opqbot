package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {
    @JsonProperty("Uin")
    private Long uin;
    @JsonProperty("Uid")
    private String uid;
    @JsonProperty("Nick")
    private String nick;
    @JsonProperty("Mark")
    private String mark;
    @JsonProperty("Head")
    private String head;
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("Sex")
    private Integer sex;
    @JsonProperty("Level")
    private Integer level;
}
