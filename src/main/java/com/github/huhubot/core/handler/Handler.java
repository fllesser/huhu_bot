package com.github.huhubot.core.handler;

import com.github.huhubot.adapters.onebot.v11.entity.message.Message;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import com.github.huhubot.config.GlobalConfig;
import com.github.huhubot.core.DispatcherCore;
import com.github.huhubot.core.annotation.MessageHandler;
import com.github.huhubot.core.annotation.NoticeHandler;
import com.github.huhubot.core.exception.FinishedException;
import com.github.huhubot.core.exception.PluginLoadException;
import com.github.huhubot.core.rule.Rule;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Handler {

    private Object plugin; //ioc容器中的插件Bean
    private Method method;

    private String name;         //Handler注解里的name
    private int priority;
    private boolean block;       //false 为不阻断

    //private Rule rule;

    public static Handler buildMessageHandler(Object plugin, Method method) {
        MessageHandler mh = method.getAnnotation(MessageHandler.class);
        Handler handler = null;
        if (mh.commands().length > 0) {
            handler = new CommandHandler(mh.commands());
        } else if (mh.keywords().length > 0) {
            handler = new KeywordHandler(mh.keywords());
        } else {
            throw new PluginLoadException("MessageHandler need to fill commands os keywords");
        }
        handler.setPlugin(plugin);
        handler.setMethod(method);
        handler.setPriority(mh.priority());
        handler.setBlock(mh.block());
        handler.setName(mh.name());
        return handler;
    }

    public static Handler buildNoticeHandler(Object plugin, Method method) {
        NoticeHandler nh = method.getAnnotation(NoticeHandler.class);
        Handler handler = new Handler();
        handler.setPlugin(plugin);
        handler.setMethod(method);
        handler.setPriority(nh.priority());
        return handler;
    }


    public boolean match(final MessageEvent event) {
        return false;
    }


    public boolean match(final NoticeEvent event) {
        execute(event);
        return block;
    }

    protected void execute(final Event event) {
        try {
            this.method.invoke(this.plugin, event);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException: handler method[{}] must be public", method.getName());
        } catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if (targetE instanceof FinishedException fe) {
                log.info("[{}] | {} Finished, Msg:{}, Event:{}", plugin.getClass().getSimpleName(), name, fe.getMessage(), event);
                if (event instanceof MessageEvent me)
                    me.reply(Message.reply(me.getMessageId()).append(fe.getMessage()));
            } else {
                StackTraceElement[] stackTrace = targetE.getStackTrace();
                log.error("{} | {} | {}", targetE.getClass().getSimpleName(), targetE.getMessage(), stackTrace[0]);
                Long testGroup = GlobalConfig.botCf.getTestGroup();
                if (testGroup != null) {
                    String willSend = """
                                [Event]
                                %s
                                ——————
                                [%s]
                                %s
                                ——————
                                [Stack Top]
                                %s
                                """;
                    event.getBot().sendGroupMessage(testGroup, willSend.formatted(event, targetE.getClass().getSimpleName(), targetE.getMessage(), stackTrace[0]));
                }

            }

        }
    }
}
