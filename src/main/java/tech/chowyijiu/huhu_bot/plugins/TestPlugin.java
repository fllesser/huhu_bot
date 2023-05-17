package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NotifyNoticeHandler;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.NoticeEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
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

    @NoticeHandler(name = "群聊撤回1", type = NoticeTypeEnum.group_recall, priority = 2)
    public void test5(Bot bot, NoticeEvent event) {
        log.info("群聊撤回1 event: {}", event);
    }

    @NoticeHandler(name = "群聊撤回2", type = NoticeTypeEnum.group_recall, priority = 1)
    public void test8(Bot bot, NoticeEvent event) {
        log.info("群聊撤回2 event: {}", event);
    }

    @NotifyNoticeHandler(name = "群昵称变更", subType = SubTypeEnum.title)
    public void test6(Bot bot, NoticeEvent event) {
        log.info("群昵称变更 event: {}", event);
    }
}
