package tech.chowyijiu.huhu_bot.core;

import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.NoticeHandler;
import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.constant.ANSI;
import tech.chowyijiu.huhu_bot.core.rule.Rule;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.entity.arr_message.Message;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.event.request.RequestEvent;
import tech.chowyijiu.huhu_bot.exception.ActionFailed;
import tech.chowyijiu.huhu_bot.exception.FinishedException;
import tech.chowyijiu.huhu_bot.utils.LogUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.lang.reflect.Field;
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
public class CoreDispatcher {

    private final ApplicationContext ioc;

    private final List<Handler> MESSAGE_HANDLER_CONTAINER = new ArrayList<>();
    private final List<Handler> NOTICE_HANDLER_CONTAINER = new ArrayList<>();

    //private final Map<String, Handler> HANDLER_CALL_RECORD = new HashMap<>();

    @PostConstruct
    private void loadPlugin() {
        //获取所有插件Bean
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        //创建两个临时存储的List
        List<Handler> messageHandlers = new ArrayList<>();
        List<Handler> noticeHandlers = new ArrayList<>();
        if (!botPluginMap.isEmpty()) {
            log.info("{}[HUHUBOT] Start Load Plugin...{}", ANSI.YELLOW, ANSI.RESET);
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
                    }
                });
                Object[] args = LogUtil.buildArgsWithColor(ANSI.YELLOW, pluginName, count++, botPluginMap.size(),
                        Arrays.toString(handlerNames.toArray()));
                log.info("{}Succeeded to load plugin[{}], progress[{}/{}], function set: {}{}", args);
            }
        }
        if (messageHandlers.isEmpty() && noticeHandlers.isEmpty()) {
            //throw new RuntimeException("[CoreDispatcher] No plugins were found");
            log.info("{}No plugins were found{}", ANSI.YELLOW, ANSI.RESET);
            return;
        }
        //根据priority对handler进行排序, 并全部加入到handlerContainer中
        MESSAGE_HANDLER_CONTAINER.addAll(messageHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority)).toList());
        NOTICE_HANDLER_CONTAINER.addAll(noticeHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority)).toList());
        log.info("{}[HUHUBOT] Running...{}", ANSI.YELLOW, ANSI.RESET);
    }

    public void onMessage(final Bot bot, final MessageEvent event) {
        log.info("{} Start Match MessageHandler", event);
        for (Handler handler : MESSAGE_HANDLER_CONTAINER) {
            //判断事件类型
            if (!handler.eventTypeMatch(event.getClass())) continue;
            //因为注解中的commands和keywords 默认为{}, 无需判空
            if (handler.commandsHasLength()) {
                if (matchCommand(bot, event, handler)) break;
                continue;
            }
            if (handler.keyWordsHasLength()) {
                if (matchKeyword(bot, event, handler)) break;
                //continue;
            }
        }
        log.info("{} Match MessageHandler End", event);
    }

    private boolean matchCommand(final Bot bot, final MessageEvent event, final Handler handler) {
        Message message = event.getMessage();
        String plainText = message.plainText();
        //如果配置了命令前缀
        if (BotConfig.commandPrefixes.size() > 0) {
            if (BotConfig.commandPrefixes.stream().map(Object::toString).noneMatch(plainText::startsWith)) {
                //配置了命令前缀, 没有命令前缀的消息, 直接结束这个handler的后续匹配
                return false;
            } else {
                plainText = plainText.substring(1);
            }
        }
        for (String command : handler.commands) {
            //匹配前缀命令
            if (plainText.startsWith(command)) {
                //去除触发的command, 并去掉头尾空格
                event.setCommandArgs(plainText.replaceFirst(command, "").trim());
                handler.execute(bot, event);
                return handler.block;
            }
        }
        return false;
    }

    /**
     * 关键词匹配
     */
    private boolean matchKeyword(final Bot bot, final MessageEvent event, final Handler handler) {
        for (String keyword : handler.keywords) {
            if (event.getMessage().plainText().contains(keyword)) {
                handler.execute(bot, event);
                return handler.block; //
            }
        }
        return false;
    }

    public void onNotice(final Bot bot, final NoticeEvent event) {
        log.info("{} Start Match NoticeHandler", event);
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (handler.eventTypeMatch(event.getClass())) {
                handler.execute(bot, event);
                if (handler.block) break;
            }
        }
        log.info("{} Match NoticeHandler End", event);
    }

    @Deprecated
    public void onRequest(final Bot bot, final RequestEvent event) {
        log.info("{} Start Match RequestHandler", event);
        for (Handler handler : new ArrayList<Handler>()) {
            if (handler.eventTypeMatch(event.getClass())) {
                handler.execute(bot, event);
                break;
            }
        }
        log.info("{} Match RequestHandler End", event);
    }

    @Builder
    static class Handler {
        private final Object plugin; //ioc容器中的插件Bean
        private final Method method;

        private Class<?> eventType;  //用于isAssignableFrom 匹配事件类型
        private String name;         //Handler注解里的name
        private int priority;
        private boolean block;       //false 为不阻断

        //public int cutdown;         //cd 单位秒
        //public long lastExecuteTime;//上次调用时间戳
        //public String cdMsg;

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
                try {
                    Field field = plugin.getClass().getDeclaredField(RuleName);
                    field.setAccessible(true);
                    Object obj = field.get(plugin);
                    if (obj instanceof Rule) this.rule = (Rule) obj;
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
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

        //@Deprecated
        //public boolean checkCutdown(final Bot bot, final Event event) {
        //    if (this.cutdown <= 0) return true; //设置小于0, 防止用户设置负数cutdown, 徒增以下计算
        //    if (System.currentTimeMillis() - this.lastExecuteTime > this.cutdown * 1000L) {
        //        return true;
        //    } else {
        //        //cd 没好发送cdMsg
        //        if (StringUtils.hasLength(this.cdMsg)) {
        //            bot.sendMessage(event, this.cdMsg, true);
        //        }
        //        return false;
        //    }
        //}

        public void execute(Bot bot, Event event) {
            try {
                if (rule != null && !rule.check(bot, event)) return;
                log.info("{}{} will be handled by Plugin[{}] Function[{}] Priority[{}]{}"
                        , ANSI.YELLOW, event, this.plugin.getClass().getSimpleName()
                        , this.name, this.priority, ANSI.RESET);
                //this.lastExecuteTime = System.currentTimeMillis(); //是否需要加锁
                method.invoke(plugin, bot, event);
            } catch (IllegalAccessException e) {
                log.info("{}IllegalAccessException: {}{}",
                        LogUtil.buildArgsWithColor(ANSI.RED, "handler method must be public"));
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof FinishedException) {
                    log.info("{} FinishedException ignored", event);
                } else if (targetException instanceof ActionFailed) {
                    log.info("{}ActionFailed: {}{}",
                            LogUtil.buildArgsWithColor(ANSI.RED, targetException.getMessage()));
                } else {
                    targetException.printStackTrace();
                }

            }
        }


        public boolean keyWordsHasLength() {
            return keywords.length > 0;
        }

        public boolean commandsHasLength() {
            return commands.length > 0;
        }

        public boolean eventTypeMatch(Class<? extends Event> eventClass) {
            return eventType.isAssignableFrom(eventClass);
        }
    }

}
