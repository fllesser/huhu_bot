package tech.chowyijiu.huhu_bot.core.rule;

import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.entity.response.GroupMember;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 27/6/2023
 */
public class RuleImpl {

    //弃用, 使用会产生浪费的计算
    //@Deprecated
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
            //这里本来准备就取缓存, 毕竟群主不可能也变来变去吧
            //但好像使用缓存, 一直没有响应数据, gocq那边有问题
            GroupMember groupMember = bot.getGroupMember(groupMessageEvent.getGroupId(), bot.getUserId(), true);
            return "owner".equals(groupMember.getRole());
        } else return false;
    }

    public static boolean selfAdmin(Bot bot, Event event) {
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            GroupMember groupMember = bot.getGroupMember(groupMessageEvent.getGroupId(), bot.getUserId(), true);
            String role = groupMember.getRole();
            return "admin".equals(role) || "owner".equals(role);
        } else return false;
    }

}
