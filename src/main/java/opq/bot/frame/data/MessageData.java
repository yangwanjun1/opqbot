package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageData {
    @JsonProperty("CurrentPacket")
    private CurrentPacket currentPacket;
    @JsonProperty("CurrentQQ")
    private long currentQQ;
}
