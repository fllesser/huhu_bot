package tech.chowyijiu.huhu_bot.config;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
public class MyJsonConfig {

    public static final SerializeConfig jsonConfig = new SerializeConfig();

    static {
        jsonConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }
}
