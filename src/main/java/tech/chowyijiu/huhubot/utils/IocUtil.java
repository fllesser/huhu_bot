package tech.chowyijiu.huhubot.utils;

import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
