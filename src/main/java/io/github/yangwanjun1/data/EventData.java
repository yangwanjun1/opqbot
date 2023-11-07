
package io.github.yangwanjun1.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventData {
    @JsonProperty("MsgHead")
    private MsgHead msgHead;
    //戳一戳等事件msgBody为null
    @JsonProperty("MsgBody")
    private MsgBody msgBody;
    @JsonProperty("Event")
    private Event event;
    @JsonProperty("Content")
    private String content; //内容
    @JsonProperty("Uin")
    private Long uin;   //账号
    @JsonProperty("Nick")
    private String nick; //昵称
    @JsonProperty("EventName")
    private String eventName;
    @JsonProperty("Status")
    private Long status; //1未处理2已加入或待审核3已拒绝4忽略
    @JsonProperty("SrcId")
    private Long srcId;
    @JsonProperty("Src")
    private String src;//账号查找 手机查找 昵称查找等
    @JsonProperty("ReqUid")
    private String reqUid;
    @JsonProperty("ReqTime")
    private Long reqTime;
    @JsonProperty("GroupCode")
    private Long groupCode;
    @JsonProperty("MsgAdditional")
    private String msgAdditional; //备注
    @JsonProperty("ReqUidNick")
    private String reqUidNick;
    @JsonProperty("MsgType")
    private Integer msgType;//MsgType 1 申请进群 2 被邀请进群 13退出群聊 15取消管理员 3设置管理员 13退出群聊 针对管理员群主的推送事件
    @JsonProperty("InvitorUidNick")
    private String invitorUidNick;
    @JsonProperty("ActorUid")
    private String actorUid;
    @JsonProperty("ActorUidNick")
    private String actorUidNick;
    @JsonProperty("GroupName")
    private String groupName;
    @JsonProperty("InvitorUid")
    private String invitorUid;
    @JsonProperty("MsgSeq")
    private Long msgSeq;
}