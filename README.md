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
3. 在这个类中添加类型为tech.chowyijiu.huhu_bot.rule.Rule, 名称为响应方法加"Rule"的成员变量, 使用返回布尔值lambda表达式作为对应方法响应的前置条件

## 示例插件

```Java
@Slf4j
@BotPlugin("群组骚操作")
@SuppressWarnings("unused")
public class GroupCoquettishOperationPlugin {

    @Scheduled(cron = "0 * * * * *  ")
    public void dateGroupCard() {
        String card = buildDateCard();
        Server.getBots().forEach(bot -> Optional.ofNullable(bot.getGroups()).orElseGet(bot::getGroupList)
                .stream().map(GroupInfo::getGroupId).forEach(groupId -> {
                    bot.callApi(GocqActionEnum.SET_GROUP_CARD,
                            "group_id", groupId, "user_id", bot.getUserId(), "card", card);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
    }

    private String buildDateCard() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime shitTime = LocalDateTime.parse("2023-05-27 17:00", formatter);
        Duration duration = Duration.between(shitTime, LocalDateTime.now());
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        return "失业第" + days + "天" + hours + "时" + minutes + "分";
    }


    //机器人->群主
    Rule sgstRule = (bot, event) -> {
        GroupMember groupMember = bot.getGroupMember(
                ((GroupMessageEvent) event).getGroupId(), bot.getUserId(), true);
        return "owner".equals(groupMember.getRole());
    };

    @MessageHandler(name = "头衔自助", commands = {"sgst"})
    public void sgst(Bot bot, GroupMessageEvent event) {
        String title = event.getMessage();
        if (title.length() > 6) {
            bot.sendGroupMessage(event.getGroupId(), "[bot]群头衔最多为6位", true);
            return;
        }
        for (String filter : new String[]{"群主", "管理员"}) {
            if (title.contains(filter)) {
                title = "群猪";
                break;
            }
        }
        bot.callApi(GocqActionEnum.SET_GROUP_SPECIAL_TITLE,
                "group_id", event.getGroupId(), "user_id", event.getUserId(),
                "special_title", title);
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

    Rule replyJyGroupRule = (bot, event) -> "group".equals(((PrivateMessageEvent) event).getSubType());

    @MessageHandler(name = "回复jy群的临时会话", keywords = {"汉化", "英文", "中文"})
    public void replyJyGroup(Bot bot, PrivateMessageEvent event) {
        String message = "[bot]" + MessageSegment.at(event.getUserId()) +
                "请认真观看教程视频 https://www.bilibili.com/video/BV1Xg411x7S2 不要再发临时会话问我或者其他管理了";
        bot.sendGroupMessage(event.getSender().getGroupId(), message,false);
    }
    
    Rule replyTtsMessageRule = (bot, event) -> BotConfig.isSuperUser(((GroupMessageEvent) event).getUserId());

    @MessageHandler(name = "文字转语音测试", commands = {"tts", "文字转语音"})
    public void replyTtsMessage(Bot bot, GroupMessageEvent event) {
        bot.sendMessage(event, MessageSegment.tts(event.getMessage()) + "", false);
    }
    
}
```

参考项目 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server) [nonebot2](https://github.com/nonebot/nonebot2)
