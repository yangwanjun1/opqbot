package ywj.gz.cn.body.receive;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendBody extends ResponseBody{

    @JsonProperty("ResponseData")
    private FriendData responseData;

}
