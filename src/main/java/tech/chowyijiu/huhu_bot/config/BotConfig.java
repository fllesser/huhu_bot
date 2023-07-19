package tech.chowyijiu.huhu_bot.config;

import jakarta.annotation.PostConstruct;
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
    public static String aliRefreshToken;
    public static Long testGroup;

    public void setSuperUsers(List<Long> superUsers) {
        BotConfig.superUsers = superUsers;
    }

    public void setCommandPrefixes(List<Character> commandPrefixes) {
        BotConfig.commandPrefixes = commandPrefixes;
    }

    public void setAliRefreshToken(String aliRefreshToken) {
        BotConfig.aliRefreshToken = aliRefreshToken;
    }

    public void setTestGroup(Long testGroup) {
        BotConfig.testGroup = testGroup;
    }

    @PostConstruct
    public void postLog() {
        log.info("{}[BotConfig] {}{}", ANSI.YELLOW, this, ANSI.RESET);
    }

    @Override
    public String toString() {
        return "super-users: " + Arrays.toString(superUsers.toArray()) +
                ", command-prefix: " + Arrays.toString(commandPrefixes.toArray()) +
                ", test-group: " + testGroup +
                ", ali-refresh-token: " + aliRefreshToken;
    }

    public static boolean isSuperUser(Long userId) {
        return superUsers.contains(userId);
    }
}
