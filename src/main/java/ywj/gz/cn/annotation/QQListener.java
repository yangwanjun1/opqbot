package ywj.gz.cn.annotation;

import ywj.gz.cn.constants.Action;
import ywj.gz.cn.event.QQBotEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QQListener {
    /**
     * 监听的事件类型
     */
    Class<? extends QQBotEvent> type();

    /**
     * 正则表达式 空时默认关闭正则
     */
    String matcher() default "";

    /**
     * 是否为At类消息
     */
    Action action() default Action.NONE;
}
