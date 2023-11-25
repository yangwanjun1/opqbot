package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Friend implements Serializable {
    @JsonProperty("Head")
    private String head;
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("TagId")
    private Integer tagId;
    @JsonProperty("Uid")
    private String uid;
    @JsonProperty("Uin")
    private Long uin;
    @JsonProperty("Nick")
    private String nick;
    @JsonProperty("Sex")
    private Integer sex;
}
