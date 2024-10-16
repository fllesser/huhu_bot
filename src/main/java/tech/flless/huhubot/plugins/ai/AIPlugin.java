package tech.flless.huhubot.plugins.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import tech.flless.huhubot.adapters.onebot.v11.bot.Bot;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.Message;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import tech.flless.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.config.BotConfig;
import tech.flless.huhubot.config.ErrieConfig;
import tech.flless.huhubot.config.ReechoConfig;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;
import tech.flless.huhubot.core.exception.FinishedException;
import tech.flless.huhubot.plugins.ai.errie.entity.CompletionRes;
import tech.flless.huhubot.plugins.ai.errie.ErnieClient;
import tech.flless.huhubot.plugins.ai.errie.entity.WxMessages;
import tech.flless.huhubot.plugins.ai.reecho.ReechoClient;
import tech.flless.huhubot.plugins.ai.reecho.entity.resp.RoleList;
import tech.flless.huhubot.utils.StringUtil;


import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static tech.flless.huhubot.plugins.ai.reecho.ReechoClient.NameIdMap;

@BotPlugin("AI")
@Slf4j
@SuppressWarnings("unused")
public class AIPlugin {

    @Resource
    private ErnieClient ernieClient;

    @Resource
    private ErrieConfig errieConfig;

    @Resource
    private ReechoClient reechoClient;

    @Resource
    private ReechoConfig reechoConfig;


    @Resource
    private BotConfig botConfig;

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
                () -> content.set(event.getCommandArgs())
        );
        if (!StringUtil.hasLength(AccessToken)) {
            AccessToken = ernieClient.getToken(errieConfig.getClientId(), errieConfig.getClientSecret()).getAccessToken();
        }
        CompletionRes completion = ernieClient.getCompletion(AccessToken, new WxMessages(content.get()));
        event.reply(completion.getResult());

    }

    @MessageHandler(name = "睿声语音生成", keywords = "说")
    public void aiVoice(MessageEvent event) throws ExecutionException, InterruptedException, TimeoutException {
        Message message = event.getMessage();
        MessageInfo reply = event.getReply();
        String[] nameAndText = message.getPlainText().split("说", 2);

        String roleName = nameAndText[0].trim();
        if (!StringUtil.hasLength(roleName) || reechoClient.isNotRole(roleName)) return; //意外触发 ignore

        String text = reply != null ? reply.getMessage().plainText() : nameAndText[1].trim();
        if (!StringUtil.hasLength(text)) return;
        if (!botConfig.isSuperUser(event.getUserId()) && text.length() >= 102)
            throw new FinishedException("api额度有限，so字符长度须少于100, 或者vivo20给你加白名单");

        event.reply(Message.reply(event.getMessageId()).append(MessageSegment.text("正在合成语音中...")));
        event.reply(MessageSegment.record(reechoClient.generate(roleName, text)));
    }


    @MessageHandler(name = "睿声角色列表", keywords = "角色列表")
    public void list(MessageEvent event) {
        RoleList roleList = reechoClient.getVoiceList(reechoConfig.getApiKey());
        NameIdMap.clear();
        roleList.getData().forEach(role -> NameIdMap.put(role.getName(), role.getId()));
        StringBuilder sb = new StringBuilder();
        roleList.getData().forEach(d -> sb.append(d.getName()).append(" | "));
        event.reply(sb.toString());
    }


}
