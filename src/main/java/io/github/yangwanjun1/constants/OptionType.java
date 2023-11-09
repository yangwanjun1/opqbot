package io.github.yangwanjun1.constants;

public enum OptionType {
    /**
     * 同意
     */
    GROUP_AGREE(1),
    /**
     * 拒绝
     */
    GROUP_REFUSE(2),
    /**
     * 忽略
     */
    GROUP_IGNORE(3),
    /**
     * 拒绝
     */
    FRIEND_REFUSE(5),
    /**
     * 同意
     */
    FRIEND_AGREE(3);
    private final int type;

    OptionType(int type) {
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
