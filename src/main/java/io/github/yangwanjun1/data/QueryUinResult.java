package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryUinResult {
    @JsonProperty("CgiBaseResponse")
    private CgiBaseResponse cgiBaseResponse;
    @JsonProperty("ResponseData")
    private UserData responseData;
}

