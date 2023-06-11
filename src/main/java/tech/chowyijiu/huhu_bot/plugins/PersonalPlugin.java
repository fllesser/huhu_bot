package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.core.rule.Rule;
import tech.chowyijiu.huhu_bot.entity.gocq.message.MessageSegment;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

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
        String message = "[Bot 代发, 非本人]" + MessageSegment.at(event.getUserId()) +
                "请认真观看教程视频 https://www.bilibili.com/video/BV1Xg411x7S2 不要再发临时会话问我或者其他管理了";
        bot.sendGroupMessage(event.getSender().getGroupId(), message,false);
    }


    Rule replyTtsMessageRule = (bot, event) -> BotConfig.isSuperUser(((GroupMessageEvent) event).getUserId());

    @MessageHandler(name = "文字转语音测试", commands = {"tts", "文字转语音"})
    public void replyTtsMessage(Bot bot, GroupMessageEvent event) {
        bot.sendMessage(event, MessageSegment.tts(event.getMessage()) + "", false);
    }

}
