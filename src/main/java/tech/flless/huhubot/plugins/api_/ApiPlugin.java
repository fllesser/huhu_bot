package tech.flless.huhubot.plugins.api_;

import jakarta.annotation.Resource;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.Message;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.MessageSegment;
import tech.flless.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.config.ApiSpaceConfig;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;
import tech.flless.huhubot.core.annotation.RuleCheck;
import tech.flless.huhubot.core.rule.RuleEnum;
import tech.flless.huhubot.plugins.api_.api_sapce.ApiSpaceClient;
import tech.flless.huhubot.plugins.api_.api_sapce.ApiSpaceResult;
import tech.flless.huhubot.plugins.api_.nbnhhsh.NbnhhshClient;
import tech.flless.huhubot.plugins.api_.vvhan.VvhanClient;
import tech.flless.huhubot.utils.StringUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author elastic chow
 * @date 26/7/2023
 */
@SuppressWarnings("unused")
@BotPlugin("huhubot-plugin-api")
public class ApiPlugin {
    @Resource
    private VvhanClient vvhanClient;

    @Resource
    private NbnhhshClient nbnhhshClient;

    @Resource
    private ApiSpaceClient apiSpaceClient;

    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "缩写查询", commands = {"sx", "缩写"})
    public void sx(MessageEvent event) {
        String word = event.getCommandArgs();
        if (!StringUtil.hasLength(word)) {
            MessageInfo reply = event.getReply();
            if (reply != null) {
                Message message = reply.getMessage();
                message.plainText();
                word = StringUtil.getFirstLAN(message.getPlainText());
            }
        }
        List<String> trans = StringUtil.hasLength(word, nbnhhshClient::defaultGuess);
        if (trans != null && trans.size() > 0) {
            StringBuilder sb = new StringBuilder(StringUtil.manMachine(word + ": "));
            trans.forEach(str -> sb.append(" ").append(str));
            event.replyMessage(sb.toString());
        }
    }

    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "摸鱼人日历", commands = "摸鱼")
    public void moyu(MessageEvent event) {
        event.replyMessage(MessageSegment.image(vvhanClient.moyu().getUrl()));
    }

    //@RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "周公解梦", commands = "zgjm")
    public void zgjm(MessageEvent event) {
        if (!StringUtil.hasLength(event.getCommandArgs())) return;
        ApiSpaceResult result = apiSpaceClient.zgjm(ApiSpaceConfig.token, event.getCommandArgs());
        if (result.getResult() == null) {
            event.replyMessage("解梦失败");
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
        event.replyMessage(message);
    }

}
