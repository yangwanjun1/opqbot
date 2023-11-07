package io.github.yangwanjun1.annotation;

import io.github.yangwanjun1.constants.Action;
import io.github.yangwanjun1.core.OpqRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpqListener {

    Class<? extends OpqRequest> type();
    String matcher() default "";
    Action action() default Action.NONE;
}
