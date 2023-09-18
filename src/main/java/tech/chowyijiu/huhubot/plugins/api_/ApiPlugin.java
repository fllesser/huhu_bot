package tech.chowyijiu.huhubot.plugins.api_;

import jakarta.annotation.Resource;
import tech.chowyijiu.huhubot.adapters.onebot.v11.entity.arr_message.Message;
import tech.chowyijiu.huhubot.adapters.onebot.v11.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import tech.chowyijiu.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.annotation.RuleCheck;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.plugins.api_.nbnhhsh.GuessPost;
import tech.chowyijiu.huhubot.plugins.api_.vvhan.VvhanApi;
import tech.chowyijiu.huhubot.utils.StringUtil;

import java.util.List;

/**
 * @author elastic chow
 * @date 26/7/2023
 */
@SuppressWarnings("unused")
@BotPlugin("huhubot-plugin-api")
public class ApiPlugin {
    @Resource
    private VvhanApi vvhanApi;

    @RuleCheck(rule = RuleEnum.superuser)
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
        List<String> trans = StringUtil.hasLength(word, GuessPost::guess);
        if (trans != null && trans.size() > 0) {
            StringBuilder sb = new StringBuilder(StringUtil.manMachine(word + ": "));
            trans.forEach(str -> sb.append(" ").append(str));
            event.sendMessage(sb.toString());
        }
    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "摸鱼人日历", commands = "摸鱼")
    public void moyu(MessageEvent event) {
        event.sendMessage(MessageSegment.image(vvhanApi.moyu().getUrl()));
    }

}
