package opq.bot.frame.data;

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
    @JsonProperty("FileUrl")
    private String  fileUrl;
    @JsonProperty("Base64Buf")
    private String  base64Buf;
}
