package tech.chowyijiu.huhu_bot.plugins;

import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.entity.arr_message.ForwardMessage;
import tech.chowyijiu.huhu_bot.entity.arr_message.Message;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@SuppressWarnings("unused")
@Slf4j
@BotPlugin(name = "测试插件")
public class TestPlugin {


    @MessageHandler(name = "callApi", commands = "api", rule = RuleEnum.superuser)
    public void apiTest(Bot bot, MessageEvent event) {
        //[key:value,key:value]
        String[] args = event.getCommandArgs().split(" ");
        GocqActionEnum action = null;
        try {
            action = GocqActionEnum.valueOf(args[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            bot.sendMessage(event, "没有这个API, 或huhubot暂未支持");
        }
        String[] keyValue = new String[args.length - 1];
        System.arraycopy(args, 1, keyValue, 0, args.length - 1);
        Map<String, Object> map = Arrays.stream(keyValue)
                .map(kv -> kv.split(":"))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
        long start = System.currentTimeMillis();
        assert action != null;
        String resp = bot.callApiWithResp(action, map);
        long end = System.currentTimeMillis();
        String costTime = "time-consuming: " + (end - start) + "ms";
        if (resp.startsWith("{")) {
            bot.sendMessage(event, resp + "\n" + costTime);
        } else if (resp.startsWith("[")) {
            List<Message> messages = new ArrayList<>();
            messages.add(Message.text(costTime));
            messages.addAll(JSONArray.parseArray(resp, String.class).stream()
                    .limit(98).map(Message::text).toList());
            List<ForwardMessage> nodes = ForwardMessage.quickBuild("Huhubot", event.getUserId(), messages);
            if (event instanceof GroupMessageEvent) {
                bot.sendGroupForwardMsg(((GroupMessageEvent) event).getGroupId(), nodes);
            } else if (event instanceof PrivateMessageEvent) {
                bot.sendPrivateForwardMsg(event.getUserId(), nodes);
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
