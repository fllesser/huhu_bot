package tech.chowyijiu.huhu_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan({"tech.chowyijiu.huhu_bot.config", "tech.chowyijiu.huhu_bot.plugins"})
//@EnableAspectJAutoProxy
public class HuhuBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuhuBotApplication.class, args);
    }

}
