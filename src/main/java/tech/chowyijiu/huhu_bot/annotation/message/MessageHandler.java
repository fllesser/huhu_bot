package tech.chowyijiu.huhu_bot.annotation.message;

import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageHandler {

    MessageTypeEnum type() default MessageTypeEnum.all;
    //int weight() default 0; 按方法定义顺序匹配
    String name() default "";
    String[] command();

}
