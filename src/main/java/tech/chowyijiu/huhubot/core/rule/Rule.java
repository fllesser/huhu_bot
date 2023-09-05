package tech.chowyijiu.huhubot.core.rule;

import tech.chowyijiu.huhubot.core.event.Event;

/**
 * @author elastic chow
 * @date 10/6/2023
 */
@FunctionalInterface
public interface Rule {
    boolean check(Event event);
}

