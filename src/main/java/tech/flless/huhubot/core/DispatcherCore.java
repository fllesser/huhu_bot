package tech.flless.huhubot.core;

import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;
import tech.flless.huhubot.core.annotation.NoticeHandler;
import tech.flless.huhubot.core.constant.ANSI;
import tech.flless.huhubot.adapters.onebot.v11.event.Event;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import tech.flless.huhubot.core.exception.ActionFailed;
import tech.flless.huhubot.core.exception.FinishedException;
import tech.flless.huhubot.core.rule.Rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


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
            log.info("huhubot starts to load plugins...");
            int count = 1;
            StringBuilder pluginFuctionNames;
            //容器中的插件Bean
            Object plugin;
            for (String pluginName : botPluginMap.keySet()) {
                plugin = botPluginMap.get(pluginName);
                pluginFuctionNames = new StringBuilder();
                //开启aop后, 使用aop增强的Bean会变成代理类对象, 代理类不包含原始类的注解
                //所以需要使用AopUtils.getTargetClass()获取原始类
                for (Method method : AopUtils.getTargetClass(plugin).getDeclaredMethods()) {
                    Handler handler;
                    //取消检查
                    method.setAccessible(true);
                    if (method.isAnnotationPresent(MessageHandler.class)) {
                        handler = Handler.buildMessageHandler(plugin, method);
                        messageHandlers.add(handler);
                    } else if (method.isAnnotationPresent(NoticeHandler.class)) {
                        handler = Handler.buildNoticeHandler(plugin, method);
                        noticeHandlers.add(handler);
                    } else continue;
                    pluginFuctionNames.append(handler.name).append(" ");
                }
                log.info("huhubot succeeded to load plugin {}, progress:{}/{}, function:{}",
                        pluginName, count++, botPluginMap.size(), pluginFuctionNames);
            }
        }
        if (messageHandlers.isEmpty() && noticeHandlers.isEmpty()) {
            log.info("{}No plugin was found{}, this application will exit", ANSI.YELLOW, ANSI.RESET);
            System.exit(0);
            return;
        }
        //根据priority对handler进行排序, 并全部加入到handlerContainer中
        MESSAGE_HANDLER_CONTAINER = List.copyOf(messageHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority)).toList());
        NOTICE_HANDLER_CONTAINER = List.copyOf(noticeHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority)).toList());
        initHandlerNameMap();
        log.info("huhubot is running...");
    }

    public void onMessage(final MessageEvent event) {
        event.getMessage().plainText();
        for (Handler handler : MESSAGE_HANDLER_CONTAINER) {
            //判断事件类型
            if (!handler.match(event.getClass())) continue;
            if (!handler.status) continue; //是关闭的
            //因为注解中的commands和keywords 默认为{}, 无需判空
            if (handler.commands.length > 0) {
                if (handler.matchCommand(event)) break;
            } else if (handler.keywords.length > 0) {
                if (handler.matchKeyword(event)) break;
            }
        }
    }

    public void onNotice(final NoticeEvent event) {
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (!handler.status) continue;
            if (handler.matchNotice(event)) break;
        }
    }

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

        private boolean status;

        private void initEventType() {
            //在method的形参中定位事件类型
            for (Class<?> clazz : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(clazz)) {
                    this.eventType = clazz;
                    break;
                }
            }
        }

        public static Handler buildMessageHandler(Object plugin, Method method) {
            MessageHandler mh = method.getAnnotation(MessageHandler.class);
            Handler handler = Handler.builder().plugin(plugin).method(method).name(mh.name()).block(mh.block())
                    .priority(mh.priority()).commands(mh.commands()).keywords(mh.keywords()).status(true).build();
            handler.initEventType();
            return handler;
        }

        public static Handler buildNoticeHandler(Object plugin, Method method) {
            NoticeHandler nh = method.getAnnotation(NoticeHandler.class);
            Handler handler = Handler.builder().plugin(plugin).method(method).name(nh.name())
                    .priority(nh.priority()).status(true).build();
            handler.initEventType();
            return handler;
        }

        /**
         * 命令匹配
         */
        private boolean matchCommand(final MessageEvent event) {
            String plainText = event.getMessage().getPlainText();
            for (String command : this.commands) {
                //匹配前缀命令
                if (plainText.startsWith(command)) {
                    //去除触发的command, 并去掉头尾空格
                    event.setCommandArgs(plainText.replaceFirst(command, "").trim());
                    this.execute(event);
                    return this.block;
                }
            }
            return false;
        }

        /**
         * 关键词匹配
         */
        private boolean matchKeyword(final MessageEvent event) {
            String plainText = event.getMessage().getPlainText();
            for (String keyword : this.keywords) {
                if (plainText.contains(keyword)) {
                    this.execute(event);
                    return this.block; //
                }
            }
            return false;
        }

        private boolean matchNotice(final NoticeEvent event) {
            if (this.match(event.getClass())) {
                this.execute(event);
                return this.block;
            }
            return false;
        }

        private void execute(final Event event) {
            try {
                this.method.invoke(this.plugin, event);
            } catch (IllegalAccessException e) {
                log.info("{}IllegalAccessException: {}{}", ANSI.RED, "handler method must be public", ANSI.RESET);
            } catch (InvocationTargetException e) {
                Throwable targetE = e.getTargetException();
                if (targetE instanceof FinishedException fe) {
                    log.info("[{}] | {} Finished, msg:{}, event:{}", plugin.getClass().getSimpleName(), name, fe.getMsg(), event);
                    if (event instanceof MessageEvent messageEvent) messageEvent.replyMessage(fe.getMsg());
                } else if (targetE instanceof ActionFailed) {
                    log.info("{}ActionFailed: {}{}", ANSI.RED, targetE.getMessage(), ANSI.RESET);
                } else {
                    log.error(targetE.getMessage());
                }
            }
        }


        public boolean match(Class<? extends Event> eventClass) {
            return eventType.isAssignableFrom(eventClass);
        }
    }


    private final Map<Integer, String> handlerNameMap = new HashMap<>();

    private void initHandlerNameMap() {
        AtomicInteger i = new AtomicInteger(1);
        Stream.concat(MESSAGE_HANDLER_CONTAINER.stream(), NOTICE_HANDLER_CONTAINER.stream())
                .forEach(handler -> handlerNameMap.put(i.getAndIncrement(), handler.name));
    }

    public String getHandlerNameList() {
        StringBuilder sb = new StringBuilder();
        for (Integer integer : handlerNameMap.keySet()) {
            sb.append(integer).append(". ").append(handlerNameMap.get(integer)).append("\n");
        }
        return sb.toString();
    }

    /**
     * 逻辑关闭
     */
    @Deprecated
    public boolean logicClose(int no) {
        String handlerName = handlerNameMap.get(no);
        for (Handler handler : MESSAGE_HANDLER_CONTAINER) {
            if (handlerName.equals(handler.name)) {
                handler.status = false;
                return true;
            }
        }
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (handlerName.equals(handler.name)) {
                handler.status = false;
                return true;
            }
        }
        return false;
    }

    /**
     * 逻辑开启
     */
    @Deprecated
    public boolean logicOpen(int no) {
        String handlerName = handlerNameMap.get(no);
        for (Handler handler : MESSAGE_HANDLER_CONTAINER) {
            if (handlerName.equals(handler.name)) {
                handler.status = true;
                return true;
            }
        }
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (handlerName.equals(handler.name)) {
                handler.status = true;
                return true;
            }
        }
        return false;
    }
}
