package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtUinLists {
    @JsonProperty("Nick")
    private String nick;
    @JsonProperty("Uid")
    private String uid;
    @JsonProperty("Uin")
    private long uin;//被at的人
    //{[{"Nick":"全体成员","Uid":"0","Uin":0}]}
}
