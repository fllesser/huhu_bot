package tech.chowyijiu.huhu_bot.plugins;

import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.RuleV2;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.message.ForwardMessage;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.List;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@SuppressWarnings("unused")
@Slf4j
@BotPlugin(name = "测试插件")
public class TestPlugin {

    @RuleV2
    public void handle(Bot bot, Event event) {
        System.out.println("handle 执行完毕");
    }


    @MessageHandler(name = "callApi", commands = "api", rule = RuleEnum.superuser)
    public void apiTest(Bot bot, MessageEvent event) {
        String[] args = event.getCommandArgs().split(" ");
        if (args.length % 2 != 1) {
            bot.finish(event, "参数错误");
        }
        GocqActionEnum action = null;
        for (GocqActionEnum value : GocqActionEnum.values()) {
            if (value.getAction().equals(args[0])) {
                action = value;
                break;
            }
        }
        if (action == null) {
            bot.finish(event, "没有这个API, 或未支持");
        }
        Object[] params = new String[args.length - 1];
        System.arraycopy(args, 1, params, 0, args.length - 1);
        String resp = bot.callApiWithResp(action, params);
        if (resp.startsWith("{")) {
            bot.sendMessage(event, resp, true);
        } else if (resp.startsWith("[")) {
            List<String> list = JSONArray.parseArray(resp, String.class);
            if (list.size() > 0) {
                List<ForwardMessage> nodes = ForwardMessage.quickBuild("Huhubot", event.getUserId(), list);
                //bot.sendMessage(event, resp, true);
                if (event instanceof GroupMessageEvent) {
                    bot.sendGroupForwardMsg(((GroupMessageEvent) event).getGroupId(), nodes);
                } else if (event instanceof PrivateMessageEvent) {
                    bot.sendPrivateForwardMsg(event.getUserId(), nodes);
                }
            }
        }
    }

    //@MessageHandler(name = "测试rule, 注解", keywords = "rule1", rule = RuleEnum.tome)
    //public void testRule1(Bot bot, GroupMessageEvent event) {
    //    bot.sendMessage(event, "测试rule, 注解", true);
    //}
    //
    //Rule testRule2Rule = (bot, event) -> {
    //    if (event instanceof GroupMessageEvent) {
    //        return bot.getUserId().equals(((GroupMessageEvent) event).getUserId());
    //    }
    //    return false;
    //};
    //
    //@MessageHandler(name = "测试rule, 属性", keywords = "rule2")
    //public void testRule2(Bot bot, GroupMessageEvent event) {
    //    bot.sendMessage(event, "测试rule, 属性", true);
    //}


    //失败
    //@MessageHandler(name = "测试cutdown", keywords = {"cd"}, priority = 0)
    //public void testCutDown(Bot bot, GroupMessageEvent event) {
    //    bot.sendMessage(event, "测试cutdown", true);
    //}

    //@MessageHandler(name = "测试消息", commands = {"测试", "test"})
    //public void test1(Bot bot, MessageEvent event) {
    //    bot.sendMessage(event, "测试消息", false);
    //}
    //
    //@MessageHandler(name = "群聊测试1", commands = {"echo"}, priority = 3)
    //public void test2(Bot bot, GroupMessageEvent event) {
    //    bot.sendGroupMessage(event.getGroupId(), "群聊测试111", true);
    //}
    //
    //@MessageHandler(name = "群聊测试2", commands = {"echo"}, priority = 2, block = true)
    //public void test3(Bot bot, GroupMessageEvent event) {
    //    bot.sendGroupMessage(event.getGroupId(), "群聊测试222", true);
    //}
    //
    //@MessageHandler(name = "群聊测试3", commands = {"echo"}, priority = 1)
    //public void test7(Bot bot, GroupMessageEvent event) {
    //    bot.sendGroupMessage(event.getGroupId(), "群聊测试333", true);
    //}
    //
    //@MessageHandler(name = "私聊测试", commands = {"echo"})
    //public void test4(Bot bot, PrivateMessageEvent event) {
    //    bot.sendPrivateMessage(event.getUserId(), "测试私聊", true);
    //}
    //
    //@NoticeHandler(name = "群聊撤回1", priority = 2)
    //public void test5(Bot bot, GroupRecallNoticeEvent event) {
    //    //bot.sendMessage(event, "群聊撤回1 优先级2", true);
    //    log.info("群聊撤回1, 优先级2");
    //}
    //
    //@NoticeHandler(name = "群聊撤回2", priority = 1)
    //public void test8(Bot bot, GroupRecallNoticeEvent event) {
    //    //bot.sendMessage(event, "群聊撤回2 优先级1", true);
    //    log.info("群聊撤回2, 优先级1");
    //}
    //
    //@NoticeHandler(name = "群头衔变更")
    //public void test6(Bot bot, NotifyNoticeEvent event) {
    //    if (SubTypeEnum.title.name().equals(event.getSubType())) {
    //        //bot.sendMessage(event, "群头衔变更", true);
    //    }
    //}
    //
    //@MessageHandler(name = "获取群成员列表", commands = {"获取群成员列表"})
    //public void testGetGroupMemberList(Bot bot, GroupMessageEvent event) {
    //    List<GroupMember> groupMembers = bot.getGroupMembers(event.getGroupId(), true);
    //}
    //
    //
    //@MessageHandler(name = "测试call get api", commands = {"callSyncGetApi"})
    //public void testCallGetApi(Bot bot, MessageEvent event) {
    //
    //}
}
