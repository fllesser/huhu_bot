package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.core.rule.Rule;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.message.ForwardMessage;
import tech.chowyijiu.huhu_bot.entity.gocq.message.MessageSegment;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author elastic chow
 * @date 21/5/2023
 */
@Slf4j
@BotPlugin(name = "杂七杂八")
@SuppressWarnings("unused")
public class PersonalPlugin {

    Rule replyJyGroupRule = (bot, event) -> "group".equals(((PrivateMessageEvent) event).getSubType());

    @MessageHandler(name = "回复jy群的临时会话", keywords = {"汉化", "英文", "中文"})
    public void replyJyGroup(Bot bot, PrivateMessageEvent event) {
        String message = "[bot]" + MessageSegment.at(event.getUserId()) +
                "请认真观看教程视频 https://www.bilibili.com/video/BV1Xg411x7S2 不要再发临时会话问我或者其他管理了";
        bot.sendGroupMessage(event.getSender().getGroupId(), message,false);
    }

    @MessageHandler(name = "文字转语音测试", commands = {"tts", "文字转语音"}, rule = RuleEnum.superuser)
    public void replyTtsMessage(Bot bot, GroupMessageEvent event) {
        bot.sendMessage(event, MessageSegment.tts(event.getMessage()) + "", false);
    }

    @MessageHandler(name = "测试发送群转发消息", commands = {"转发"}, rule = RuleEnum.superuser)
    public void testSendGroupForwardMsg(Bot bot, GroupMessageEvent event) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("1-2", "1.转发消息完全不可信\n2.转发消息完全不可信");
        map.put("3-4", "3.转发消息完全不可信\n4.转发消息完全不可信");
        map.put("5-6", "5.转发消息完全不可信\n6.转发消息完全不可信");
        map.put("7-8", "7.转发消息完全不可信\n8.转发消息完全不可信");
        List<ForwardMessage> nodes = ForwardMessage.quickBuild(bot.getUserId(), map);
        bot.sendGroupForwardMsg(event.getGroupId(), nodes);
    }

    @MessageHandler(name = "echo", commands = "echo", rule = RuleEnum.superuser)
    public void echo(Bot bot, MessageEvent event) {
        bot.sendMessage(event, event.getMessage(), true);
    }

}
