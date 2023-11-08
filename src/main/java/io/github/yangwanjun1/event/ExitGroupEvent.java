package io.github.yangwanjun1.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.yangwanjun1.data.UserData;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 退群事件
 */
@EqualsAndHashCode(callSuper=false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ExitGroupEvent extends OtherEvent{
    @JsonProperty("Uid")
    private String uid; //被邀请人uid
    private UserData userInfo;//退群人信息


    public ExitGroupEvent(String uid, long groupId, long currentQQ) {
        super(groupId,currentQQ);
        this.uid = uid;
    }

    public UserData getUserInfo() {
        if (userInfo == null) {
            userInfo = request(uid);
        }
        return userInfo;
    }


}
