package com.github.huhubot.plugins.ai;

import com.github.huhubot.adapters.onebot.v11.constant.SubTypeEnum;
import com.github.huhubot.adapters.onebot.v11.event.notice.NotifyNoticeEvent;
import com.github.huhubot.core.annotation.NoticeHandler;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.core.rule.RuleEnum;
import com.github.huhubot.plugins.ai.reecho.ReechoUtil;
import com.github.huhubot.plugins.ai.reecho.entity.resp.AccountInfo;
import com.github.huhubot.plugins.ai.smart_reply.WordsDict;
import lombok.RequiredArgsConstructor;
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
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@BotPlugin("AI")
@Slf4j
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class AIPlugin {


    private final ErnieClient ernieClient;
    private final ErrieConfig errieConfig;
    private final ReechoClient reechoClient;
    private final ReechoConfig reechoConfig;
    private final BotConfig botConfig;

    private String AccessToken;

    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "一言", commands = "ai")
    public void ai(GroupMessageEvent event) {
        Bot bot = event.getBot();
        MessageInfo reply = event.getReply();
        String text = event.getCommandArgs() + (reply != null ? reply.getMessage().plainText() : "");
        if (!StringUtil.hasLength(text)) return;
        if (!StringUtil.hasLength(AccessToken)) {
            AccessToken = ernieClient.getToken(errieConfig.getClientId(), errieConfig.getClientSecret()).getAccessToken();
        }
        CompletionRes completion = ernieClient.getCompletion(AccessToken, new WxMessages(text));
        MessageSegment innerNode = MessageSegment.node("最菜的文心四", bot.getSelfId(), MessageSegment.markdown(completion.getResult()));
        MessageSegment node = MessageSegment.node("最菜的文心四", bot.getSelfId(), Message.forward(List.of(innerNode)));
        Message forward = Message.forward(List.of(node));
        bot.sendGroupForwardMsg(event.getGroupId(), forward);
    }

    @MessageHandler(name = "睿声语音生成", keywords = "说")
    public void aiVoice(GroupMessageEvent event) throws ExecutionException, InterruptedException {
        Message message = event.getMessage();
        MessageInfo reply = event.getReply();
        String[] nameAndText = message.plainText().split("说", 2);

        String roleName = nameAndText[0].trim();
        if (!StringUtil.hasLength(roleName) || !ReechoUtil.contains(roleName)) return; //意外触发 ignore

        String text = reply != null ? reply.getMessage().plainText() : nameAndText[1].trim();
        if (!StringUtil.hasLength(text)) return;
        if (!botConfig.isSuperUser(event.getUserId()) && text.length() >= 120)
            throw new FinishedException("Api 额度有限，单次调用字数须少于 40, 或者 vivo 20 给你加白名单");

        Bot bot = event.getBot();
        Future<Integer> messageId = bot.asyncSendGroupMessage(event.getGroupId(),
                Message.reply(event.getMessageId()).append(MessageSegment.text("正在合成语音中...")));
        String audioUrl = reechoClient.generate(ReechoUtil.get(roleName), text);
        event.reply(MessageSegment.record(audioUrl));
        bot.deleteMsg(messageId.get());
    }


    @MessageHandler(name = "睿声角色列表", keywords = "角色列表")
    public void list(MessageEvent event) {
        RoleList roleList = reechoClient.getVoiceList(reechoConfig.authorization());
        ReechoUtil.update(roleList);
        Message message = new Message();
        message.append(MessageSegment.node("使用说明", event.getSelfId(), Message.text("角色+说+文字(支持回复别人的消息)")));
        roleList.getData().forEach(d -> {
            message.append(MessageSegment.node("角色信息", event.getSelfId(), Message.text(d.getName() + "\n" + d.getMetadata().getDescription())));
        });
        event.reply(message);
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
        Object willSend;
        if (random < 0.33) {
            bot.groupPoke(event.getGroupId(), event.getUserId());
        } else if (random < 0.66) {
            willSend = WordsDict.randWord();
            bot.sendGroupMessage(event.getGroupId(), willSend);
        } else {
            willSend = MessageSegment.record("file://" + WordsDict.randVoice());
            bot.sendGroupMessage(event.getGroupId(), willSend);
        }
    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "获取账户信息", commands = "点数")
    public void credits(MessageEvent event) {
        AccountInfo accountInfo = reechoClient.getAccountInfo(reechoConfig.authorization());
        event.reply("剩余点数：" + accountInfo.getUser().getCredits());
    }


}
