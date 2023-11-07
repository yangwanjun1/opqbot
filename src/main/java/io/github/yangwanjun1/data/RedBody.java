package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedBody {
    @JsonProperty("CgiCmd")
    private String CgiCmd = "OpenREDBAG";
    @JsonProperty("CgiRequest")
    private RedBag CgiRequest;
}
