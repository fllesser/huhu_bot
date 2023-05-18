package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupInfo;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupMember;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;
import tech.chowyijiu.huhu_bot.ws.Server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Slf4j
@BotPlugin("群组骚操作")
public class GroupCoquettishOperationPlugin {

    @Scheduled(cron = "0 * * * * *  ")
    public void dateCard() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime shitTime = LocalDateTime.parse("2023-05-27 10:00", formatter);
        Duration duration = Duration.between(LocalDateTime.now(), shitTime);
        String card = "距离答辩还有 " + duration.toMinutes() + " 分钟";
        Server.getBots().forEach(bot -> {
            bot.getGroupList(true).stream().map(GroupInfo::getGroupId).forEach(groupId -> {
                bot.callApi(GocqActionEnum.SET_GROUP_CARD,
                        "group_id", groupId, "user_id", bot.getUserId(), "card", card);
            });
        });
    }

    @MessageHandler(name = "头衔自助", commands = {"sgst"})
    public void sgst(Bot bot, GroupMessageEvent event) {
        //先判断bot是不是群主
        GroupMember groupMember = bot.getGroupMember(event.getGroupId(), bot.getUserId(), true);
        if (!"owner".equals(groupMember.getRole())) {
            log.info("{} {} 机器人不是群主, 忽略", this.getClass().getSimpleName(), "头衔自助");
            return;
        }
        String title = event.getMessage().replace(" ", "").replace("sgst", "");
        if (title.length() > 6) {
            bot.sendGroupMessage(event.getGroupId(), "群头衔最多为6位", true);
            return;
        }
        bot.callApi(GocqActionEnum.SET_GROUP_SPECIAL_TITLE,
                "group_id", event.getGroupId(), "user_id", event.getUserId(),
                "special_title", title);
    }
}