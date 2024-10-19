package com.github.huhubot.plugins.api;

import jakarta.annotation.Resource;
import com.github.huhubot.adapters.onebot.v11.entity.message.Message;
import com.github.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import com.github.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.config.GlobalConfig;
import com.github.huhubot.core.annotation.BotPlugin;
import com.github.huhubot.core.annotation.MessageHandler;
import com.github.huhubot.plugins.api.api_sapce.ApiSpaceClient;
import com.github.huhubot.plugins.api.api_sapce.ApiSpaceResult;
import com.github.huhubot.plugins.api.nbnhhsh.NbnhhshClient;
import com.github.huhubot.plugins.api.vvhan.VvhanClient;
import com.github.huhubot.utils.StringUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author elastic chow
 * &#064;date  26/7/2023
 */
@SuppressWarnings("unused")
@BotPlugin("API")
@RequiredArgsConstructor
public class ApiPlugin {

    private final VvhanClient vvhanClient;
    private final NbnhhshClient nbnhhshClient;
    private final ApiSpaceClient apiSpaceClient;


    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "缩写查询", commands = {"sx", "缩写"})
    public void sx(MessageEvent event) {
        String word = event.getCommandArgs();
        if (!StringUtil.hasLength(word)) {
            MessageInfo reply = event.getReply();
            if (reply != null) {
                Message message = reply.getMessage();
                message.plainText();
                word = StringUtil.getFirstLAN(message.plainText());
            }
        }
        List<String> trans = StringUtil.hasLength(word, nbnhhshClient::defaultGuess);
        if (trans != null && !trans.isEmpty()) {
            StringBuilder sb = new StringBuilder(StringUtil.manMachine(word + ": "));
            trans.forEach(str -> sb.append(" ").append(str));
            event.reply(sb.toString());
        }
    }

    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "摸鱼人日历", commands = "摸鱼")
    public void moyu(MessageEvent event) {
        event.reply(MessageSegment.image(vvhanClient.moyu().getUrl()));
    }

    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "周公解梦", commands = "zgjm")
    public void zgjm(MessageEvent event) {
        if (!StringUtil.hasLength(event.getCommandArgs())) return;
        ApiSpaceResult result = apiSpaceClient.zgjm(GlobalConfig.apiSpaceCf.getToken(), event.getCommandArgs());
        if (result.getResult() == null) {
            event.reply("解梦失败");
            return;
        }
        String content = result.getResult()[0].get("content");
        String message = "解梦失败";
        if (StringUtil.hasLength(content)) {
            String pattern = "<p>(.*?)</p>";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(content);
            StringBuilder sb = new StringBuilder();
            while (m.find()) {
                sb.append("\n").append(m.group(1));
            }
            message = "解梦结果: " + sb;
        }
        event.reply(message);
    }
}
