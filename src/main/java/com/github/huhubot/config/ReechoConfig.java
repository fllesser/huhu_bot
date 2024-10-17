package com.github.huhubot.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "reecho")
public class ReechoConfig {

    private String apiKey;
    private String webToken;

    public String authorization() {
        return "Bearer " + apiKey;
    }

}
