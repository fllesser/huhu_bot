package tech.chowyijiu.huhu_bot.core.rule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;

/**
 * @author elastic chow
 * @date 20/6/2023
 */
@Getter
@RequiredArgsConstructor
public enum RuleEnum {

    default_("default", (bot, event) -> true),
    tome("tome", (bot, event) -> {
        if (event instanceof GroupMessageEvent) return ((GroupMessageEvent) event).isToMe();
        else return false;
    }), //at 机器人
    superuser("superuser", (bot, event) -> {
        if (event instanceof MessageEvent) return BotConfig.isSuperUser(((MessageEvent) event).getUserId());
        else return false;
    }), //superuser
    owner("owner", (bot, event) -> {
        if (event instanceof GroupMessageEvent)
            return "owner".equals(((GroupMessageEvent) event).getSender().getRole());
        else return false;
    }), //群主
    admin("admin", (bot, event) -> {
        if (event instanceof GroupMessageEvent) {
            String role = ((GroupMessageEvent) event).getSender().getRole();
            return "admin".equals(role) || "owner".equals(role) || superuser.rule.check(bot, event);
        } else return false;
    }); //管理 or 群主 or superuser


    private final String name;
    private final Rule rule;
}
