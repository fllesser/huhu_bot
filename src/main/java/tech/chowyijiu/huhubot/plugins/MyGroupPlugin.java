package tech.chowyijiu.huhubot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.annotation.NoticeHandler;
import tech.chowyijiu.huhubot.core.annotation.RuleCheck;
import tech.chowyijiu.huhubot.core.constant.SubTypeEnum;
import tech.chowyijiu.huhubot.core.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.core.entity.response.GroupInfo;
import tech.chowyijiu.huhubot.core.entity.response.GroupMember;
import tech.chowyijiu.huhubot.core.event.message.GroupMessageEvent;
import tech.chowyijiu.huhubot.core.event.notice.NotifyNoticeEvent;
import tech.chowyijiu.huhubot.core.rule.Rule;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.core.ws.Bot;
import tech.chowyijiu.huhubot.core.ws.Huhubot;
import tech.chowyijiu.huhubot.utils.xiaoai.XiaoAIUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Slf4j
@BotPlugin("huhubot-plugin-mygroup")
@SuppressWarnings("unused")
public class MyGroupPlugin {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Scheduled(cron = "0 0/2 * * * * ")
    public void dateGroupCard() {
        String card = "失业第" + this.countdown("2023-06-16 10:00");
        log.info("Time group nicknames start to be updated card: {}", card);
        Huhubot.getBots().forEach(bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
                .stream().map(GroupInfo::getGroupId).forEach(groupId -> {
                    bot.setGroupCard(groupId, bot.getSelfId(), card);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }));
        log.info("Time group nickname set up card: {}", card);
    }

    private String countdown(String dateText) {
        LocalDateTime fromTime = LocalDateTime.parse(dateText, formatter);
        Duration duration = Duration.between(fromTime, LocalDateTime.now());
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        return days + "天" + hours + "时" + minutes + "分";
    }

    private final List<Long> clockGroups = List.of(768887710L, 754044548L, 208248400L, 643396867L);

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyClockIn() {
        log.info("开始群打卡");
        for (Bot bot : Huhubot.getBots()) {
            for (Long clockGroup : clockGroups) {
                bot.sendGroupSign(clockGroup);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ignored) {
                }
            }
        }
        log.info("群打卡完毕");
    }

    @RuleCheck(rule = RuleEnum.self_owner)
    @MessageHandler(name = "头衔自助", commands = {"sgst"})
    public void sgst(GroupMessageEvent event) {
        String title = event.getCommandArgs();
        for (String filter : new String[]{"群主", "管理", "主群"}) {
            if (title.contains(filter)) {
                title = "群猪";
                break;
            }
        }
        event.getBot().setGroupSpecialTitle(event.getGroupId(), event.getUserId(), title);
    }

    Rule replyPokeRule = event -> {
        NotifyNoticeEvent notifyNoticeEvent = (NotifyNoticeEvent) event;
        Bot bot = event.getBot();
        return SubTypeEnum.poke.name().equals(notifyNoticeEvent.getSubType()) //戳一戳事件
                && bot.getSelfId().equals(notifyNoticeEvent.getTargetId())    //被戳的是bot
                && !bot.getSelfId().equals(notifyNoticeEvent.getUserId());    //不是bot号自己戳的
    };

    @NoticeHandler(name = "群内回戳", priority = 0)
    public void replyPoke(NotifyNoticeEvent event) {
        Bot bot = event.getBot();
        if (!SubTypeEnum.poke.name().equals(event.getSubType()) //不是戳一戳事件
                || !bot.getSelfId().equals(event.getTargetId()) //被戳的不是bot
                || bot.getSelfId().equals(event.getUserId())    //是bot号自己戳的)
        ) {
            return;
        }
        if (event.getGroupId() != null) {
            event.getBot().sendGroupMessage(event.getGroupId(), MessageSegment.poke(event.getUserId()));
        }
    }

    @RuleCheck(rule = RuleEnum.tome)
    @MessageHandler(name = "被@, 让小爱通知我", keywords = {""}, priority = 9)
    public void atMeXiaoAiNotice(GroupMessageEvent event) {
        Bot bot = event.getBot();
        GroupMember groupMember = bot.getGroupMember(event.getGroupId(), event.getUserId(), true);
        bot.getGroups().stream()
                .filter(g -> g.getGroupId().equals(event.getGroupId()))
                .findFirst()
                .ifPresent(g -> XiaoAIUtil.tts("群" + g.getGroupName() + "内"
                        + groupMember.getNickname() + "艾特你说, " + event.getMessage().getPlainText())
                );

    }

    //private final Map<String, Integer> verificationMap = new HashMap<>();
    //private final List<Long> verificationGroups = List.of(754044548L, 208248400L);

    //@NoticeHandler(name = "麦片哥验证1")
    //public void verify1(Bot bot, GroupIncreaseNoticeEvent event) {
    //    if (verificationGroups.stream().noneMatch(gid -> gid.equals(event.getGroupId()))) return;
    //    int i = (int) (Math.random() * 10);
    //    int j = (int) (Math.random() * 10);
    //    verificationMap.put(event.getGroupId() + "_" + event.getUserId(), i + j);
    //    Message message = new Message().append(MessageSegment.at(event.getUserId()))
    //            .append(" 取汉化前请先完成入群验证:").append("\n" + i + " + " + j + " = ?")
    //            .append("\n注意:回答无需@我, 只有一次机会, 回答错误, 将会被踢出群聊");
    //    bot.sendMessage(event, message);
    //}
    //
    //@MessageHandler(name = "麦片哥验证2")
    //public void verify2(Bot bot, GroupMessageEvent event) {
    //    String key = event.getGroupId() + "_" + event.getUserId();
    //    if (!verificationMap.containsKey(key)) return;
    //    int res = verificationMap.get(key);
    //    verificationMap.remove(key);
    //    if (res == Integer.parseInt(event.getRawMessage())) {
    //        bot.sendMessage(event, MessageSegment.at(event.getUserId()) + " 回答正确!");
    //    } else {
    //        bot.deleteMsg(event.getMessageId());
    //        bot.setGroupKick(event.getGroupId(), event.getUserId(), false);
    //    }
    //}

    //@NoticeHandler(name = "清代肝")
    //public void cleanDaiGan(Bot bot, GroupIncreaseNoticeEvent event) {
    //    GroupMember groupMember = bot.getGroupMember(event.getGroupId(), event.getUserId(), true);
    //    String nickname = groupMember.getNickname();
    //    if (StringUtil.hasLength(nickname) && nickname.contains("代肝"))
    //        bot.sendMessage(event, new Message()
    //                .append(MessageSegment.at(groupMember.getUserId()))
    //                .append("本群禁止代肝, 请修改群昵称, 否则5分钟后将踢出"));
    //    new Thread(() -> {
    //        try {
    //            Thread.sleep(300000L);
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        }
    //        GroupMember gm = bot.getGroupMember(event.getGroupId(), event.getUserId(), true);
    //        String nickname_ = gm.getNickname();
    //        if (StringUtil.hasLength(nickname_) && nickname_.contains("代肝"))
    //                bot.setGroupKick(event.getGroupId(), event.getUserId(), true);
    //    }).start();
    //}


    //RuleCheck giveAdminRule = (bot, event) -> RuleReference.selfOwner(bot, event)
    //        && "message_sent".equals(event.getPostType());
    //
    ///**
    // * 是答辩
    // * command setadmin@... false / true
    // */
    //@MessageHandler(name = "授予管理员", commands = "setadmin")
    //public void giveAdmin(Bot bot, GroupMessageEvent event) {
    //    Message message = event.getMessage();
    //    List<MessageSegment> segments = message.getSegmentByType("at");
    //    segments.forEach(segment -> {
    //        long qq = segment.getLong("qq");
    //        GroupMember groupMember = bot.getGroupMember(event.getGroupId(), qq, true);
    //        boolean isAdmin = "admin".equals(groupMember.getRole());
    //        bot.setGroupAdmin(event.getGroupId(), qq, !isAdmin);
    //    });
    //
    //}

    //@NoticeHandler(name = "清除不活跃成员")
    //public void kickNotActiveMembers(Bot bot, GroupIncreaseNoticeEvent event) {
    //    GroupInfo groupInfo = bot.getGroupInfo(event.getGroupId(), true);
    //    if (!groupInfo.getMemberCount().equals(groupInfo.getMaxMemberCount())) {
    //        //群没满
    //        return;
    //    }
    //    List<GroupMember> groupMembers = bot.getGroupMembers(event.getGroupId(), true);
    //    long curTime = System.currentTimeMillis();
    //    groupMembers.stream()
    //            .filter(gm -> !StringUtil.hasLength(gm.getTitle())  //没有群头衔
    //                && Integer.parseInt(gm.getLevel()) <= 1         //
    //                && curTime - gm.getLastSentTime() > 7777777)    //三个月未发言
    //            .limit(10).forEach(gm -> bot.setGroupKick(event.getGroupId(), gm.getUserId(), false));
    //}


    //@MessageHandler(name = "禁言", commands = {"ban", "禁"}, rule = RuleEnum.admin)
    //public void ban(Bot bot, GroupMessageEvent event) {
    //    List<MessageSegment> segments = event.getMsg().getSegmentByType("at");
    //    final int duration;
    //    if (StringUtil.isDigit(event.getCommandArgs())) {
    //        duration = Integer.parseInt(event.getCommandArgs());
    //    } else duration = 600; //默认禁 10 min
    //    segments.forEach(segment -> {
    //        long qq = Long.parseLong(segment.get("qq"));
    //        bot.setGroupBan(event.getGroupId(), qq, duration);
    //    });
    //    //if (segments.size() == 1 && "all".equals(segments.get(0).get("qq"))) {
    //    //    bot.setGroupWholeBan(event.getGroupId(), true);
    //    //} else {
    //    //    final int duration;
    //    //    if (StringUtil.isDigit(event.getCommandArgs())) {
    //    //         duration = Integer.parseInt(event.getCommandArgs());
    //    //    } else duration = 600; //默认禁 10 min
    //    //    segments.forEach(segment -> {
    //    //        //这里qq必为数字, 无需判断
    //    //        long qq = Long.parseLong(segment.get("qq"));
    //    //        bot.setGroupBan(event.getGroupId(), qq, duration);
    //    //    });
    //    //}
    //}
    //
    //@MessageHandler(name = "踢人", commands = {"kick", "踢"}, rule = RuleEnum.admin)
    //public void kick(Bot bot, GroupMessageEvent event) {
    //    List<MessageSegment> segments = event.getMsg().getSegmentByType("at");
    //    if (segments.size() == 1 && "all".equals(segments.get(0).get("qq"))) {
    //        event.finish("你是要把所有人都踢了吗?");
    //    }
    //    segments.forEach(segment -> {
    //        //这里qq必为数字, 无需判断
    //        long qq = Long.parseLong(segment.get("qq"));
    //        bot.setGroupKick(event.getGroupId(), qq, true);
    //    });
    //}
}
