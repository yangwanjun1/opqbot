package opq.bot.frame.annotation;

import opq.bot.frame.constants.Action;
import opq.bot.frame.event.OpqMessageEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpqListener {

    Class<? extends OpqMessageEvent> type();
    String matcher() default "";
    Action action() default Action.NONE;
}
