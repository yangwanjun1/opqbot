package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedBody {
    @JsonProperty("CgiCmd")
    private String cgiCmd;
    @JsonProperty("CgiRequest")
    private RedBag cgiRequest;

    public RedBody(String type, RedBag redBag) {
        this.cgiCmd = type;
        this.cgiRequest = redBag;
    }
}
