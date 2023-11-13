package io.github.yangwanjun1.constants;

public enum OptionType {
    /**
     * 同意
     */
    GROUP_AGREE(2),
    /**
     * 拒绝
     */
    GROUP_REFUSE(1),
    /**
     * 忽略
     */
    GROUP_IGNORE(3),
    /**
     * 拒绝
     */
    FRIEND_REFUSE(5),
    /**
     * 发送好友图片
     */
    FRIEND_IMAGE(1),
    /**
     * 发送群图片
     */
    GROUP_IMAGE(2),
    /**
     * 发送好友语音(暂未支持)
     */
    FRIEND_VOICE(26),
    /**
     * 发送群语音(暂未支持)
     */
    GROUP_VOICE(29),
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
