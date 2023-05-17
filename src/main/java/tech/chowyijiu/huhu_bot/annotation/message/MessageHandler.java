package tech.chowyijiu.huhu_bot.annotation.message;

import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageHandler {

    String name() default "";
    int priority() default 9;       //默认按方法定义顺序匹配 0~9, 值越小, 优先级越高
    boolean block() default false;  //默认不阻断
    String[] commands();

}
