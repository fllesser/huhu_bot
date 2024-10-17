package com.github.huhubot.plugins.ai;

import com.github.huhubot.adapters.onebot.v11.constant.SubTypeEnum;
import com.github.huhubot.adapters.onebot.v11.event.notice.NotifyNoticeEvent;
import com.github.huhubot.core.annotation.NoticeHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.github.huhubot.adapters.onebot.v11.bot.Bot;
import com.github.huhubot.adapters.onebot.v11.entity.message.Message;
import com.github.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import com.github.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import com.github.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.config.BotConfig;
import com.github.huhubot.config.ErrieConfig;
import com.github.huhubot.config.ReechoConfig;
import com.github.huhubot.core.annotation.BotPlugin;
import com.github.huhubot.core.annotation.MessageHandler;
import com.github.huhubot.core.exception.FinishedException;
import com.github.huhubot.plugins.ai.errie.entity.CompletionRes;
import com.github.huhubot.plugins.ai.errie.ErnieClient;
import com.github.huhubot.plugins.ai.errie.entity.WxMessages;
import com.github.huhubot.plugins.ai.reecho.ReechoClient;
import com.github.huhubot.plugins.ai.reecho.entity.resp.RoleList;
import com.github.huhubot.utils.StringUtil;


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
            throw new FinishedException("api 额度有限，so 字符长度须少于100, 或者 vivo 20给你加白名单");

        Bot bot = event.getBot();
        Future<Integer> messageId = bot.asyncSendGroupMessage(event.getGroupId(),
                Message.reply(event.getMessageId()).append(MessageSegment.text("正在合成语音中...")));
        try {
            String audioUrl = reechoClient.generate(voiceMap.get(roleName), text);
            event.reply(MessageSegment.record(audioUrl));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new FinishedException("请求超时");
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


    @NoticeHandler(name = "群内回戳", priority = 0)
    public void replyPoke(NotifyNoticeEvent event) {
        Bot bot = event.getBot();
        if (!SubTypeEnum.poke.name().equals(event.getSubType()) //不是戳一戳事件
                || !bot.getSelfId().equals(event.getTargetId()) //被戳的不是bot
                || bot.getSelfId().equals(event.getUserId())    //是bot号自己戳的
        ) return;
        bot.sendGroupMessage(event.getGroupId(), MessageSegment.poke(event.getUserId()));
    }


}
