package ywj.gz.cn.constants;

import lombok.Getter;

@Getter
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
     * 发送好友图片
     */
    FRIEND_IMAGE(1),
    /**
     * 发送私聊图片
     */
    TEMPORARILY_IMAGE(3),
    /**
     * 发送群图片
     */
    GROUP_IMAGE(2),
    /**
     * 发送好友语音
     */
    FRIEND_VOICE(26),
    /**
     * 发送群语音
     */
    GROUP_VOICE(29),
    /**
     * 同意
     */
    FRIEND_AGREE(3),
    NONE(-1),
    COMMAND_ID(71),

    EXIT_GROUP(4247),
    REMOVE_GROUPER(2208),
    BEN_GROUPER(4691);

    private final int type;

    OptionType(int type) {
        this.type = type;
    }

}
