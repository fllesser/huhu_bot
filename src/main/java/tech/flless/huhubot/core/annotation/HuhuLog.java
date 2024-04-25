package tech.flless.huhubot.core.annotation;

import java.lang.annotation.*;

/**
 * @author FLLess7
 * @date 24/4/2024
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuhuLog {

    String[] params() default {};
}
