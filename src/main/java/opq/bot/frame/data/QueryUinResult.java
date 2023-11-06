package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryUinResult {
    @JsonProperty("CgiBaseResponse")
    private CgiBaseResponse cgiBaseResponse;
    @JsonProperty("ResponseData")
    private UserData responseData;
}


