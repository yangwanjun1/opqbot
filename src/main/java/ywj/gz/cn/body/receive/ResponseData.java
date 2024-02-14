package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ywj.gz.cn.body.pojo.Friend;
import ywj.gz.cn.body.pojo.Group;
import ywj.gz.cn.body.pojo.GroupData;
import ywj.gz.cn.body.pojo.Tag;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    @JsonProperty("MsgTime")
    private Long msgTime;
    @JsonProperty("GetMoney")
    private Double getMoney;//单位分
    @JsonProperty("TotalCount")
    private String totalCount;
    @JsonProperty("MsgSeq")
    private String msgSeq;
    @JsonProperty("TotalMoney")
    private Long totalMoney;
    @JsonProperty("FileMd5")
    private String fileMd5;
    @JsonProperty("FileSize")
    private Long fileSize;
    @JsonProperty("FileToken")
    private String fileToken;
    @JsonProperty("FileId")
    private Long  fileId;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Uid")
    private String  uid;
    @JsonProperty("Uin")
    private String uin;
    @JsonProperty("MemberLists")
    private List<GroupData> memberLists;
    @JsonProperty("FriendLists")
    private List<Friend> friendLists;
    @JsonProperty("LastBuffer")
    private String lastBuffer;
    @JsonProperty("TagLists")
    private List<Tag> tagLists;
    @JsonProperty("groupLists")
    private List<Group> GroupLists;
}
