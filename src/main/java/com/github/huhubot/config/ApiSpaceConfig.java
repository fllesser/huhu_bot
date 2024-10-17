package com.github.huhubot.config;

import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author FLLess7
 * @date 22/1/2024
 */
@Data
@Component
@ConfigurationProperties("api-space")
public class ApiSpaceConfig {

    private String token;

}

