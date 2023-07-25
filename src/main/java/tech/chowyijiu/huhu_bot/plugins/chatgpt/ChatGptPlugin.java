package tech.chowyijiu.huhu_bot.plugins.chatgpt;

import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 29/6/2023
 */
@SuppressWarnings("unused")
@BotPlugin
public class ChatGptPlugin {

    @MessageHandler(name = "chatgpt", commands = {"gpt"}, rule = RuleEnum.superuser)
    public void defaultChatGpt(Bot bot, MessageEvent event) {
        String resp = GptReq.chat(event.getUserId(), event.getCommandArgs());
        bot.sendMessage(event, resp);
    }

}
