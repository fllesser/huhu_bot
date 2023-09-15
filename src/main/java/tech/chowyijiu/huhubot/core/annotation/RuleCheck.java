package tech.chowyijiu.huhubot.core.annotation;

import tech.chowyijiu.huhubot.core.rule.RuleEnum;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 2/7/2023
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RuleCheck {
    RuleEnum rule();
}
