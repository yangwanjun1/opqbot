package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClusterInfoBody extends ResponseBody{
    @JsonProperty("ResponseData")
    private ClusterInfo clusterInfo;
}
