package tech.chowyijiu.huhu_bot.core.rule;

import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupMember;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 27/6/2023
 */
public class RuleImpl {

    public static boolean tome(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent) return ((GroupMessageEvent) event).isToMe();
        else return false;
    }

    public static boolean superuser(Bot bot, Event event) {
        if (event instanceof MessageEvent) return BotConfig.isSuperUser(((MessageEvent) event).getUserId());
        else return false;
    }

    public static boolean owner(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent)
            return "owner".equals(((GroupMessageEvent) event).getSender().getRole());
        else return false;
    }

    public static boolean admin(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent) {
            String role = ((GroupMessageEvent) event).getSender().getRole();
            return "admin".equals(role) || "owner".equals(role) || superuser(bot, event);
        } else return false;
    }

    public static boolean selfOwner(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            //这里就取缓存了, 毕竟群主不可能也变来变去吧
            GroupMember groupMember = bot.getGroupMember(groupMessageEvent.getGroupId(), bot.getUserId(), false);
            return "owner".equals(groupMember.getRole());
        } else return false;
    }

    public static boolean selfAdmin(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            GroupMember groupMember = bot.getGroupMember(groupMessageEvent.getGroupId(), bot.getUserId(), true);
            String role = groupMember.getRole();
            return "admin".equals(role) || "owner".equals(role);
        } else return false;
    }
}
