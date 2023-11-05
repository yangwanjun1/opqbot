package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileBody {
    @JsonProperty("FileId")
    private Long fileId;
    @JsonProperty("FileMd5")
    private String fileMd5;
    @JsonProperty("FileSize")
    private Long fileSize;
    @JsonProperty("Url")
    private String url;
    @JsonProperty("Height")
    private Integer height;
    @JsonProperty("Width")
    private Integer width;
}
