package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CgiBaseResponse {
    @JsonProperty("Ret")
    private Long ret;
    @JsonProperty("ErrMsg")
    private String errMsg;
}
