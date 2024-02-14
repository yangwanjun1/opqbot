package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBody {
    @JsonProperty("CgiBaseResponse")
    private CgiBaseResponse cgiBaseResponse;
    @JsonProperty("Data")
    private Map<Object,Object> data;
}
