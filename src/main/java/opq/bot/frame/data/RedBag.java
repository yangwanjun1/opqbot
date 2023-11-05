package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedBag {
    /**
     * 个人发红包时，该消息体为空，需要封装
     */
    @JsonProperty("Wishing")
    private String wishing; //红包祝福语(转账时为金额，私人红包时redbag为空)
    @JsonProperty("Des")
    private String des; //红包状态 （已转入你的余额）
    @JsonProperty("RedType")
    private int redType; //红包类型
    @JsonProperty("Listid")
    private String listid;
    @JsonProperty("Authkey")
    private String authkey;
    @JsonProperty("Channel")
    private int channel;
    @JsonProperty("StingIndex")
    private String stingIndex;
    @JsonProperty("TransferMsg")
    private String transferMsg;//官方备注  QQ转账  QQ红包
    @JsonProperty("Token_17_2")
    private String token_17_2;
    @JsonProperty("Token_17_3")
    private String token_17_3;
    @JsonProperty("FromUin")
    private long fromUin;
    @JsonProperty("FromType")
    private int fromType;//消息来原  0
}