package tech.chowyijiu.huhu_bot.dispenser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author elastic chow
 * @date 13/5/2023
 */

/**
 * 普通消息分发器
 * 收到群聊 私聊消息时
 * 消息将通过这个类分发给所有实现了接口 IMessageEvent 的类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageDispenser {

    private final ApplicationContext ioc;

    private final Map<Object, List<Method>> plugins = new HashMap<>();
    private final Map<Method, MessageHandler> handlers = new HashMap<>();

    //@PostConstruct
    public void loadPlugin() {
        //Map<String, Object> botPluginMap = IocUtil.getBeansWithAnnotation(BotPlugin.class);
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        if (!botPluginMap.isEmpty()) {
            log.info("MessageDispenser Start Load Plugin...");
            int count = 1;
            for (String pluginName : botPluginMap.keySet()) {
                Object plugin = botPluginMap.get(pluginName);
                List<Method> handlers = Arrays.stream(plugin.getClass().getMethods())
                        .filter(this::isHandler)
                        .collect(Collectors.toList());
                plugins.put(plugin, handlers);
                List<String> handlerNames = getHandlerNames(handlers);
                log.info("[MessageDispenser] Load Plugin [{}], PROGRESS[{}/{}], COMMAND: {}", pluginName, count, botPluginMap.size(), handlerNames);
                count++;
            }
        }

    }

    public boolean isHandler(Method method) {
        return method.isAnnotationPresent(MessageHandler.class);
        //    ||method.isAnnotationPresent(GroupMessageHandler.class)
        //   || method.isAnnotationPresent(PrivateMessageHandler.class);
    }

    public List<String> getHandlerNames(List<Method> handlers) {
        return handlers.stream()
                .map(handler -> handler.getAnnotation(MessageHandler.class).name())
                .collect(Collectors.toList());
    }

    /**
     * 将消息分发给handler
     */
    public void dispense(final WebSocketSession session, final Message message, final String rawMessage) throws InvocationTargetException, IllegalAccessException {
        if (!CollectionUtils.isEmpty(plugins)) {
            outer:
            for (Object plugin : plugins.keySet()) {
                List<Method> handlers = plugins.get(plugin);
                for (Method handler : handlers) {
                    MessageHandler annotation = handler.getAnnotation(MessageHandler.class);
                    if (matchPrefix(annotation.command(), rawMessage)) {
                        if (annotation.type().equals(MessageTypeEnum.all)
                                || annotation.type().getType().equals(message.getMessageType())) {
                            log.info("匹配到 handler: [{}]", annotation.name());
                            handler.invoke(plugin, session, message);
                        }
                        break outer;
                    }
                }
            }
        }
    }


    private boolean matchPrefix(String[] prefixs, String rawMessage) {
        for (String prefix : prefixs) {
            if (rawMessage.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }


}