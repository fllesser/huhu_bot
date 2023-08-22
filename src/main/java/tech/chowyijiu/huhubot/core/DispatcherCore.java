package tech.chowyijiu.huhubot.core;

import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.annotation.NoticeHandler;
import tech.chowyijiu.huhubot.core.constant.ANSI;
import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.event.notice.NoticeEvent;
import tech.chowyijiu.huhubot.core.exception.ActionFailed;
import tech.chowyijiu.huhubot.core.exception.FinishedException;
import tech.chowyijiu.huhubot.core.rule.Rule;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.core.ws.Bot;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatcherCore {

    private final ApplicationContext ioc;

    private List<Handler> MESSAGE_HANDLER_CONTAINER;
    private List<Handler> NOTICE_HANDLER_CONTAINER;

    //private final Map<String, Handler> HANDLER_CALL_RECORD = new HashMap<>();

    @PostConstruct
    private void loadPlugin() {
        //获取所有插件Bean
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        //创建两个临时存储的List
        List<Handler> messageHandlers = new ArrayList<>();
        List<Handler> noticeHandlers = new ArrayList<>();
        if (!botPluginMap.isEmpty()) {
            log.info("{}huhubot starts to load plugins...{}", ANSI.YELLOW, ANSI.RESET);
            int count = 1;
            StringBuilder sb;
            //容器中的插件Bean
            Object plugin;
            for (String pluginName : botPluginMap.keySet()) {
                plugin = botPluginMap.get(pluginName);
                //插件功能名, 用于打印日志
                sb = new StringBuilder();
                for (Method method : plugin.getClass().getMethods()) {
                    Handler handler;
                    if (method.isAnnotationPresent(MessageHandler.class)) {
                        handler = Handler.buildMessageHandler(plugin, method);
                        messageHandlers.add(handler);
                    } else if (method.isAnnotationPresent(NoticeHandler.class)) {
                        handler = Handler.buildNoticeHandler(plugin, method);
                        noticeHandlers.add(handler);
                    } else continue;
                    sb.append(handler.name).append(" ");
                }
                log.info("{}huhubot succeeded to load plugin {}, progress:{}/{}, function:{}{}",
                        ANSI.YELLOW, pluginName, count++, botPluginMap.size(), sb, ANSI.RESET);
            }
        }
        if (messageHandlers.isEmpty() && noticeHandlers.isEmpty()) {
            log.info("{}No plugin was found{}", ANSI.YELLOW, ANSI.RESET);
            return;
        }
        //根据priority对handler进行排序, 并全部加入到handlerContainer中
        MESSAGE_HANDLER_CONTAINER = List.copyOf(messageHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority)).toList());
        NOTICE_HANDLER_CONTAINER = List.copyOf(noticeHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority)).toList());
        log.info("{}running huhubot...{}", ANSI.YELLOW, ANSI.RESET);
    }

    public void onMessage(final Bot bot, final MessageEvent event) {
        event.getMessage().plainText();
        for (Handler handler : MESSAGE_HANDLER_CONTAINER) {
            //判断事件类型
            if (!handler.match(event.getClass())) continue;
            //因为注解中的commands和keywords 默认为{}, 无需判空
            if (handler.commands.length > 0) {
                if (handler.matchCommand(bot, event)) break;
            } else if (handler.keywords.length > 0) {
                if (handler.matchKeyword(bot, event)) break;
            }
        }
    }


    public void onNotice(final Bot bot, final NoticeEvent event) {
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (handler.match(event.getClass())) {
                handler.execute(bot, event);
                if (handler.block) break;
            }
        }
    }

    //@Deprecated
    //public void onRequest(final Bot bot, final RequestEvent event) {
    //    for (Handler handler : new ArrayList<Handler>()) {
    //        if (handler.match(event.getClass())) {
    //            handler.execute(bot, event);
    //            break;
    //        }
    //    }
    //}

    @Builder
    static class Handler {
        private final Object plugin; //ioc容器中的插件Bean
        private final Method method;

        private Class<?> eventType;  //用于isAssignableFrom 匹配事件类型
        private String name;         //Handler注解里的name
        private int priority;
        private boolean block;       //false 为不阻断

        //MessageHandler
        private String[] commands;
        private String[] keywords;

        private Rule rule;

        private void initEventType() {
            //在method的形参中定位事件类型
            for (Class<?> clazz : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(clazz)) {
                    eventType = clazz;
                    break;
                }
            }
        }

        private void initRule(RuleEnum rule) {
            if (!rule.equals(RuleEnum.default_)) {
                this.rule = rule.getRule();
            } else {
                String RuleName = method.getName() + "Rule";
                Field field = null;
                for (Field f : plugin.getClass().getDeclaredFields()) {
                    if (RuleName.equals(f.getName())) {
                        field = f;
                        break;
                    }
                }
                if (field != null) {
                    field.setAccessible(true);
                    Object obj = null;
                    try {
                        obj = field.get(plugin);
                    } catch (IllegalAccessException ignored) {
                    }
                    if (obj instanceof Rule) this.rule = (Rule) obj;
                } else {
                    this.rule = null;
                }
            }
        }

        public static Handler buildMessageHandler(Object plugin, Method method) {
            MessageHandler mh = method.getAnnotation(MessageHandler.class);
            Handler handler = Handler.builder().plugin(plugin).method(method).name(mh.name()).block(mh.block())
                    .priority(mh.priority()).commands(mh.commands()).keywords(mh.keywords()).build();
            handler.initEventType();
            handler.initRule(mh.rule());
            return handler;
        }

        public static Handler buildNoticeHandler(Object plugin, Method method) {
            NoticeHandler nh = method.getAnnotation(NoticeHandler.class);
            Handler handler = Handler.builder().plugin(plugin).method(method).name(nh.name())
                    .priority(nh.priority()).build();
            handler.initEventType();
            handler.initRule(nh.rule());
            return handler;
        }

        /**
         * 命令匹配
         */
        private boolean matchCommand(final Bot bot, final MessageEvent event) {
            //String plainText = event.getMessage().plainText();
            String plainText = event.getMessage().getPlainText();
            //如果配置了命令前缀
            //if (BotConfig.commandPrefixes.size() > 0) {
            //    if (BotConfig.commandPrefixes.stream().map(Object::toString).noneMatch(plainText::startsWith)) {
            //        //配置了命令前缀, 没有命令前缀的消息, 直接结束这个handler的后续匹配
            //        return false;
            //    } else {
            //        plainText = plainText.substring(1);
            //    }
            //}
            for (String command : this.commands) {
                //匹配前缀命令
                if (plainText.startsWith(command)) {
                    //去除触发的command, 并去掉头尾空格
                    event.setCommandArgs(plainText.replaceFirst(command, "").trim());
                    this.execute(bot, event);
                    return this.block;
                }
            }
            return false;
        }

        /**
         * 关键词匹配
         */
        private boolean matchKeyword(final Bot bot, final MessageEvent event) {
            String plainText = event.getMessage().getPlainText();
            for (String keyword : this.keywords) {
                if (plainText.contains(keyword)) {
                    this.execute(bot, event);
                    return this.block; //
                }
            }
            return false;
        }

        public void execute(final Bot bot, final Event event) {
            try {
                if (rule != null && !rule.check(bot, event)) return;
                log.info("{}{} will be handled by Plugin[{}] Function[{}] Priority[{}]{}"
                        , ANSI.YELLOW, event, this.plugin.getClass().getSimpleName()
                        , this.name, this.priority, ANSI.RESET);
                this.method.invoke(this.plugin, bot, event);
            } catch (IllegalAccessException e) {
                log.info("{}IllegalAccessException: {}{}", ANSI.RED, "handler method must be public", ANSI.RESET);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof FinishedException) {
                    log.info("{} FinishedException ignored", event);
                } else if (targetException instanceof ActionFailed) {
                    log.info("{}ActionFailed: {}{}", ANSI.RED, targetException.getMessage(), ANSI.RESET);
                } else {
                    targetException.printStackTrace();
                }

            }
        }


        public boolean match(Class<? extends Event> eventClass) {
            return eventType.isAssignableFrom(eventClass);
        }
    }

}
