package com.github.huhubot.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author elastic chow
 * @date 22/5/2023
 */

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    private List<Long> superUsers;
    private List<Character> commandPrefixes = new ArrayList<>(1);
    private Long testGroup;


    @PostConstruct
    public void postLog() throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        for (Field f : this.getClass().getDeclaredFields()) {
            if ("Logger".equals(f.getType().getSimpleName())) continue;
            sb.append(" ").append(f.getName()).append(": ").append(f.get(this));
        }
        log.info("[BotConfig]{}", sb);
    }


    public boolean isSuperUser(Long userId) {
        return superUsers != null && !superUsers.isEmpty() && superUsers.contains(userId);
    }
}
