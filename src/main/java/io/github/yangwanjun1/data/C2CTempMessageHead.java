package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class C2CTempMessageHead {
    @JsonProperty("C2CType")
    private Integer c2CType;
    @JsonProperty("GroupUin")
    private Integer groupUin;
    @JsonProperty("GroupCode")
    private Integer groupCode;
    @JsonProperty("Sig")
    private String  sig;
}
