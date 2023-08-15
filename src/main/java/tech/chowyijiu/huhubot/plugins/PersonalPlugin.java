package tech.chowyijiu.huhubot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.entity.arr_message.Message;
import tech.chowyijiu.huhubot.core.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.core.event.message.GroupMessageEvent;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhubot.core.rule.Rule;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.core.ws.Bot;

/**
 * @author elastic chow
 * @date 21/5/2023
 */
@Slf4j
@BotPlugin(name = "huhubot-plugin-seveneight")
@SuppressWarnings("unused")
public class PersonalPlugin {

    Rule replyJyGroupRule = (bot, event) -> "group".equals(((PrivateMessageEvent) event).getSubType());

    @MessageHandler(name = "回复jy群的临时会话", keywords = {"汉化", "英文", "中文"})
    public void replyJyGroup(Bot bot, PrivateMessageEvent event) {
        Message message = new Message()
                .append("[bot]").append(MessageSegment.at(event.getUserId()))
                .append("请认真观看教程视频 https://www.bilibili.com/video/BV1Xg411x7S2 不要再发临时会话问我或者其他管理了");
        bot.sendGroupMessage(event.getSender().getGroupId(), message);
    }

    @MessageHandler(name = "文字转语音测试", commands = {"tts", "文字转语音"}, rule = RuleEnum.superuser)
    @Deprecated
    public void replyTtsMessage(Bot bot, GroupMessageEvent event) {
        bot.sendMessage(event, MessageSegment.tts(event.getCommandArgs()));
    }

    //@MessageHandler(name = "测试发送群转发消息", commands = "转发", rule = RuleEnum.superuser)
    //public void testSendGroupForwardMsg(Bot bot, GroupMessageEvent event) {
    //    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    //    map.put("1", "转发消息是完全不可信的");
    //    map.put("2", "昵称内容头像均可自定义");
    //    map.put("3", "连演员啥的都不需要请捏");
    //    map.put("4", "当然此条也是不可信的哈");
    //    List<ForwardMessage> nodes = ForwardMessage.quickBuild(bot.getUserId(), map);
    //    bot.sendGroupForwardMsg(event.getGroupId(), nodes);
    //}

    @MessageHandler(name = "echo", commands = "echo", rule = RuleEnum.superuser)
    public void echo(Bot bot, MessageEvent event) {
        bot.sendMessage(event, event.getCommandArgs());
    }

}
