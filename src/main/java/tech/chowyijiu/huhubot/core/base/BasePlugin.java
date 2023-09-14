package tech.chowyijiu.huhubot.core.base;

import tech.chowyijiu.huhubot.core.DispatcherCore;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.annotation.RuleCheck;
import tech.chowyijiu.huhubot.core.aop.rule.RuleEnum;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.utils.IocUtil;

/**
 * @author flless
 * @date 13/9/2023
 */
@BotPlugin("huhubot-plugin-base")
public class BasePlugin {


    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "echo", commands = "echo")
    public void echo(MessageEvent event) {
        event.getBot().sendMessage(event, event.getCommandArgs());
    }


    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "功能关闭", commands = "off")
    public void close(MessageEvent event) {
        DispatcherCore core = IocUtil.getBean(DispatcherCore.class);
        String arg = event.getCommandArgs();
        event.getBot().sendMessage(event, "关闭" + arg + (core.logicClose(arg) ? "成功" : "失败"));
    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "功能开启", commands = "on")
    public void open(MessageEvent event) {
        DispatcherCore core = IocUtil.getBean(DispatcherCore.class);
        String arg = event.getCommandArgs();
        event.getBot().sendMessage(event, "开启" + arg + (core.logicClose(arg) ? "成功" : "失败"));
    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "功能集", commands = "list")
    public void list(MessageEvent event) {
        DispatcherCore core = IocUtil.getBean(DispatcherCore.class);
        String handlerNames = core.getHandlerNames();
        event.getBot().sendMessage(event, handlerNames);
    }
}
