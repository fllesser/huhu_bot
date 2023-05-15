package tech.chowyijiu.huhu_bot.annotation.notice;

import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;

import java.lang.annotation.*;

/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoticeHandler {

    NoticeTypeEnum type();
    SubTypeEnum subType() default SubTypeEnum.ignore; //如果, type为notify, 必须指定subtype
    //int weight() default 0; 按方法定义顺序匹配
    String name() default "";

}
