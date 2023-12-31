package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryUinResult {
    @JsonProperty("CgiBaseResponse")
    private CgiBaseResponse cgiBaseResponse;
    @JsonProperty("ResponseData")
    private List<UserData> responseData;
    @JsonProperty("Data")
    private Map<Object,Object> data;
}


