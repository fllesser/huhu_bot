package tech.chowyijiu.huhu_bot.annotation;

import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 2/7/2023
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RuleV2 {
    String value() default "";
    RuleEnum rule() default RuleEnum.default_;
}
