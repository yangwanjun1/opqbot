package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QQUser {
    @JsonProperty("LevelInfo")
    private String levelInfo;
    @JsonProperty("MoneyCount")
    private String moneyCount;
    @JsonProperty("OnlieTime")
    private String onlieTime;
    @JsonProperty("QQ")
    private String qq;
    @JsonProperty("ReceiveCount")
    private Long receiveCount;
    @JsonProperty("SendCount")
    private Long sendCount;
    @JsonProperty("TotalMoney")
    private String totalMoney;
    @JsonProperty("TotalRecv")
    private String totalRecv;
    @JsonProperty("TotalSend")
    private String totalSend;
    @JsonProperty("Uid")
    private String uid;
    @JsonProperty("Uin")
    private Long uin;
}
