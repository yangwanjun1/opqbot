package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CgiBaseResponse {
    @JsonProperty("Ret")
    private Long ret; //-1 失败 0 成功
    @JsonProperty("ErrMsg")
    private String errMsg;
}
