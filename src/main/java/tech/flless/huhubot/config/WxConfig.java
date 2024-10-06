package tech.flless.huhubot.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties(prefix = "wx")
public class WxConfig {

    public static String clientId;
    public static String clientSecret;

    public void setClientId(String clientId) {
        WxConfig.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        WxConfig.clientSecret = clientSecret;
    }
}
