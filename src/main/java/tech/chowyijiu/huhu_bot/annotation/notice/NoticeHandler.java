package tech.chowyijiu.huhu_bot.annotation.notice;

import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoticeHandler {

    String name() default "";
    NoticeTypeEnum type();
    int priority() default 0; //默认按方法定义顺序匹配 0~9, 值越小, 优先级越高

}
