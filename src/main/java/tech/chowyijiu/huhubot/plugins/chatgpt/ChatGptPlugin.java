package tech.chowyijiu.huhubot.plugins.chatgpt;

import tech.chowyijiu.huhubot.annotation.BotPlugin;
import tech.chowyijiu.huhubot.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.event.message.MessageEvent;
import tech.chowyijiu.huhubot.ws.Bot;

/**
 * @author elastic chow
 * @date 29/6/2023
 */
@SuppressWarnings("unused")
@BotPlugin
public class ChatGptPlugin {

    @MessageHandler(name = "chatgpt", commands = {"gpt"}, rule = RuleEnum.superuser)
    public void defaultChatGpt(Bot bot, MessageEvent event) {
        String resp = GptReq.chat(event.getCommandArgs());
        bot.sendMessage(event, resp);
    }

}
