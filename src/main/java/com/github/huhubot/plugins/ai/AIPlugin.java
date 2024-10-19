package com.github.huhubot.plugins.ai;

import com.github.huhubot.adapters.onebot.v11.constant.SubTypeEnum;
import com.github.huhubot.adapters.onebot.v11.event.notice.NotifyNoticeEvent;
import com.github.huhubot.core.annotation.NoticeHandler;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.core.rule.RuleEnum;
import com.github.huhubot.plugins.ai.reecho.entity.resp.AccountInfo;
import com.github.huhubot.plugins.ai.smart_reply.WordsDict;
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
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;


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
    public void aiVoice(GroupMessageEvent event) throws ExecutionException, InterruptedException {
        Message message = event.getMessage();
        MessageInfo reply = event.getReply();
        String[] nameAndText = message.getPlainText().split("说", 2);

        String roleName = nameAndText[0].trim();
        if (!StringUtil.hasLength(roleName) || isNotRole(roleName)) return; //意外触发 ignore

        String text = reply != null ? reply.getMessage().plainText() : nameAndText[1].trim();
        if (!StringUtil.hasLength(text)) return;
        if (!botConfig.isSuperUser(event.getUserId()) && text.length() >= 120)
            throw new FinishedException("Api 额度有限，单次调用字数须少于 40, 或者 vivo 20 给你加白名单");

        Bot bot = event.getBot();
        Future<Integer> messageId = bot.asyncSendGroupMessage(event.getGroupId(),
                Message.reply(event.getMessageId()).append(MessageSegment.text("正在合成语音中...")));
        String audioUrl = reechoClient.generate(voiceMap.get(roleName), text);
        event.reply(MessageSegment.record(audioUrl));
        bot.deleteMsg(messageId.get());
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


    @NoticeHandler(name = "语音回应戳一戳", priority = 0)
    public void replyPoke(NotifyNoticeEvent event) {
        if (event.getGroupId() == null) return;
        Bot bot = event.getBot();
        if (!SubTypeEnum.poke.name().equals(event.getSubType()) //不是戳一戳事件
                || !bot.getSelfId().equals(event.getTargetId()) //被戳的不是bot
                || bot.getSelfId().equals(event.getUserId())    //是bot号自己戳的
        ) return;
        double random = Math.random();
        if (random < 0.5) {
            String willSend = WordsDict.randWord();
            bot.sendGroupMessage(event.getGroupId(), willSend);
        } else {
            MessageSegment willSend = MessageSegment.record("file://" + WordsDict.randVoice());
            bot.sendGroupMessage(event.getGroupId(), willSend);
        }

    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "获取账户信息", commands = "账户")
    public void credits(MessageEvent event) {
        AccountInfo accountInfo = reechoClient.getAccountInfo(reechoConfig.authorization());
        event.reply("剩余点数：" + accountInfo.getUser().getCredits());
    }


}
