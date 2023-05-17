# huhu_bot
nonebot2, 但是jvav

## 介绍
1. java(springboot)基于go-cqhttp, websocket反向连接的qq机器人;
2. 支持被多个go-cqhttp连接;

## 架构
Spring Boot
go-cqhttp
websocket

## 使用要点
go-cqhttp 反向ws地址设置如 ws://127.0.0.1:8888/huhu/ws


## 示例插件
```Java
@Slf4j
@BotPlugin(name = "测试插件")
public class TestPlugin {

    @MessageHandler(name = "测试消息", commands = {"测试", "test"})
    public void test1(WebSocketSession session, MessageEvent event) {
        if (event instanceof PrivateMessageEvent) {
            Bot.sendPrivateMessage(session, event.getSender().getUserId(), "测试消息" ,false);
        } else if (event instanceof GroupMessageEvent) {
            Bot.sendGroupMessage(session, ((GroupMessageEvent) event).getGroupId(), "测试消息", false);
        }
    }

    @MessageHandler(name = "群聊测试1", commands = {"echo"}, priority = 3)
    public void test2(WebSocketSession session, GroupMessageEvent event) {
        Bot.sendGroupMessage(session, event.getGroupId(), "群聊测试111", true);
    }

    @MessageHandler(name = "群聊测试2", commands = {"echo"}, priority = 2, block = true)
    public void test3(WebSocketSession session, GroupMessageEvent event) {
        Bot.sendGroupMessage(session, event.getGroupId(), "群聊测试222", true);
    }
    @MessageHandler(name = "群聊测试3", commands = {"echo"}, priority = 1)
    public void test7(WebSocketSession session, GroupMessageEvent event) {
        Bot.sendGroupMessage(session, event.getGroupId(), "群聊测试333", true);
    }

    @MessageHandler(name = "私聊测试", commands = {"echo"})
    public void test4(final WebSocketSession session, PrivateMessageEvent event) {
        Bot.sendPrivateMessage(session, event.getUserId(), "测试私聊", true);
    }

    @NoticeHandler(name = "群聊撤回1", type = NoticeTypeEnum.group_recall, priority = 2)
    public void test5(final WebSocketSession session, NoticeEvent event) {
        log.info("群聊撤回1 event: {}", event);
    }

    @NoticeHandler(name = "群聊撤回2", type = NoticeTypeEnum.group_recall, priority = 1)
    public void test8(final WebSocketSession session, NoticeEvent event) {
        log.info("群聊撤回2 event: {}", event);
    }

    @NotifyNoticeHandler(name = "群昵称变更", subType = SubTypeEnum.title)
    public void test6(final WebSocketSession session, NoticeEvent event) {
        log.info("群昵称变更 event: {}", event);
    }
}
```

参考 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server)
