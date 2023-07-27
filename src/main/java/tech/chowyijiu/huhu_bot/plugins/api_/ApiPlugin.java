package tech.chowyijiu.huhu_bot.plugins.api_;

import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.plugins.api_.nbnhhsh.GuessPost;
import tech.chowyijiu.huhu_bot.utils.StringUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.List;

/**
 * @author elastic chow
 * @date 26/7/2023
 */
@SuppressWarnings("unuesd")
@BotPlugin
public class ApiPlugin {

    @MessageHandler(name = "缩写查询", commands = {"sx", "缩写"}, rule = RuleEnum.superuser)
    public void sx(Bot bot, MessageEvent event) {
        String word = event.getCommandArgs();
        List<String> trans = StringUtil.hasLength(word, GuessPost::guess);
        if (trans != null && trans.size() > 0) {
            StringBuilder sb = new StringBuilder(StringUtil.manMachine(word + ": "));
            trans.forEach(str -> sb.append(" ").append(str));
            bot.sendMessage(event, sb.toString());
        }
    }
}
