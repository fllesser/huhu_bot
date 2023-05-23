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


## 示例插件

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

    @MessageHandler(name = "文字转语音测试", commands = {"tts", "文字转语音"})
    public void replyTtsMessage(Bot bot, GroupMessageEvent event) {
        if (!BotConfig.isSuperUser(event.getUserId())) {
            return;
        }
        bot.sendMessage(event, MessageSegment.tts(event.getMessage()) + "", false);
    }
}
```

参考项目 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server) [nonebot2](https://github.com/nonebot/nonebot2)
