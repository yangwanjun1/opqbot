package ywj.gz.cn.body.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 群成员
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupData {
    @JsonProperty("CreditLevel")
    private Integer creditLevel;
    @JsonProperty("JoinTime")
    private Long joinTime;
    @JsonProperty("LastSpeakTime")
    private Long lastSpeakTime;
    @JsonProperty("Level")
    private Integer level;
    /**
     * 0成员 1群主 2管理员
     */
    @JsonProperty("MemberFlag")
    private Integer memberFlag;
    @JsonProperty("Nick")
    private String nick;
    @JsonProperty("Uid")
   private String uid;
    @JsonProperty("Uin")
   private Long uin;
}
