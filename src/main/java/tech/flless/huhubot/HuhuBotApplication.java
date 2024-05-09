package tech.flless.huhubot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({"tech.flless.huhubot.config", "tech.flless.huhubot.plugins"})
public class HuhuBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuhuBotApplication.class, args);
    }

}
