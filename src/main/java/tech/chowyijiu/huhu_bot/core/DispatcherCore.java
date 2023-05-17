package tech.chowyijiu.huhu_bot.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NotifyNoticeHandler;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.NoticeEvent;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private final List<Handler> MESSAGE_HANDLER_CONTAINER = new CopyOnWriteArrayList<>();
    private final List<Handler> NOTICE_HANDLER_CONTAINER = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void loadPlugin() {
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
                        MessageHandler annotation = method.getAnnotation(MessageHandler.class);
                        handlerNames.add(annotation.name());
                        messageHandlers.add(new Handler(plugin, method));
                    } else if (method.isAnnotationPresent(NoticeHandler.class)) {
                        NoticeHandler annotation = method.getAnnotation(NoticeHandler.class);
                        handlerNames.add(annotation.name());
                        noticeHandlers.add(new Handler(plugin, method));
                    } else if (method.isAnnotationPresent(NotifyNoticeHandler.class)) {
                        NotifyNoticeHandler annotation = method.getAnnotation(NotifyNoticeHandler.class);
                        handlerNames.add(annotation.name());
                        noticeHandlers.add(new Handler(plugin, method));
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

    public void matchMessageHandler(final WebSocketSession session, final MessageEvent event) {
        log.info("[{}] {}[user_id:{},message:{}] start match handler",
                this.getClass().getSimpleName(), event.getClass().getSimpleName(),
                event.getUserId(), event.getMessage());
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
                                event.getClass().getSimpleName(), event.getUserId(), event.getMessage(),
                                handler.plugin.getClass().getSimpleName(), command, handler.priority);
                        handler.execute(session, event);
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
                event.getUserId(), event.getMessage());
    }

    /**
     * todo
     */
    public void matchNoticeHandler(final WebSocketSession session, final NoticeEvent event) {
        String noticeType = event.getNoticeType();
        String subtype = event.getSubType();
        log.info("[{}] NoticeEvent[type:{}, subtype:{}] start match handler",
                this.getClass().getSimpleName(), noticeType, subtype);
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (Objects.equals(handler.noticeType, noticeType)) {
                if (Objects.equals(noticeType, NoticeTypeEnum.notify.name())) {
                    if (Objects.equals(handler.subType, subtype)) {
                        log.info("[DispatcherCore] NoticeEvent[notice_type:{},sub_type:{}] will be handled by Plugin[{}], Priority[{}]",
                                noticeType, subtype, handler.plugin.getClass().getSimpleName(), handler.priority);
                        handler.execute(session, event);
                        if (handler.block) {
                            break;
                        }
                    }
                } else {
                    log.info("[DispatcherCore] NoticeEvent[notice_type:{}] will be handled by Plugin[{}], Priority[{}]",
                            noticeType, handler.plugin.getClass().getSimpleName(), handler.priority);
                    handler.execute(session, event);
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


}
