package tech.chowyijiu.huhu_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NotifyNoticeHandler;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatcherContext {

    private final ApplicationContext ioc;

    private final List<Handler> handlerContainer = new ArrayList<>();

    @PostConstruct
    public void loadPlugin() {
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        List<Handler> tempHandlerContainer = new ArrayList<>();
        if (!botPluginMap.isEmpty()) {
            log.info("[HuHu Bot] Start Load Plugin...");
            int count = 1;
            for (String pluginName : botPluginMap.keySet()) {
                Object plugin = botPluginMap.get(pluginName);
                //插件功能名, 用于打印日志
                List<String> handlerNames = new ArrayList<>();
                Arrays.stream(plugin.getClass().getMethods())
                        .filter(this::isHandler)
                        .forEach(method -> {
                            Annotation annotation = method.getAnnotations()[0];
                            if (annotation instanceof MessageHandler) {
                                handlerNames.add(((MessageHandler) annotation).name());
                            } else if (annotation instanceof NoticeHandler) {
                                handlerNames.add(((NoticeHandler) annotation).name());
                            } else if (annotation instanceof NotifyNoticeHandler) {
                                handlerNames.add(((NotifyNoticeHandler) annotation).name());
                            }
                            tempHandlerContainer.add(new Handler(plugin, method));
                        });
                log.info("[DispatcherContext] Load plugin [{}], progress[{}/{}], function set: {}",
                        pluginName, count++, botPluginMap.size(), Arrays.toString(handlerNames.toArray()));
            }
        }
        if (tempHandlerContainer.isEmpty()) {
            throw new RuntimeException("No plugins were found");
        }
        //根据weight对handler进行排序, 并全部加入到handlerContainer中
        handlerContainer.addAll(tempHandlerContainer.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority))
                .collect(Collectors.toList()));
    }

    public void matchMessageHandler(final WebSocketSession session, final MessageEvent event) {
        log.info("[{}] {} start match handler", this.getClass().getSimpleName(), event.getClass().getSimpleName());
        outer:
        for (Handler handler : handlerContainer) {
            String[] commands = handler.commands;
            if (commands == null) {
                continue;
            }
            for (String command : commands) {
                if (event.getMessage().startsWith(command)) {
                    if (handler.eventType.isAssignableFrom(event.getClass())) {
                        log.info("[{}] matched handler, matchCommand[{}]", this.getClass().getSimpleName(), command);
                        handler.execute(session, event);
                        if (handler.block) {
                            //停止向下传递
                            break outer;
                        }
                    }
                }
            }
        }

    }


    static class Handler {
        private final Object plugin;
        private final Method method;

        public Class<?> eventType = Event.class;
        public int priority;
        public boolean block;

        public String[] commands;

        public String noticeType;
        public String subType;

        public Handler(Object plugin, Method method) {
            this.plugin = plugin;
            this.method = method;
            fillFields();
        }

        private void fillFields() {
            for (Class<?> clazz : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(clazz)) {
                    eventType = clazz;
                    break;
                }
            }
            Annotation annotation = (this.method.getAnnotations())[0];
            if (annotation instanceof MessageHandler) {
                MessageHandler mh = (MessageHandler) annotation;
                this.commands = mh.commands();
                this.priority = mh.priority();
                this.block = mh.block();
            } else if (annotation instanceof NoticeHandler) {
                NoticeHandler nh = (NoticeHandler) annotation;
                this.noticeType = nh.type().name();
                this.priority = nh.priority();
            } else if (annotation instanceof NotifyNoticeHandler) {
                NotifyNoticeHandler nnh = (NotifyNoticeHandler) annotation;
                this.subType = nnh.subType().name();
                this.priority = nnh.priority();
            }
        }

        public Object execute(Object... args) {
            Object result = null;
            try {
                result = method.invoke(plugin, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return result;
        }

    }


    public boolean isHandler(Method method) {
        return method.isAnnotationPresent(MessageHandler.class)
                || method.isAnnotationPresent(NoticeHandler.class)
                || method.isAnnotationPresent(NotifyNoticeHandler.class);
    }


}
