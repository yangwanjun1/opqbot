package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileData {
    @JsonProperty("CgiCmd")
    private String CgiCmd = "PicUp.DataUp";
    @JsonProperty("CgiRequest")
    private CgiRequest CgiRequest;
}
