package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GroupRequestBody {
    @JsonProperty("CgiCmd")
    private String cgiCmd = "SystemMsgAction.Group";
    @JsonProperty("CgiRequest")
    private CgiRequest cgiRequest;
}
