package com.github.huhubot.core.rule;

import com.github.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import com.github.huhubot.adapters.onebot.v11.entity.response.GroupMember;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.message.PrivateMessageEvent;
import com.github.huhubot.adapters.onebot.v11.bot.Bot;
import com.github.huhubot.config.BotConfig;
import com.github.huhubot.config.GlobalConfig;

/**
 * @author elastic chow
 * @date 27/6/2023
 */
public class RuleRef {

    public static boolean tome(Event event) {
        if (event instanceof GroupMessageEvent gme) {
            return gme.isToMe() || gme.getRawMessage().contains(GlobalConfig.botCf.getNickName());
        }
        return false;
    }

    public static boolean toAll(Event event) {
        if (event instanceof GroupMessageEvent gme) {
            for (MessageSegment segment : gme.getMessage()) {
                if ("at".equals(segment.getType())) {
                    //这里就直接return吧, 毕竟不会有人又@全体又单独@谁
                    return "all".equals(segment.getString("qq"));
                }
            }
        }
        return false;
    }

    public static boolean superuser(Event event) {
        if (event instanceof MessageEvent me) return GlobalConfig.botCf.isSuperUser(me.getUserId());
        else return false;
    }

    public static boolean owner(Event event) {
        if (event instanceof GroupMessageEvent gme)
            return "owner".equals(gme.getSender().getRole());
        else return false;
    }

    public static boolean admin(Event event) {
        if (event instanceof GroupMessageEvent gme) {
            String role = gme.getSender().getRole();
            return "admin".equals(role) || "owner".equals(role) || GlobalConfig.botCf.isSuperUser(gme.getUserId());
        } else return false;
    }

    public static boolean selfOwner(Event event) {
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            //取缓存, 毕竟群主不可能变来变去吧
            Bot bot = event.getBot();
            GroupMember groupMember = bot.getGroupMember(
                    groupMessageEvent.getGroupId(), bot.getSelfId(), false);
            return "owner".equals(groupMember.getRole());
        } else return false;
    }

    public static boolean selfAdmin(Event event) {
        if (event instanceof GroupMessageEvent gme) {
            Bot bot = event.getBot();
            GroupMember groupMember = bot.getGroupMember(
                    gme.getGroupId(), bot.getSelfId(), true);
            String role = groupMember.getRole();
            return "admin".equals(role) || "owner".equals(role);
        } else return false;
    }

    public static boolean tempSession(Event event) {
        if (event instanceof PrivateMessageEvent pme) {
            return "group".equals(pme.getSubType());
        }
        return false;
    }


}
