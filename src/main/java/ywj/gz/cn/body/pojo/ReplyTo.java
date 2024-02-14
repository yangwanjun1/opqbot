package ywj.gz.cn.body.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReplyTo {
    @JsonProperty("MsgSeq")
    private Long msgSeq;
    @JsonProperty("MsgTime")
    private Long msgTime;
    @JsonProperty("MsgUid")
    private Long msgUid;
}
