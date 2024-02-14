package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ywj.gz.cn.body.pojo.User;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryUserBody extends ResponseBody{
    @JsonProperty("ResponseData")
    private List<User> responseData;
}
