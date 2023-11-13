package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CgiRequest {

    /**
     * 好友id
     */
    @JsonProperty("ToUin")
    private Long toUin;
    /**
     * 聊天类型 1：朋友 2 ：群里 3：陌生人
     */
    @JsonProperty("ToType")
    private Integer toType;
    @JsonProperty("Notify")
    private Boolean notify;
    /**
     * 必须转义
     */
    @JsonProperty("Content")
    private String content;
    @JsonProperty("AtUinLists")
    private List<AtUinLists> atUinLists;
    @JsonProperty("Images")
    private List<FileBody> images;
    @JsonProperty("Voice")
    private FileBody voice;
    @JsonProperty("CommandId")
    private Integer commandId;
    @JsonProperty("FilePath")
    private String  filePath;
    @JsonProperty("FileName")
    private String  fileName;
    @JsonProperty("FileUrl")
    private String  fileUrl;
    @JsonProperty("Base64Buf")
    private String  base64Buf;
    @JsonProperty("OpCode")
    private Integer  opCode;
    @JsonProperty("Uin")
    private Long  uin;
    @JsonProperty("BanTime")
    private Integer  banTime;
    @JsonProperty("MsgSeq")
    private Long msgSeq;
    @JsonProperty("MsgRandom")
    private Long msgRandom;
    @JsonProperty("Uid")
    private String uid;
    @JsonProperty("ReqUid")
    private String reqUid;
    @JsonProperty("MsgType")
    private Integer msgType;
    @JsonProperty("GroupCode")
    private Long groupCode;
}
