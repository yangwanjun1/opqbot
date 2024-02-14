package ywj.gz.cn.body.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtUinLists {
    @JsonProperty("Nick")
    private String nick;
    @JsonProperty("Uid")
    private String uid;
    /**
     * 被at的人qq号
     */
    @JsonProperty("Uin")
    private long uin;
}
