package ywj.gz.cn.event;

import ywj.gz.cn.body.pojo.MessageData;

/**
 * 拦截器，仅拦截聊天类
 */
public abstract class MessageIntercept {
    /**
     * 消息进来之前 true:拦截
     */
    public boolean preHandler(MessageData event) {
        return false;
    }
}
