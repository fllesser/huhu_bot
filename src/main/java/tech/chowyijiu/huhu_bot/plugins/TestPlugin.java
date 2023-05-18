package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.NoticeHandler;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupMember;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.GroupRecallNoticeEvent;
import tech.chowyijiu.huhu_bot.event.notice.NotifyNoticeEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.List;

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

    @NoticeHandler(name = "群聊撤回1", priority = 2)
    public void test5(Bot bot, GroupRecallNoticeEvent event) {
        //bot.sendMessage(event, "群聊撤回1 优先级2", true);
        log.info("群聊撤回1, 优先级2");
    }

    @NoticeHandler(name = "群聊撤回2", priority = 1)
    public void test8(Bot bot, GroupRecallNoticeEvent event) {
        //bot.sendMessage(event, "群聊撤回2 优先级1", true);
        log.info("群聊撤回2, 优先级1");
    }

    @NoticeHandler(name = "群头衔变更")
    public void test6(Bot bot, NotifyNoticeEvent event) {
        if (SubTypeEnum.title.name().equals(event.getSubType())) {
            //bot.sendMessage(event, "群头衔变更", true);
        }
    }

    @MessageHandler(name = "获取群成员列表", commands = {"获取群成员列表"})
    public void testGetGroupMemberList(Bot bot, GroupMessageEvent event) {
        List<GroupMember> groupMembers = bot.getGroupMembers(event.getGroupId(), true);
    }


    @MessageHandler(name = "测试call get api", commands = {"callGetApi"})
    public void testCallGetApi(Bot bot, MessageEvent event) {

    }
}
