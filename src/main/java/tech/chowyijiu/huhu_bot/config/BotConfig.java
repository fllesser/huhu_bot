package tech.chowyijiu.huhu_bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author elastic chow
 * @date 22/5/2023
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    //todo 改为静态变量, 和集合类

    private Long[] superUsers = {};
    private Character[] commandPrefixes = {};

    public boolean isSuperUser(Long userId) {
        return Arrays.asList(superUsers).contains(userId);
    }
}
