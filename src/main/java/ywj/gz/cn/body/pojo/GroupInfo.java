package ywj.gz.cn.body.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * QQ群信息
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupInfo {
    @JsonProperty("GroupCard")
    private String groupCard;
    @JsonProperty("GroupCode")
    private Long groupCode;
    @JsonProperty("GroupInfoSeq")
    private int groupInfoSeq;
    @JsonProperty("GroupLevel")
    private int GroupLevel;
    @JsonProperty("GroupRank")
    private int groupRank;
    @JsonProperty("GroupType")
    private int groupType;
    @JsonProperty("GroupName")
    private String groupName;
}
