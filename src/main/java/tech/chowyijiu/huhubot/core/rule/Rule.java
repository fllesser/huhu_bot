package tech.chowyijiu.huhubot.core.rule;

import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.ws.Bot;

/**
 * @author elastic chow
 * @date 10/6/2023
 */
@FunctionalInterface
public interface Rule {
    boolean check(Bot bot, Event event);
}

