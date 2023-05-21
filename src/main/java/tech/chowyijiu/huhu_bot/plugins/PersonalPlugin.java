package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.entity.gocq.message.MessageSegment;
import tech.chowyijiu.huhu_bot.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 21/5/2023
 */
@BotPlugin(name = "机器人接管QQ")
@Slf4j
public class PersonalPlugin {

    @MessageHandler(name = "回复jy群的临时会话", keywords = {"汉化", "英文", "中文"})
    public void replyJyGroup(Bot bot, PrivateMessageEvent event) {
        //如果不是临时会话
        if (!"group".equals(event.getSubType())) return;
        String message = "[Bot 代发, 非本人]" + MessageSegment.at(event.getUserId()) +
                "请认真观看教程视频 https://www.bilibili.com/video/BV1Xg411x7S2 不要再发临时会话问我或者其他管理了";
        bot.sendGroupMessage(event.getSender().getGroupId(), message,false);
    }
}
