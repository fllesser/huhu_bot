package tech.flless.huhubot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan({"tech.chowyijiu.huhubot.config", "tech.chowyijiu.huhubot.plugins"})
@EnableAspectJAutoProxy
public class HuhuBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuhuBotApplication.class, args);
    }

}
