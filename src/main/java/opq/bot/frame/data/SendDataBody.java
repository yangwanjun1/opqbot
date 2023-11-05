package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendDataBody {
    @JsonProperty("CgiCmd")
    private String cgiCmd = "MessageSvc.PbSendMsg";
    @JsonProperty("CgiRequest")
    private CgiRequest cgiRequest;
}
