package tech.chowyijiu.huhu_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HuhuBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuhuBotApplication.class, args);
    }

}
