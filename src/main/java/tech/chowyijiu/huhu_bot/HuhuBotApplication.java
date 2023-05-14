package tech.chowyijiu.huhu_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tech.chowyijiu.huhu_bot.dispenser.MessageDispenser;
import tech.chowyijiu.huhu_bot.utils.IocUtil;

@SpringBootApplication
public class HuhuBotApplication {


    public static void main(String[] args) {
        SpringApplication.run(HuhuBotApplication.class, args);
        MessageDispenser messageDispenser = IocUtil.getBean(MessageDispenser.class);
        messageDispenser.loadPlugin();
    }

}
