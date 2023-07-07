# huhu_bot
nonebot2, 但是jvav

## 介绍
1. java(springboot), 基于go-cqhttp, websocket反向连接的qq机器人;
2. 支持被多个go-cqhttp连接;
3. 类似于nonebot2的插件编写方式

## 使用要点
go-cqhttp 反向ws地址设置如 ws://127.0.0.1:8888/huhu/ws

## TODO
1. 连续对话
2. 自定义合并转发
3. 把整个项目,做成springboot-starter, 使之和plugin分离

## 插件编写方式
1. 新建一个类加上`@BotPlugin("指定名称,用于加载时打印日志")` 和 `@SuppressWarnings("unused")`
2. 编写响应的方法, 参数必须为(Bot bot, Event event), 其中event指定其子类, 响应对应的事件
3. 然后在方法上加上@MessageHandler(可指定命令匹配或者关键词匹配) 或者 @NoticeHandler
4. 在这个类中添加类型为tech.chowyijiu.huhu_bot.rule.Rule, 名称为响应方法加"Rule"的成员变量, 使用返回布尔值lambda表达式作为对应方法响应的前置条件

## 示例插件

```Java
@Slf4j
@BotPlugin("群组骚操作")
@SuppressWarnings("unused")
public class GroupCoquettishOperationPlugin {


    @Scheduled(cron = "0 0/2 * * * * ")
    public void dateGroupCard() {
        final String card = buildDateCard();
        log.info("时间群昵称开始设置 card: {}", card);
        Server.getBots().forEach(bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
                .stream().map(GroupInfo::getGroupId).forEach(groupId -> {
                    bot.setGroupCard(groupId, bot.getUserId(), card);
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


    @Scheduled(cron = "0 0 0 * * *")
    public void dailyClockIn() {
        log.info("开始群打卡");
        List<Long> clockGroups = Arrays.asList(768887710L, 754044548L, 208248400L);
        Server.getBots().forEach(bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
                .stream().map(GroupInfo::getGroupId).filter(clockGroups::contains)
                .forEach(groupId -> {
                    bot.sendGroupSign(groupId);
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException ignored) {
                    }
                }));
        log.info("群打卡完毕");
    }


    @MessageHandler(name = "头衔自助", commands = {"sgst"}, rule = RuleEnum.self_owner)
    public void sgst(Bot bot, GroupMessageEvent event) {
        String title = event.getCommandArgs();
        if (!StringUtil.hasLength(title)) event.finish("[bot]群头衔为空");
        if (title.length() > 6) event.finish("[bot]群头衔最多为6位");
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

    Rule giveAdminRule = (bot, event) -> RuleImpl.selfOwner(bot, event) &&
            "message_sent".equals(event.getPostType());

    /**
     * 是答辩
     * command setadmin@... false / true
     */
    @MessageHandler(name = "授予管理员", commands = "setadmin")
    public void giveAdmin(Bot bot, GroupMessageEvent event) {
        boolean enable = Boolean.parseBoolean(event.getCommandArgs());
        Message message = event.getMsg();
        List<MessageSegment> segments = message.getSegmentByType("at");
        segments.forEach(segment -> {
            long qq = Long.parseLong(segment.get("qq"));
            bot.setGroupAdmin(event.getGroupId(), qq, enable);
        });

    }
}
```

参考项目 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server) [nonebot2](https://github.com/nonebot/nonebot2)
