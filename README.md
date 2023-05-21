# huhu_bot
nonebot2, 但是jvav

## 介绍
1. java(springboot), 基于go-cqhttp, websocket反向连接的qq机器人;
2. 支持被多个go-cqhttp连接;
3. 类似于nonebot2的插件编写方式

## 使用要点
go-cqhttp 反向ws地址设置如 ws://127.0.0.1:8888/huhu/ws


## 示例插件
```Java
@Slf4j
@BotPlugin(name = "测试插件")
public class TestPlugin {

    @MessageHandler(name = "测试消息", commands = {"测试", "test"})
    public void test1(Bot bot, MessageEvent event) {
        bot.sendMessage(event, "测试消息", false);
    }

    @MessageHandler(name = "群聊测试1", commands = {"echo"}, priority = 3)
    public void test2(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "群聊测试111", true);
    }

    @MessageHandler(name = "群聊测试2", commands = {"echo"}, priority = 2, block = true)
    public void test3(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "群聊测试222", true);
    }

    @MessageHandler(name = "群聊测试3", commands = {"echo"}, priority = 1)
    public void test7(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "群聊测试333", true);
    }

    @MessageHandler(name = "私聊测试", commands = {"echo"})
    public void test4(Bot bot, PrivateMessageEvent event) {
        bot.sendPrivateMessage(event.getUserId(), "测试私聊", true);
    }

    @NoticeHandler(name = "群聊撤回1", priority = 2)
    public void test5(Bot bot, GroupRecallNoticeEvent event) {
        bot.sendMessage(event, "群聊撤回1 优先级2", true);
    }
    @NoticeHandler(name = "群聊撤回2", priority = 1)
    public void test8(Bot bot, GroupRecallNoticeEvent event) {
        bot.sendMessage(event, "群聊撤回2 优先级1", true);
    }

    @NoticeHandler(name = "群头衔变更")
    public void test6(Bot bot, NotifyNoticeEvent event) {
        if (SubTypeEnum.title.name().equals(event.getSubType())) {
            bot.sendMessage(event, "群头衔变更", true);
        }
    }
}

```

```Java
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
```

参考项目 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server) [nonebot2](https://github.com/nonebot/nonebot2)
