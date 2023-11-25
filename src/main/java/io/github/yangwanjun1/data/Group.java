package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {
    @JsonProperty("CreateTime")
    private Long createTime;
    @JsonProperty("GroupCnt")
    private Long groupCnt;
    @JsonProperty("GroupCode")
    private Long groupCode;
    @JsonProperty("GroupName")
    private String groupName;
    @JsonProperty("MemberCnt")
    private Long memberCnt;
}
