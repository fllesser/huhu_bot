package tech.chowyijiu.huhubot.core.rule;

import tech.chowyijiu.huhubot.config.BotConfig;
import tech.chowyijiu.huhubot.core.entity.response.GroupMember;
import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.event.message.GroupMessageEvent;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.ws.Bot;

/**
 * @author elastic chow
 * @date 27/6/2023
 */
public class RuleImpl {

    public static boolean tome(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent groupMessageEvent) return groupMessageEvent.isToMe();
        else return false;
    }

    public static boolean superuser(Bot bot, Event event) {
        if (event instanceof MessageEvent messageEvent) return BotConfig.isSuperUser(messageEvent.getUserId());
        else return false;
    }

    public static boolean owner(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent groupMessageEvent)
            return "owner".equals(groupMessageEvent.getSender().getRole());
        else return false;
    }

    public static boolean admin(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent) {
            String role = ((GroupMessageEvent) event).getSender().getRole();
            return "admin".equals(role) || "owner".equals(role) || superuser(bot, event);
        } else return false;
    }

    public static boolean selfOwner(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            //取缓存, 毕竟群主不可能也变来变去吧
            GroupMember groupMember = bot.getGroupMember(
                    groupMessageEvent.getGroupId(), bot.getSelfId(), false);
            return "owner".equals(groupMember.getRole());
        } else return false;
    }

    public static boolean selfAdmin(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            GroupMember groupMember = bot.getGroupMember(
                    groupMessageEvent.getGroupId(), bot.getSelfId(), true);
            String role = groupMember.getRole();
            return "admin".equals(role) || "owner".equals(role);
        } else return false;
    }

}
