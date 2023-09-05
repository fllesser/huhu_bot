package tech.chowyijiu.huhubot.plugins.api_;

import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.plugins.api_.nbnhhsh.GuessPost;
import tech.chowyijiu.huhubot.utils.StringUtil;

import java.util.List;

/**
 * @author elastic chow
 * @date 26/7/2023
 */
@SuppressWarnings("unuesd")
@BotPlugin("huhubot-plugin-api")
public class ApiPlugin {

    @MessageHandler(name = "缩写查询", commands = {"sx", "缩写"}, rule = RuleEnum.superuser)
    public void sx(MessageEvent event) {
        String word = event.getCommandArgs();
        List<String> trans = StringUtil.hasLength(word, GuessPost::guess);
        if (trans != null && trans.size() > 0) {
            StringBuilder sb = new StringBuilder(StringUtil.manMachine(word + ": "));
            trans.forEach(str -> sb.append(" ").append(str));
            event.getBot().sendMessage(event, sb.toString());
        }
    }
}
