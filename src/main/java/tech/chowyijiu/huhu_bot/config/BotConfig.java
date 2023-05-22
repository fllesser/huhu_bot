package tech.chowyijiu.huhu_bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author elastic chow
 * @date 22/5/2023
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    private Long[] superUsers;
    private String[] commandPrefixes;


}
