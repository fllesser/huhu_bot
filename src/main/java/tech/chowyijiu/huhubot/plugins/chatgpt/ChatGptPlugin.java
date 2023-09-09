package tech.chowyijiu.huhubot.plugins.chatgpt;

import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.annotation.RuleCheck;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.aop.rule.RuleEnum;

/**
 * @author elastic chow
 * @date 29/6/2023
 */
@SuppressWarnings("unused")
@BotPlugin("huhubot-plugin-chatgpt")
public class ChatGptPlugin {

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "chatgpt", commands = {"gpt"})
    public void defaultChatGpt(MessageEvent event) {
        String resp = GptReq.chat(event.getCommandArgs());
        event.getBot().sendMessage(event, resp);
    }

}
