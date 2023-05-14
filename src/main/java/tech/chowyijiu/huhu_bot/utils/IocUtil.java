package tech.chowyijiu.huhu_bot.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Component
public class IocUtil {

    private static ApplicationContext ioc;

    @Resource
    public void setIoc(ApplicationContext ioc) {
        IocUtil.ioc = ioc;
    }

    public static <T> T getBean(Class<T> clazz) {
        return ioc.getBean(clazz);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return ioc.getBeansWithAnnotation(clazz);
    }



}
