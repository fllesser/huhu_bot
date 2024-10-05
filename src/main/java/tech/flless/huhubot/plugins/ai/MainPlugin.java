package tech.flless.huhubot.plugins.ai;

import jakarta.annotation.Resource;
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


import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@BotPlugin("AI")
public class MainPlugin {

    @Resource
    private ErnieClient ernieClient;

    private String AccessToken;

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "一言", commands = "ai")
    public void ai(MessageEvent event) {
        Bot bot = event.getBot();
        Message message = event.getMessage();
        MessageSegment reply = message.get("reply", 0);
        AtomicReference<String> content = new AtomicReference<>("");
        Optional.ofNullable(reply).ifPresentOrElse(
                seg -> {
                    MessageInfo msg = bot.getMsg(seg.getInteger("id"));
                    Message replied = msg.getMessage();
                    replied.plainText();
                    content.set(message.getPlainText() + ", " + replied.getPlainText());
                },
                () -> {
                    content.set(message.getPlainText());
                }
        );
        AccessToken = ernieClient.getToken(WxConfig.ak, WxConfig.sk).getAccess_token();
        //String res = ernieClient.getCompletion(AccessToken, new WxMessages(content.get()));


    }
}
