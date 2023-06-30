package tech.chowyijiu.huhu_bot.annotation;

import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;

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

    //commands 和 keywords只能指定其中一个
    String[] commands() default {}; //命令前缀匹配, event的message字段会自动去除触发的command和前后空白符
    String[] keywords() default {}; //关键词匹配

    //1. 如果不指定可以在插件类中定义一个属性(引用为Rule, 名称为要加规则的方法名+"Rule") = (bot, event) -> {};
    //示例: Rule testRule = (bot, event) -> { return false;}
    //2. 可以在注解中指定RuleEnum中已有的规则
    RuleEnum rule() default RuleEnum.default_;

    //放弃
    //int cutdown() default 0;    // cd 单位 s
    //String cdMsg() default "";

}
