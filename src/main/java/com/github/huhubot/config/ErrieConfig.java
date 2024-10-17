package com.github.huhubot.config;


import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "errie")
public class ErrieConfig {

    private String clientId;
    private String clientSecret;

}
