package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ywj.gz.cn.body.pojo.Friend;
import ywj.gz.cn.body.pojo.Tag;

import java.util.List;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendData {
    @JsonProperty("FriendLists")
    private List<Friend> friendLists;
    @JsonProperty("LastBuffer")
    private String lastBuffer;
    @JsonProperty("LastUin")
    private Long lastUin;
    @JsonProperty("TagLists")
    private List<Tag> tagLists;
}
