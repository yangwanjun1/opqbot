package opq.bot.frame.constants;

/**
 * 相关事件类型，具体查看文档
 */
public enum SourceType {
    RED_BAG(12),
    NONE(-1),
    MONEY(0),
    FRIEND(1),
    GROUP(2),
    ON_RED_BAG(1),
    TEMPORARILY(3),
    FROM_FRIEND(166),
    FROM_GROUP(82),
    FROM_REMOVE(34),    //退群踢人事件
    FROM_INVITE(33),    //邀请加群事件
    FROM_IN_GROUP(732); //进群事件

    private final int type;

    SourceType(int type) {
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
