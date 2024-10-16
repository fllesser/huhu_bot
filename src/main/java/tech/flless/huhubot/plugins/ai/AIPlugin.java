package tech.flless.huhubot.plugins.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import tech.flless.huhubot.adapters.onebot.v11.bot.Bot;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.Message;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import tech.flless.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import tech.flless.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.config.BotConfig;
import tech.flless.huhubot.config.ErrieConfig;
import tech.flless.huhubot.config.GlobalConfig;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;


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
    public void aiVoice(GroupMessageEvent event) {
        Message message = event.getMessage();
        MessageInfo reply = event.getReply();
        String[] nameAndText = message.getPlainText().split("说", 2);

        String roleName = nameAndText[0].trim();
        if (!StringUtil.hasLength(roleName) || isNotRole(roleName)) return; //意外触发 ignore

        String text = reply != null ? reply.getMessage().plainText() : nameAndText[1].trim();
        if (!StringUtil.hasLength(text)) return;
        if (!botConfig.isSuperUser(event.getUserId()) && text.length() >= 102)
            throw new FinishedException("api额度有限，so字符长度须少于100, 或者vivo20给你加白名单");

        Bot bot = event.getBot();
        Future<Integer> messageId = bot.asyncSendGroupMessage(event.getGroupId(),
                Message.reply(event.getMessageId()).append(MessageSegment.text("正在合成语音中...")));
        try {
            String audioUrl = reechoClient.generate(voiceMap.get(roleName), text);
            event.reply(MessageSegment.record(audioUrl));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new FinishedException("未知错误: " + e.getClass().toString());
        } finally {
            try {
                bot.deleteMsg(messageId.get());
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }

    }


    //name:id
    private final Map<String, String> voiceMap = new ConcurrentHashMap<>();

    @MessageHandler(name = "睿声角色列表", keywords = "角色列表")
    public void list(MessageEvent event) {
        RoleList roleList = reechoClient.getVoiceList(reechoConfig.authorization());
        voiceMap.clear();
        roleList.getData().forEach(role -> voiceMap.put(role.getName(), role.getId()));
        StringBuilder sb = new StringBuilder();
        roleList.getData().forEach(d -> sb.append(d.getName()).append(" | "));
        event.reply(sb.toString());
    }

    private boolean isNotRole(String name) {
        if (voiceMap.isEmpty()) {
            RoleList roleList = reechoClient.getVoiceList();
            roleList.getData().forEach(role -> voiceMap.put(role.getName(), role.getId()));
        }
        return !voiceMap.containsKey(name);
    }



}
