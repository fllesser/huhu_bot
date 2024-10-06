package tech.flless.huhubot.plugins.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import tech.flless.huhubot.adapters.onebot.v11.bot.Bot;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.Message;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.MessageSegment;
import tech.flless.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.config.WxConfig;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;
import tech.flless.huhubot.core.annotation.RuleCheck;
import tech.flless.huhubot.core.rule.RuleEnum;
import tech.flless.huhubot.plugins.ai.entity.CompletionRes;
import tech.flless.huhubot.plugins.ai.entity.TokenRes;
import tech.flless.huhubot.plugins.ai.entity.WxMessage;
import tech.flless.huhubot.plugins.ai.entity.WxMessages;
import tech.flless.huhubot.utils.StringUtil;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@BotPlugin("AI")
@Slf4j
public class AIPlugin {

    @Resource
    private ErnieClient ernieClient;

    private String AccessToken;

    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "一言", commands = "ai")
    public void ai(MessageEvent event) {
        Bot bot = event.getBot();
        MessageInfo reply = event.getReply();
        AtomicReference<String> content = new AtomicReference<>("");
        Optional.ofNullable(reply).ifPresentOrElse(
                seg -> {
                    Message replied = seg.getMessage();
                    replied.plainText();
                    content.set(event.getCommandArgs() + ", " + replied.getPlainText());
                },
                () -> {
                    content.set(event.getCommandArgs());
                }
        );
        if (!StringUtil.hasLength(AccessToken)) {
            AccessToken = ernieClient.getToken(WxConfig.clientId, WxConfig.clientSecret).getAccessToken();
        }
        CompletionRes completion = ernieClient.getCompletion(AccessToken, new WxMessages(content.get()));
        bot.sendMessage(event, completion.getResult());

    }

}
