package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    @JsonProperty("MsgTime")
    private Long msgTime;
    @JsonProperty("GetMoney")
    private Double getMoney;
    @JsonProperty("TotalCount")
    private String totalCount;
    @JsonProperty("TotalMoney")
    private Long totalMoney;
    @JsonProperty("FileMd5")
    private String fileMd5;
    @JsonProperty("FileSize")
    private Long fileSize;
    @JsonProperty("FileToken")
    private String fileToken;

    @JsonProperty("FileId")
    private Long fileId;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Uid")
    private String  uid;
    @JsonProperty("Uin")
    private String uin;
}
