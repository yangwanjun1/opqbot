package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgHead {

    @JsonProperty("FromUin")
    private long fromUin;
    @JsonProperty("FromUid")
    private String fromUid;
    @JsonProperty("ToUin")
    private long toUin;
    @JsonProperty("ToUid")
    private String toUid;
    @JsonProperty("FromType")
    private int fromType;
    @JsonProperty("SenderUin")
    private long senderUin;
    @JsonProperty("SenderUid")
    private String senderUid;
    @JsonProperty("SenderNick")
    private String SenderNick;
    @JsonProperty("MsgType")
    private int msgType;
    @JsonProperty("C2cCmd")
    private int c2cCmd;
    @JsonProperty("MsgSeq")
    private long msgSeq;
    @JsonProperty("MsgTime")
    private long msgTime;
    @JsonProperty("MsgRandom")
    private long msgRandom;
    @JsonProperty("MsgUid")
    private long msgUid;
    @JsonProperty("GroupInfo")
    private GroupInfo groupInfo;

    @JsonProperty("C2CTempMessageHead")
    private String c2CTempMessageHead;

}