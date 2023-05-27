package tech.chowyijiu.huhu_bot.annotation;

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

    //int cutdown() default 0;    // cd 单位 s
    //todo 自定义cd消息
    //String cdMsg() default "";
}
