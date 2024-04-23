package tech.flless.huhubot.core.rule;

import tech.flless.huhubot.adapters.onebot.v11.event.Event;

/**
 * @author elastic chow
 * @date 10/6/2023
 */
@FunctionalInterface
public interface Rule {
    boolean check(Event event);
}

