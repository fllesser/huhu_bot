package com.github.huhubot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({"com.github.huhubot.config"})
public class HuhuBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuhuBotApplication.class, args);
    }

}
