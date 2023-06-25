package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
//@BotPlugin(name = "测试插件")
@SuppressWarnings("unused")
public class TestPlugin {



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
