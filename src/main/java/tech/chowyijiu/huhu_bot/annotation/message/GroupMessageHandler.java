package tech.chowyijiu.huhu_bot.annotation.message;

import org.springframework.core.annotation.AliasFor;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(type = MessageTypeEnum.group)
public @interface GroupMessageHandler {

    //@AliasFor(
    //        annotation = MessageHandler.class
    //)
    //int weight(); //

    @AliasFor(
            annotation = MessageHandler.class
    )
    String name();

    @AliasFor(
            annotation = MessageHandler.class
    )
    String[] command() default {};
}