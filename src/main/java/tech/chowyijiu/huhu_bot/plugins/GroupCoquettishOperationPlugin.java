package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.NoticeHandler;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.core.rule.Rule;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.message.MessageSegment;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupInfo;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupMember;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.GroupIncreaseNoticeEvent;
import tech.chowyijiu.huhu_bot.event.notice.NotifyNoticeEvent;
import tech.chowyijiu.huhu_bot.utils.StringUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;
import tech.chowyijiu.huhu_bot.ws.Server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Slf4j
@BotPlugin("群组骚操作")
@SuppressWarnings("unused")
public class GroupCoquettishOperationPlugin {


    @Scheduled(cron = "0 0/2 * * * * ")
    public void dateGroupCard() {
        String card = buildDateCard();
        log.info("时间群昵称开始设置 card: {}", card);
        Server.getBots().forEach(bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
                .stream().map(GroupInfo::getGroupId).forEach(groupId -> {
                    bot.callApi(GocqActionEnum.SET_GROUP_CARD,
                            "group_id", groupId, "user_id", bot.getUserId(), "card", card);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }));
        log.info("时间群昵称设置完毕 card: {}", card);
    }

    private String buildDateCard() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime shitTime = LocalDateTime.parse("2023-06-16 10:00", formatter);
        Duration duration = Duration.between(shitTime, LocalDateTime.now());
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        return "失业第" + days + "天" + hours + "时" + minutes + "分";
    }


    @Scheduled(cron = "1 0 0 * * ? ")
    public void dailyClockIn() {
        log.info("开始群打卡");
        List<Long> clockGroups = Arrays.asList(768887710L, 754044548L, 208248400L, 643396867L);
        Server.getBots().forEach(bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
                .stream().map(GroupInfo::getGroupId).filter(clockGroups::contains)
                .forEach(groupId -> {
                    bot.callApi(GocqActionEnum.SEND_GROUP_SIGN, "group_id", groupId);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }));
        log.info("群打卡完毕");
    }

    @MessageHandler(name = "头衔自助", commands = {"sgst"}, rule = RuleEnum.self_owner)
    public void sgst(Bot bot, GroupMessageEvent event) {
        String title = event.getCommandArgs();
        if (title.length() > 6) {
            bot.finish(event, "[bot]群头衔最多为6位");
        }
        for (String filter : new String[]{"群主", "管理员"}) {
            if (title.contains(filter)) {
                title = "群猪";
                break;
            }
        }
        bot.setGroupSpecialTitle(event.getGroupId(), event.getUserId(), title);
    }


    Rule replyPokeRule = (bot, event) -> {
        NotifyNoticeEvent notifyNoticeEvent = (NotifyNoticeEvent) event;
        return SubTypeEnum.poke.name().equals(notifyNoticeEvent.getSubType()) //戳一戳事件
                && bot.getUserId().equals(notifyNoticeEvent.getTargetId())    //被戳的是bot
                && !bot.getUserId().equals(notifyNoticeEvent.getUserId());    //不是bot号自己戳的
    };

    @NoticeHandler(name = "群内回戳", priority = 0)
    public void replyPoke(Bot bot, NotifyNoticeEvent event) {
        if (event.getGroupId() != null) bot.sendGroupMessage(
                event.getGroupId(), MessageSegment.poke(event.getUserId()) + "", false);
    }

    @NoticeHandler(name = "清代肝")
    public void cleanDaiGan(Bot bot, GroupIncreaseNoticeEvent event) {
        GroupMember groupMember = bot.getGroupMember(event.getGroupId(), event.getUserId(), true);
        for (String name : new String[]{groupMember.getNickname(), groupMember.getCard()})
            if (StringUtil.hasLength(name) && name.contains("代肝"))
                bot.kickGroupMember(event.getGroupId(), event.getUserId(), true);
    }
}
