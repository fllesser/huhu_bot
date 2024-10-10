package tech.flless.huhubot.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties(prefix = "reecho")
public class ReechoConfig {

    public static String apiKey;
    public static String webToken;

    public void setApiKey(String apiKey) {
        ReechoConfig.apiKey = apiKey;
    }

    public void setWebToken(String webToken) {
        ReechoConfig.webToken = webToken;
    }
}
