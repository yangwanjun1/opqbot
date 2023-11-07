package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryUinBody {
    @JsonProperty("CgiCmd")
    private String cgiCmd = "QueryUinByUid";
    @JsonProperty("CgiRequest")
    private CgiRequest cgiRequest;
}
