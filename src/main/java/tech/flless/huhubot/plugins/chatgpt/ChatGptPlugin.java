package tech.flless.huhubot.plugins.chatgpt;

import jakarta.annotation.Resource;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;
import tech.flless.huhubot.core.annotation.RuleCheck;
import tech.flless.huhubot.core.rule.RuleEnum;

/**
 * @author elastic chow
 * @date 29/6/2023
 */
@SuppressWarnings("unused")
@BotPlugin("huhubot-plugin-chatgpt")
public class ChatGptPlugin {

    @Resource
    private GptClient gptClient;
    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "chatgpt", commands = {"gpt"})
    public void defaultChatGpt(MessageEvent event) {
        String resp = gptClient.chat(event.getCommandArgs());
        event.sendMessage(resp);
    }

}
