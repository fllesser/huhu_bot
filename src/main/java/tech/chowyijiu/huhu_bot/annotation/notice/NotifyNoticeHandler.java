package tech.chowyijiu.huhu_bot.annotation.notice;

import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotifyNoticeHandler {

    String name() default "";
    SubTypeEnum subType();      //如果, type为notify, 必须指定subtype
    int priority() default 0;   //默认按方法定义顺序匹配 0~9, 值越小, 优先级越高
}
