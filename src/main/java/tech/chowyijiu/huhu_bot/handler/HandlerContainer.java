package tech.chowyijiu.huhu_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.event.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.PrivateMessageEvent;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerContainer {

    private final ApplicationContext ioc;

    private final Map<String[], Handler> container = new HashMap<>();

    @PostConstruct
    public void loadPlugin() {
        //Map<String, Object> botPluginMap = IocUtil.getBeansWithAnnotation(BotPlugin.class);
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
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
                            MessageHandler annotation = method.getAnnotation(MessageHandler.class);
                            handlerNames.add(annotation.name());
                            putHandler(annotation.command(), plugin, method, annotation);
                        });
                log.info("[HandlerContainer] Load plugin [{}], progress[{}/{}], function set: {}",
                        pluginName, count++, botPluginMap.size(), Arrays.toString(handlerNames.toArray()));
            }
        }
        if (container.isEmpty()) {
            throw new RuntimeException("No plugins were found");
        }

    }

    public Object matchMessageHandler(final WebSocketSession session, final MessageEvent event) {
        log.info("[{}] {} start match handler", this.getClass().getSimpleName(), event.getClass().getSimpleName());
        if (event instanceof GroupMessageEvent || event instanceof PrivateMessageEvent) {
            for (String[] commands : container.keySet()) {
                for (String command : commands) {
                    if (event.getMessage().startsWith(command)) {
                        Handler handler = container.get(commands);
                        log.info("{}, {}", handler.eventType, event.getClass());
                        if (handler.eventType.isAssignableFrom(event.getClass())) {
                            log.info("[{}] matched handler", this.getClass().getSimpleName());
                            handler.execute(session, event);
                            // TODO handler中添加 bloke 属性进行阻断
                            // break;
                        }
                    }
                }
            }
        }
        return null;
    }


    static class Handler {
        private final Object plugin;
        private final Method method;
        private final Annotation annotation;
        private Class<?> eventType = Event.class;

        public Handler(Object plugin, Method method, Annotation annotation) {
            this.plugin = plugin;
            this.method = method;
            this.annotation = annotation;
            for (Class<?> clazz : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(clazz)) {
                    eventType = clazz;
                    break;
                }
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

    public void putHandler(String[] commands, Object plugin, Method method, Annotation annotation) {
        container.put(commands, new Handler(plugin, method, annotation));
    }


    public boolean isHandler(Method method) {
        return method.isAnnotationPresent(MessageHandler.class);
        //    ||method.isAnnotationPresent(GroupMessageHandler.class)
        //   || method.isAnnotationPresent(PrivateMessageHandler.class);
    }


}
