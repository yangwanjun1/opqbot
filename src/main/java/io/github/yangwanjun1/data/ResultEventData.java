package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultEventData {
    @JsonProperty("CgiBaseResponse")
    private CgiBaseResponse cgiBaseResponse;
    @JsonProperty("ResponseData")
    private Integer responseData;
    @JsonProperty("Data")
    private Map<Object,Object> data;
}
