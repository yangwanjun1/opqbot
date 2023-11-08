package io.github.yangwanjun1.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.yangwanjun1.data.UserData;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 进群事件
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class InviteHandlerEvent extends OtherEvent {

    @JsonProperty("Invitee")
    private String invitee; //被邀请人uid
    @JsonProperty("Invitor")
    private String invitor;//邀请人uid
    @JsonProperty("Tips")
    private String tips;
    private UserData inviteeInfo;
    private UserData invitorInfo;

    public UserData getInviteeInfo() {
        if (inviteeInfo == null) {
            inviteeInfo = request(invitee);
        }
        return inviteeInfo;
    }

    public UserData getInvitorInfo() {
        if (invitorInfo == null) {
            invitorInfo = request(invitor);
        }
        return invitorInfo;
    }


    public InviteHandlerEvent(JsonNode eventBody, long currentQQ, long groupId) {
        super(groupId,currentQQ);
        this.invitor = eventBody.get("Uid").asText();
        this.invitee = eventBody.get("AdminUid").asText();
    }
}
