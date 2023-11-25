package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgBody {

    //12 Xml消息 19 Video消息 51 JSON卡片消息
    @JsonProperty("SubMsgType")
    private int subMsgType;
    @JsonProperty("Content")
    private String content;
    @JsonProperty("AtUinLists")
    private List<AtUinLists> atUinLists;
    @JsonProperty("Images")
    private List<FileBody> images;
    @JsonProperty("RedBag")
    private RedBag redBag;
    @JsonProperty("Video")
    private FileBody video;
    @JsonProperty("Voice")
    private FileBody voice;
    @JsonProperty("File")
    private FileData file;
}
