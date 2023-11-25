package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.yangwanjun1.constants.SendType;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMsgBody {
    @JsonProperty("CgiCmd")
    private String cgiCmd;
    @JsonProperty("CgiRequest")
    private CgiRequest cgiRequest;
    public SendMsgBody(SendType type, CgiRequest cgiRequest) {
        this.cgiCmd = type.getType();
        this.cgiRequest = cgiRequest;
    }
}
