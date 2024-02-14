package ywj.gz.cn.body.send;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ywj.gz.cn.constants.SendType;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMsgBody {
    @JsonProperty("CgiCmd")
    private String cgiCmd;
    @JsonProperty("CgiRequest")
    private Object cgiRequest;
    public SendMsgBody(SendType type, Object cgiRequest) {
        this.cgiCmd = type.getType();
        this.cgiRequest = cgiRequest;
    }
}
