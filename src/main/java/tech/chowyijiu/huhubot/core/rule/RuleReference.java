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
public class RuleReference {

    public static boolean tome(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent gme) return gme.isToMe();
        return false;
    }

    public static boolean superuser(Bot bot, Event event) {
        if (event instanceof MessageEvent me) return BotConfig.isSuperUser(me.getUserId());
        else return false;
    }

    public static boolean owner(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent gme) return "owner".equals(gme.getSender().getRole());
        else return false;
    }

    public static boolean admin(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent gme) {
            String role = gme.getSender().getRole();
            return "admin".equals(role) || "owner".equals(role) || BotConfig.isSuperUser(gme.getUserId());
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
        if (event instanceof GroupMessageEvent gme) {
            GroupMember groupMember = bot.getGroupMember(
                    gme.getGroupId(), bot.getSelfId(), true);
            String role = groupMember.getRole();
            return "admin".equals(role) || "owner".equals(role);
        } else return false;
    }

}
