package tech.chowyijiu.huhu_bot.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Component
public class IocUtil {

    private static ApplicationContext applicationContext;

    @Resource
    public void setIoc(ApplicationContext applicationContext) {
        IocUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }


}
