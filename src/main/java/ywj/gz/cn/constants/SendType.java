package ywj.gz.cn.constants;

import lombok.Getter;

@Getter
public enum SendType {
    SEND_MSG("MessageSvc.PbSendMsg"),
    OPEN_RED_BAG("OpenREDBAG"),
    DATA_UP_FILE("PicUp.DataUp"),
    SSO_GROUP_OP("SsoGroup.Op"),
    SYSTEM_GROUP("SystemMsgAction.Group"),
    FRIEND_LIST("GetFriendLists"),
    GROUP_REMOVE_MSG("GroupRevokeMsg"),
    GROUP_LIST("GetGroupLists"),
    CLUSTER_INFO("ClusterInfo"),
    GROUP_MEMBER_LIST("GetGroupMemberLists"),
    QUERY_UIN_BY_UID("QueryUinByUid"),
    HANDLER_FRIEND_REQUEST("SystemMsgAction.Friend"),
    ;

    private final String type ;
    SendType(String type) {
        this.type = type;
    }
}
