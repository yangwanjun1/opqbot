package io.github.yangwanjun1.constants;

public enum SendType {
    SEND_MSG("MessageSvc.PbSendMsg"),
    OPEN_REDBAG("OpenREDBAG"),
    DATA_UP_FILE("PicUp.DataUp"),
    SSO_GROUP_OP("SsoGroup.Op"),
    SYSTEM_GROUP("SystemMsgAction.Group"),
    QUERY_UIN("QueryUinByUid"),
    FRIEND_LIST("GetFriendLists"),
    GROUP_REMOVE_MSG("GroupRevokeMsg"),
    GROUP_LIST("GetGroupLists"),
    ;

    private final String type ;
    SendType(String type) {
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
