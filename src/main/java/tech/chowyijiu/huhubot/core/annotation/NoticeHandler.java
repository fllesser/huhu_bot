package tech.chowyijiu.huhubot.core.annotation;

import tech.chowyijiu.huhubot.core.rule.RuleEnum;

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
    int priority() default 5;   //默认按方法定义顺序匹配 0~9, 值越小, 优先级越高

    //1. 如果不指定可以在插件类中定义一个属性(引用为Rule, 名称为要加规则的方法名+"Rule"), 使用lambda表达式赋值
    //示例: Rule testRule = (bot, event) -> { return false;}
    //2. 可以在注解中指定RuleEnum中已有的规则
    RuleEnum rule() default RuleEnum.default_;
}
