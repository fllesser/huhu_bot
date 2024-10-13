package tech.flless.huhubot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.flless.huhubot.adapters.onebot.v11.bot.Bot;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import tech.flless.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;

import java.util.List;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Slf4j
@BotPlugin("huhubot-plugin-mygroup")
@SuppressWarnings("unused")
public class MyGroupPlugin {

    //private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

//    @Async
//    @Scheduled(cron = "0 0/3 * * * * ")
//    public void dateGroupCard() {
//        String card = "FLLess "  + LocalDateTime.now().format(formatter);
//        log.info("Time group nicknames start to be updated card: {}", card);
//        BotContainer.getBots().forEach(
//                bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
//                .stream().map(GroupInfo::getGroupId)
//                .forEach(groupId -> {
//                    bot.setGroupCard(groupId, bot.getSelfId(), card);
//                    try {
//                        Thread.sleep(2000L);
//                    } catch (InterruptedException ignored) {
//                    }
//                }));
//        log.info("Time group nickname set up card: {}", card);
//    }

//    @Deprecated
//    private String countdown() {
//        LocalDateTime fromTime = LocalDateTime.parse("2023-06-16 10:00", formatter);
//        Duration duration = Duration.between(fromTime, LocalDateTime.now());
//        long days = duration.toDays();
//        duration = duration.minusDays(days);
//        long hours = duration.toHours();
//        duration = duration.minusHours(hours);
//        long minutes = duration.toMinutes();
//        return days + "天" + hours + "时" + minutes + "分";
//    }

    //private final List<Long> clockGroups = List.of(768887710L, 754044548L, 208248400L, 643396867L);

//    @Async
//    @Scheduled(cron = "0 0 0 * * *")
//    public void dailyClockIn() {
//        log.info("开始群打卡");
//        for (Bot bot : BotContainer.getBots()) {
//            for (Long clockGroup : clockGroups) {
//                bot.sendGroupSign(clockGroup);
//                try {
//                    Thread.sleep(500L);
//                } catch (InterruptedException ignored) {
//                }
//            }
//        }
//        log.info("群打卡完毕");
//    }

//    @RuleCheck(rule = RuleEnum.self_owner)
//    @MessageHandler(name = "头衔自助", commands = {"sgst"})
//    public void sgst(GroupMessageEvent event) {
//        String title = event.getCommandArgs();
//        for (String filter : new String[]{"群主", "管理", "主群"}) {
//            if (title.contains(filter)) {
//                title = "群猪";
//                break;
//            }
//        }
//        event.getBot().setGroupSpecialTitle(event.getGroupId(), event.getUserId(), title);
//    }


    @MessageHandler(name = "回应带表情的消息", keywords = "")
    public void replyEmoji(GroupMessageEvent event) {
        Bot bot = event.getBot();
        List<MessageSegment> faces = event.getMessage().get("face");
        faces.stream().map(seg -> seg.getInteger("id")).distinct().forEach(id -> {
            bot.setMsgEmojiLike(event.getMessageId(), id);
        });
    }

    //@NoticeHandler(name = "群内回戳", priority = 0)
    //public void replyPoke(NotifyNoticeEvent event) {
        //Bot bot = event.getBot();
        //if (!SubTypeEnum.poke.name().equals(event.getSubType()) //不是戳一戳事件
                //|| !bot.getSelfId().equals(event.getTargetId()) //被戳的不是bot
               // || bot.getSelfId().equals(event.getUserId())    //是bot号自己戳的
        //) return;
        //event.reply(MessageSegment.poke(event.getUserId()));
    //}

    //@RuleCheck(rule = RuleEnum.tome)
    //@MessageHandler(name = "被@, 让小爱通知我", keywords = {""}, priority = 9)
//    public void atMeXiaoAiNotice(GroupMessageEvent event) {
//        Bot bot = event.getBot();
//        GroupMember groupMember = bot.getGroupMember(event.getGroupId(), event.getUserId(), true);
//        GroupInfo groupInfo = bot.getGroupInfo(event.getGroupId(), false);
//        XiaoAIUtil.tts("群" + groupInfo.getGroupName() + "内"
//                + groupMember.getNickname() + "艾特你说, " + event.getMessage().getPlainText());
//    }
}
