package tech.chowyijiu.huhu_bot.dispenser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;
import tech.chowyijiu.huhu_bot.utils.IocUtil;

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
public class MessageDispenser {

    private final Map<Object, List<Method>> plugins = new HashMap<>();

    public void loadPlugin() {
        Map<String, Object> botPluginMap = IocUtil.getBeansWithAnnotation(BotPlugin.class);
        if (!botPluginMap.isEmpty()) {
            log.info("huhu bot start load plugin...");
            int count = 1;
            for (String pluginName : botPluginMap.keySet()) {
                Object plugin = botPluginMap.get(pluginName);
                List<Method> handlers = Arrays.stream(plugin.getClass().getMethods())
                        .filter(this::isHandler)
                        .collect(Collectors.toList());
                plugins.put(plugin, handlers);
                List<String> handlerNames = getHandlerNames(handlers);
                log.info("成功加载插件[{}], 进度[{}/{}], 功能集:{}" , pluginName, count, botPluginMap.size(), handlerNames);
                count++;
            }
        }

    }

    public boolean isHandler(Method method) {
        return method.isAnnotationPresent(MessageHandler.class) ;
        //    ||method.isAnnotationPresent(GroupMessageHandler.class)
        //   || method.isAnnotationPresent(PrivateMessageHandler.class);
    }

    public List<String> getHandlerNames(List<Method> handlers) {
        return handlers.stream()
                .map(handler -> handler.getAnnotation(MessageHandler.class).name())
                .collect(Collectors.toList());
    }

    /**
     * 响应handler
     *
     */
    public void onEvent(final WebSocketSession session, final Message message, final String rawMessage) throws InvocationTargetException, IllegalAccessException {
        if (!CollectionUtils.isEmpty(plugins)) {
            outer:for (Object plugin : plugins.keySet()) {
                List<Method> handlers = plugins.get(plugin);
                for (Method handler : handlers) {
                    MessageHandler annotation = handler.getAnnotation(MessageHandler.class);
                    if (matchPrefix(annotation.command(), rawMessage)) {
                        handler.invoke(plugin, session, message);
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