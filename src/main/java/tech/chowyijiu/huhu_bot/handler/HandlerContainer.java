package tech.chowyijiu.huhu_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Slf4j
@Component
public class HandlerContainer {

    private final Map<String, Handler> container = new HashMap<>();

    @RequiredArgsConstructor
    static class Handler {
        private final Object plugin;
        private final Method method;
        private final Annotation annotation;

        public Object handle(Object... args) {
            Object result = null;
            try {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] argTypes = Arrays.stream(args).map(Object::getClass).toArray();
                if (Arrays.equals(parameterTypes, argTypes)) {
                    result = method.invoke(plugin, args);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return result;
        }

    }

    public void putHandler(String command, String plugin, Method method, Annotation annotation) {
        container.put(command, new Handler(plugin, method, annotation));
    }


    /**
     * 匹配
     * @param message 消息
     */
    public void matchHandler(Message message) {
        for (Handler handler : container.values()) {

        }
    }



}
