package tech.flless.huhubot.plugins.ai;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
import tech.flless.huhubot.utils.StringUtil;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@BotPlugin("AI")
@Slf4j
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
                    content.set(event.getCommandArgs() + ", " + replied.getPlainText());
                },
                () -> {
                    content.set(event.getCommandArgs());
                }
        );
        //log.info(content.get());
        //AccessToken = ernieClient.getToken(WxConfig.ak, WxConfig.sk).getAccess_token();
        CompletionRes completion = getCompletion(content.get());
        bot.sendMessage(event, completion.getResult());

    }

    private CompletionRes getCompletion(String content){
        if (!StringUtil.hasLength(AccessToken)) {
            TokenRes token = ernieClient.getToken(WxConfig.ak, WxConfig.sk);
            AccessToken = token.getAccess_token();
        }
        List<WxMessage> wxMessages = List.of(new WxMessage("user", content));
        Map<String, List<WxMessage>> map = Map.of("messages", wxMessages);
        String jsonString = JSONObject.toJSONString(map);
        RequestBody requestBody = RequestBody.create(jsonString, MediaType.parse("application/json;charset=UTF-8"));
        return ernieClient.getCompletion(AccessToken, requestBody);
    }
}
