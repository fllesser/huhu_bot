package tech.chowyijiu.huhu_bot.rule;

import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 10/6/2023
 */
@FunctionalInterface
public interface Rule {
    boolean check(Bot bot, Event event);
}

