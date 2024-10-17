package com.github.huhubot.core.base;

import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.core.DispatcherCore;
import com.github.huhubot.core.annotation.MessageHandler;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.core.rule.RuleEnum;
import com.github.huhubot.utils.IocUtil;
import com.github.huhubot.utils.StringUtil;

/**
 * @author flless
 * @date 13/9/2023
 */
//@BotPlugin("huhubot-plugin-base")
@Deprecated
@SuppressWarnings("unused")
public class BasePlugin {


    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "echo", commands = "echo")
    public void echo(MessageEvent event) {
        event.reply(event.getCommandArgs());
    }


    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "功能关闭", commands = "close")
    public void close(MessageEvent event) {
        DispatcherCore core = IocUtil.getBean(DispatcherCore.class);
        String arg = event.getCommandArgs();
        if (!StringUtil.isDigit(arg)) return;
        int no = Integer.parseInt(arg);
        event.reply("关闭" + arg + (core.logicClose(no) ? "成功" : "失败"));
    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "功能开启", commands = "open")
    public void open(MessageEvent event) {
        DispatcherCore core = IocUtil.getBean(DispatcherCore.class);
        String arg = event.getCommandArgs();
        if (!StringUtil.isDigit(arg)) return;
        int no = Integer.parseInt(arg);
        event.reply("开启" + arg + (core.logicOpen(no) ? "成功" : "失败"));
    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "功能集", commands = "list")
    public void list(MessageEvent event) {
        DispatcherCore core = IocUtil.getBean(DispatcherCore.class);
        String handlerNames = core.getHandlerNameList();
        event.reply(handlerNames);
    }

}
