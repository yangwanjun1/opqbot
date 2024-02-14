package ywj.gz.cn.constants;

import lombok.Getter;

/**
 * 相关事件类型，具体查看文档
 */
@Getter
public enum SourceType {
    RED_BAG(12),
    NONE(-1),
    MONEY(0),//红包
    FRIEND(1),//好友
    GROUP(2),//群聊
    NOTICE(5),
    FRIEND_REQUEST(11),//好友请求事件
    TEMPORARILY(3),//临时聊天事件
    FROM_FRIEND(166),
    FROM_GROUP(82),
    FROM_REMOVE(34),    //退群踢人事件
    FROM_INVITE(33),    //邀请加群事件
    FROM_IN_GROUP(732); //进群事件

    private final int type;

    SourceType(int type) {
        this.type = type;
    }

}
