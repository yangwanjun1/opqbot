package ywj.gz.cn.body.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Files {
    @JsonProperty("FileId")
    private Long fileId;
    @JsonProperty("PathId")
    private String pathId;
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
    @JsonProperty("FileName")
    private String fileName;
    @JsonProperty("Tips")
    private String tips;
}
