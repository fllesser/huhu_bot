package tech.flless.huhubot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author FLLess7
 * @date 22/1/2024
 */
@ConfigurationProperties("apispace")
public class ApiSpaceConfig {

    public static String token;

    public void setToken(String token) {
        ApiSpaceConfig.token = token;
    }
}
