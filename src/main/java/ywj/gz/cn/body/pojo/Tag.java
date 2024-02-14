package ywj.gz.cn.body.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {
    @JsonProperty("TagId")
    private Integer tagId;
    @JsonProperty("TagName")
    private String tagName;
}
