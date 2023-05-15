package tech.chowyijiu.huhu_bot.dispenser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;

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
public class NoticeDispenser {

    private final ApplicationContext ioc;

    private final Map<Object, List<Method>> plugins = new HashMap<>();

    @PostConstruct
    public void loadPlugin() {
        //Map<String, Object> botPluginMap = IocUtil.getBeansWithAnnotation(BotPlugin.class);
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        if (!botPluginMap.isEmpty()) {
            log.info("NoticeDispenser Start Load Plugin...");
            int count = 1;
            for (String pluginName : botPluginMap.keySet()) {
                Object plugin = botPluginMap.get(pluginName);
                List<Method> handlers = Arrays.stream(plugin.getClass().getMethods())
                        .filter(this::isHandler)
                        .collect(Collectors.toList());
                plugins.put(plugin, handlers);
                List<String> handlerNames = getHandlerNames(handlers);
                log.info("[NoticeDispenser] Load Plugin [{}], PROGRESS[{}/{}], COMMAND: {}", pluginName, count, botPluginMap.size(), handlerNames);
                count++;
            }
        }

    }

    public boolean isHandler(Method method) {
        return method.isAnnotationPresent(NoticeHandler.class);
        //    ||method.isAnnotationPresent(GroupMessageHandler.class)
        //   || method.isAnnotationPresent(PrivateMessageHandler.class);
    }

    public List<String> getHandlerNames(List<Method> handlers) {
        return handlers.stream()
                .map(handler -> handler.getAnnotation(NoticeHandler.class).name())
                .collect(Collectors.toList());
    }

    /**
     * 将消息分发给handler
     */
    public void dispense(final WebSocketSession session, final Message message) throws InvocationTargetException, IllegalAccessException {
        if (!CollectionUtils.isEmpty(plugins)) {
            outer:for (Object plugin : plugins.keySet()) {
                List<Method> handlers = plugins.get(plugin);
                for (Method handler : handlers) {
                    NoticeHandler annotation = handler.getAnnotation(NoticeHandler.class);
                    if (annotation.type().equals(NoticeTypeEnum.notify)
                        && Objects.equals(annotation.type().name(), message.getNoticeType())
                        && Objects.equals(message.getSubType(), annotation.subType().name())
                    ) {
                        handler.invoke(plugin, session, message);
                        break outer;
                    }
                    if (annotation.type().name().equals(message.getNoticeType())) {
                        handler.invoke(plugin, session, message);
                        break outer;
                    }
                }
            }
        }
    }

}