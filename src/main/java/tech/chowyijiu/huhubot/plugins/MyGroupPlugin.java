package tech.chowyijiu.huhubot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhubot.annotation.BotPlugin;
import tech.chowyijiu.huhubot.annotation.MessageHandler;
import tech.chowyijiu.huhubot.annotation.NoticeHandler;
import tech.chowyijiu.huhubot.constant.SubTypeEnum;
import tech.chowyijiu.huhubot.core.rule.Rule;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.entity.response.GroupInfo;
import tech.chowyijiu.huhubot.entity.response.GroupMember;
import tech.chowyijiu.huhubot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhubot.event.notice.NotifyNoticeEvent;
import tech.chowyijiu.huhubot.utils.xiaoai.XiaoAIUtil;
import tech.chowyijiu.huhubot.ws.Bot;
import tech.chowyijiu.huhubot.ws.Server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Slf4j
@BotPlugin("群组骚操作")
@SuppressWarnings("unused")
public class MyGroupPlugin {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Scheduled(cron = "0 0/2 * * * * ")
    public void dateGroupCard() {
        final String card = "失业第" + this.countdown("2023-06-16 10:00");
        log.info("时间群昵称开始设置 card: {}", card);
        Server.getBots().forEach(bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
                .stream().map(GroupInfo::getGroupId).forEach(groupId -> {
                    bot.setGroupCard(groupId, bot.getUserId(), card);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }));
        //customInfoMap.keySet().forEach(userId -> {
        //    String customCard = "";
        //    CustomInfo customInfo = customInfoMap.get(userId);
        //    if ("now".equals(customInfo.type)) {
        //        customCard = customInfo.suffix + LocalDateTime.now().format(formatter);
        //    } else if ("countdown".equals(customInfo.type)) {
        //        customCard = customInfo.suffix + this.countdown(customInfo.date);
        //    }
        //    Server.getBots().get(0).setGroupCard(customInfo.groupId, customInfo.userId, customCard);
        //    try {
        //        Thread.sleep(1000L);
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //});
        log.info("时间群昵称设置完毕 card: {}", card);
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

    //private final Map<Long, CustomInfo> customInfoMap = new HashMap<>();

    //@Builder
    //static class CustomInfo {
    //    private Long userId;
    //    private Long groupId;
    //    private String type;    //now / countdown
    //    private String date;    //"2023-06-16 10:00"
    //    private String suffix;  //前缀
    //}

    /**
     * 两分钟一改
     * cgtn now 前缀
     * cgtn cutdown/前缀/2023-06-16 10:00
     */
    //@MessageHandler(name = "自定义时间群昵称", commands = {"cgtn"}, rule = RuleEnum.self_admin)
    //public void customDateCard(Bot bot, GroupMessageEvent event) {
    //    if (customInfoMap.size() >= 30) event.finish("定制人数已上限");
    //    String commandArgs = event.getCommandArgs();
    //    val infoBuilder = CustomInfo.builder()
    //            .userId(event.getUserId()).groupId(event.getGroupId());
    //    if (commandArgs.startsWith("now")) {
    //        infoBuilder.type("now").suffix(commandArgs.replaceFirst("now", ""));
    //    } else if (commandArgs.startsWith("cutdown")) {
    //        String[] argsArr = commandArgs.split("/");
    //        if (argsArr.length == 3) {
    //            infoBuilder.type("cutdown").suffix(argsArr[1]).date(argsArr[2]);
    //        } else {
    //            event.finish("格式有误:\n1.cgtn now 前缀\n2.cgtn cutdown/前缀/2023-06-16 10:00");
    //        }
    //    }
    //    customInfoMap.put(event.getUserId(), infoBuilder.build());
    //    bot.sendGroupMessage(event.getGroupId(), "自定义群时间昵称成功, 两分钟后见效", false);
    //}
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyClockIn() {
        log.info("开始群打卡");
        List<Long> clockGroups = Arrays.asList(768887710L, 754044548L, 208248400L, 643396867L);
        Server.getBots().forEach(bot -> clockGroups.forEach(groupId -> {
                    bot.sendGroupSign(groupId);
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException ignored) {
                    }
                })
        );
        log.info("群打卡完毕");
    }

    @MessageHandler(name = "头衔自助", commands = {"sgst"}, rule = RuleEnum.self_owner)
    public void sgst(Bot bot, GroupMessageEvent event) {
        String title = event.getCommandArgs();
        //if (!StringUtil.hasLength(title)) event.finish("[bot]群头衔为空");
        if (title.length() > 6) {
            bot.sendMessage(event, "[bot]群头衔最多为6位");
            return;
        }
        for (String filter : new String[]{"群主", "管理"}) {
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
        if (event.getGroupId() != null) {
            bot.sendGroupMessage(event.getGroupId(), MessageSegment.poke(event.getUserId()));
        }
    }

    //todo rule可以定义哪些群需要通知我, 然后移除tome, 写到方法里判断
    @MessageHandler(name = "被@, 让小爱通知我", keywords = {""}, rule = RuleEnum.tome, priority = 9)
    public void atMeXiaoAiNotice(Bot bot, GroupMessageEvent event) {
        GroupMember groupMember = bot.getGroupMember(event.getGroupId(), event.getUserId(), true);
        bot.getGroups().stream()
                .filter(g -> g.getGroupId().equals(event.getGroupId()))
                .findFirst()
                .ifPresent(g -> XiaoAIUtil.tts("群" + g.getGroupName() + "内"
                        + groupMember.getNickname() + "at你说, " + event.getMessage().plainText())
                );

    }

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


    //Rule giveAdminRule = (bot, event) -> RuleImpl.selfOwner(bot, event)
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