package tech.chowyijiu.huhu_bot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tech.chowyijiu.huhu_bot.constant.ANSI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author elastic chow
 * @date 22/5/2023
 */

@Slf4j
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    public static List<Long> superUsers = new ArrayList<>(1);
    public static List<Character> commandPrefixes = new ArrayList<>(1);

    public void setSuperUsers(List<Long> superUsers) {
        BotConfig.superUsers = superUsers;
        log.info("{}[BotConfig] super-users: {}{}", ANSI.YELLOW,
                Arrays.toString(superUsers.toArray()), ANSI.RESET);
    }

    public void setCommandPrefixes(List<Character> commandPrefixes) {
        BotConfig.commandPrefixes = commandPrefixes;
        log.info("{}[BotConfig] command-prefixes: {}{}", ANSI.YELLOW,
                Arrays.toString(commandPrefixes.toArray()), ANSI.RESET);
    }

    public static boolean isSuperUser(Long userId) {
        return superUsers.contains(userId);
    }
}
