package com.github.huhubot.core.annotation;

import java.lang.annotation.*;

/**
 * @author flless
 * @date 7/9/2023
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CoolDown {
    int seconds() default 60; // cd 单位 s
    String msg() default "";
}
