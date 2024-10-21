package com.github.huhubot.core;

import com.github.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.message.PrivateMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import com.github.huhubot.core.handler.Handler;
import com.github.huhubot.core.handler.Handlers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import com.github.huhubot.core.annotation.BotPlugin;
import com.github.huhubot.core.annotation.MessageHandler;
import com.github.huhubot.core.annotation.NoticeHandler;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;

import java.lang.reflect.Method;
import java.util.*;


/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatcherCore {

    private final ApplicationContext ioc;

    private final Handlers privateMessageHandlers = new Handlers();
    private final Handlers groupMessageHandlers = new Handlers();
    private final Handlers noticeHandlers = new Handlers();

    private final List<Handlers> handlersList = List.of(privateMessageHandlers, groupMessageHandlers, noticeHandlers);

    @PostConstruct
    private void loadPlugin() {
        //获取所有插件Bean
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        //创建两个临时存储的List
        if (!botPluginMap.isEmpty()) {
            log.info("Huhubot starts to load plugins...");
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
                    Class<?> parameterType = method.getParameterTypes()[0];
                    if (method.isAnnotationPresent(MessageHandler.class) && MessageEvent.class.isAssignableFrom(parameterType)) {
                        handler = Handler.buildMessageHandler(plugin, method);
                        if (parameterType == MessageEvent.class) {
                            groupMessageHandlers.add(handler);
                            privateMessageHandlers.add(handler);
                        } else if (parameterType == PrivateMessageEvent.class) {
                            privateMessageHandlers.add(handler);
                        } else if (parameterType == GroupMessageEvent.class) {
                            groupMessageHandlers.add(handler);
                        }
                    } else if (method.isAnnotationPresent(NoticeHandler.class) && NoticeEvent.class.isAssignableFrom(parameterType)) {
                        handler = Handler.buildNoticeHandler(plugin, method, parameterType);
                        noticeHandlers.add(handler);
                    } else continue;
                    pluginFuctionNames.append("[").append(handler.getName()).append("] ");
                }
                log.info("Huhubot Loaded Plugin[{}] Progress[{}/{}] Functions:{}", pluginName, count++, botPluginMap.size(), pluginFuctionNames);
            }
        }

        if (handlersList.stream().allMatch(Handlers::isEmpty)) {
            log.error("No Plugin Was Found, This Application Will Exit");
            System.exit(0);
            return;
        }
        //根据 priority 对 handler 进行排序
        for (Handlers handler : handlersList) {
            handler.sort();
        }
        log.info("Huhubot Is Running...");
    }

    public void onMessage(final MessageEvent event) {
        event.getMessage().plainText();
        if (event instanceof GroupMessageEvent) {
            for (Handler handler : groupMessageHandlers) {
                if (handler.match(event)) break;
            }
        } else {
            for (Handler handler : privateMessageHandlers) {
                if (handler.match(event)) break;
            }
        }

    }

    public void onNotice(final NoticeEvent event) {
        for (Handler handler : noticeHandlers) {
            if (handler.match(event)) break;
        }
    }

    public void onRequest(final RequestEvent event) {

    }


}
