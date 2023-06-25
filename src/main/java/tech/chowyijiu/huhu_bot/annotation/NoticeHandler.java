package tech.chowyijiu.huhu_bot.annotation;

import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;

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
    //NoticeTypeEnum type();    //取消支持
    int priority() default 9;   //默认按方法定义顺序匹配 0~9, 值越小, 优先级越高

    //int cutdown() default 0;    // 单位 s
    //String cdMsg() default "";
    RuleEnum rule() default RuleEnum.default_;
}
