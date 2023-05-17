package tech.chowyijiu.huhu_bot.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NotifyNoticeHandler;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.NoticeEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import javax.annotation.PostConstruct;
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
public class DispatcherCore {

    private final ApplicationContext ioc;

    private final List<Handler> MESSAGE_HANDLER_CONTAINER = new ArrayList<>();
    private final List<Handler> NOTICE_HANDLER_CONTAINER = new ArrayList<>();

    @PostConstruct
    private void loadPlugin() {
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        List<Handler> messageHandlers = new ArrayList<>();
        List<Handler> noticeHandlers = new ArrayList<>();
        if (!botPluginMap.isEmpty()) {
            log.info("[DispatcherCore] Start Load Plugin...");
            int count = 1;
            for (String pluginName : botPluginMap.keySet()) {
                Object plugin = botPluginMap.get(pluginName);
                //插件功能名, 用于打印日志
                List<String> handlerNames = new ArrayList<>();
                Arrays.stream(plugin.getClass().getMethods()).forEach(method -> {
                    if (method.isAnnotationPresent(MessageHandler.class)) {
                        Handler handler = Handler.buildMessageHandler(plugin, method);
                        handlerNames.add(handler.name);
                        messageHandlers.add(handler);
                    } else if (method.isAnnotationPresent(NoticeHandler.class)) {
                        Handler handler = Handler.buildNoticeHandler(plugin, method);
                        handlerNames.add(handler.name);
                        noticeHandlers.add(handler);
                    } else if (method.isAnnotationPresent(NotifyNoticeHandler.class)) {
                        Handler handler = Handler.buildNotifyNoticeHandler(plugin, method);
                        handlerNames.add(handler.name);
                        noticeHandlers.add(handler);
                    }
                });
                log.info("[DispatcherCore] Load plugin [{}], progress[{}/{}], function set: {}",
                        pluginName, count++, botPluginMap.size(), Arrays.toString(handlerNames.toArray()));
            }
        }
        if (messageHandlers.isEmpty() && noticeHandlers.isEmpty()) {
            throw new RuntimeException("[DispatcherCore] No plugins were found");
        }
        //根据weight对handler进行排序, 并全部加入到handlerContainer中
        MESSAGE_HANDLER_CONTAINER.addAll(messageHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority))
                .collect(Collectors.toList())
        );
        NOTICE_HANDLER_CONTAINER.addAll(noticeHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority))
                .collect(Collectors.toList()));
    }

    public void matchMessageHandler(final Bot bot, final MessageEvent event) {
        String briefMessage = event.getMessage().length() <= 10 ? event.getMessage()
                : (event.getMessage().substring(0, 10) + "......");
        log.info("[{}] {}[user_id:{},message:{}] start match handler",
                this.getClass().getSimpleName(), event.getClass().getSimpleName(),
                event.getUserId(), briefMessage);
        outer:
        for (Handler handler : MESSAGE_HANDLER_CONTAINER) {
            String[] commands = handler.commands;
            if (commands == null) {
                continue;
            }
            for (String command : commands) {
                if (event.getMessage().startsWith(command)) {
                    if (handler.eventType.isAssignableFrom(event.getClass())) {
                        log.info("[DispatcherCore] {}[user_id:{},message:{}] will be handled by Plugin[{}], Command[{}], Priority[{}]",
                                event.getClass().getSimpleName(), event.getUserId(), briefMessage,
                                handler.plugin.getClass().getSimpleName(), command, handler.priority);
                        handler.execute(bot, event);
                        if (handler.block) {
                            //停止向低优先级传递
                            break outer;
                        }
                    }
                }
            }
        }
        log.info("[{}] {}[user_id:{}, message:{}] match handler end",
                this.getClass().getSimpleName(), event.getClass().getSimpleName(),
                event.getUserId(), briefMessage);
    }

    public void matchNoticeHandler(final Bot bot, final NoticeEvent event) {
        String noticeType = event.getNoticeType();
        String subtype = event.getSubType();
        log.info("[{}] NoticeEvent[type:{}, subtype:{}] start match handler",
                this.getClass().getSimpleName(), noticeType, subtype);
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (Objects.equals(handler.noticeType, noticeType)) {
                if (Objects.equals(noticeType, NoticeTypeEnum.notify.name())) {
                    if (Objects.equals(handler.subType, subtype)) {
                        log.info("[DispatcherCore] NoticeEvent[notice_type:{},sub_type:{}] will be handled by Plugin[{}] Function[{}] Priority[{}]",
                                noticeType, subtype, handler.plugin.getClass().getSimpleName(), handler.name, handler.priority);
                        handler.execute(bot, event);
                        if (handler.block) {
                            break;
                        }
                    }
                } else {
                    log.info("[DispatcherCore] NoticeEvent[notice_type:{}] will be handled by Plugin[{}] Function[{}] Priority[{}]",
                            noticeType, handler.plugin.getClass().getSimpleName(), handler.name, handler.priority);
                    handler.execute(bot, event);
                    if (handler.block) {
                        break;
                    }
                }
            }
        }
    }



    static class Handler {
        private final Object plugin;
        private final Method method;

        //用于 isAssignableFrom 匹配事件类型
        public Class<?> eventType = Event.class;
        public String name;
        public int priority;
        public boolean block;

        //MessageHandler
        public String[] commands;

        //NoticeHandler
        public String noticeType;

        //NotifyNoticeHandler
        public String subType;

        private Handler(Object plugin, Method method) {
            this.plugin = plugin;
            this.method = method;
            for (Class<?> clazz : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(clazz)) {
                    eventType = clazz;
                    break;
                }
            }
        }

        public static Handler buildMessageHandler(Object plugin, Method method) {
            Handler handler = new Handler(plugin, method);
            MessageHandler mh = method.getAnnotation(MessageHandler.class);
            handler.name = mh.name();
            handler.block = mh.block();
            handler.priority = mh.priority();
            handler.commands = mh.commands();
            return handler;
        }

        public static Handler buildNoticeHandler(Object plugin, Method method) {
            Handler handler = new Handler(plugin, method);
            NoticeHandler nh = method.getAnnotation(NoticeHandler.class);
            handler.name = nh.name();
            handler.priority = nh.priority();
            handler.noticeType = nh.type().name();
            return handler;
        }

        public static Handler buildNotifyNoticeHandler(Object plugin, Method method) {
            Handler handler = new Handler(plugin, method);
            NotifyNoticeHandler nnh = method.getAnnotation(NotifyNoticeHandler.class);
            handler.name = nnh.name();
            handler.priority = nnh.priority();
            handler.subType = nnh.subType().name();
            return handler;
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

}
