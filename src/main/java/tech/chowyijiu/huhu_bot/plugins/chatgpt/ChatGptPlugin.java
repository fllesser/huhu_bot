package tech.chowyijiu.huhu_bot.plugins.chatgpt;

import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.HashMap;
import java.util.Map;

/**
 * @author elastic chow
 * @date 29/6/2023
 */
@SuppressWarnings("unused")
@BotPlugin
public class ChatGptPlugin {

    //userId session
    public final Map<Long, String> map = new HashMap<>();

    @MessageHandler(name = "chatgpt", commands = {"gpt"}, rule = RuleEnum.superuser)
    public void defaultChatGpt(Bot bot, MessageEvent event) {
        String resp = GptReq.chat(event.getUserId(), event.getCommandArgs());
        bot.sendMessage(event, resp, false);
    }

}
