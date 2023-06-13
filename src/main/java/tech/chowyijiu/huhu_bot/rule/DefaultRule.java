package tech.chowyijiu.huhu_bot.rule;

import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 10/6/2023
 */
public class DefaultRule implements Rule {

    public static final DefaultRule instance = new DefaultRule();

    private DefaultRule() {

    }

    @Override
    public boolean check(Bot bot, Event event) {
        return true;
    }
}