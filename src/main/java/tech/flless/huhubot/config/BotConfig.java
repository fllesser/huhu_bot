package tech.flless.huhubot.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author elastic chow
 * @date 22/5/2023
 */

@Slf4j
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    public static List<Long> superUsers;
    public static List<Character> commandPrefixes = new ArrayList<>(1);
    public static Long testGroup;


    public void setSuperUsers(ArrayList<Long> superUsers) {
        BotConfig.superUsers = superUsers;
    }

    public void setCommandPrefixes(ArrayList<Character> commandPrefixes) {
        BotConfig.commandPrefixes = commandPrefixes;
    }

    public void setTestGroup(Long testGroup) {
        BotConfig.testGroup = testGroup;
    }

    @PostConstruct
    public void postLog() throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        for (Field f : this.getClass().getDeclaredFields()) {
            if ("Logger".equals(f.getType().getSimpleName())) continue;
            sb.append(" ").append(f.getName()).append(": ").append(f.get(this));
        }
        log.info("[BotConfig]{}", sb);
    }

    public static boolean isSuperUser(Long userId) {
        return superUsers.contains(userId);
    }
}
