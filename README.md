# huhu_bot
nonebot2, 但是jvav

## 介绍
1. java(springboot), 基于go-cqhttp, websocket反向连接的qq机器人;
2. 支持被多个go-cqhttp连接;
3. 类似于nonebot2的插件编写方式

## 使用要点
go-cqhttp 反向ws地址设置如 ws://127.0.0.1:8888/huhu/ws

## TODO
1. 连续对话
2. 适配频道
3. 自定义合并转发[已支持]
4. springboot-starter 分离plugin

## 插件编写方式
1. 新建一个类加上`@BotPlugin("指定名称,用于加载时打印日志")` 和 `@SuppressWarnings("unused")`
2. 编写响应的方法, 参数必须为(Bot bot, Event event), 其中event指定其子类, 响应对应的事件
3. 然后在方法上加上@MessageHandler(可指定命令匹配或者关键词匹配) 或者 @NoticeHandler
4. 在这个类中添加类型为tech.chowyijiu.huhu_bot.rule.Rule, 名称为响应方法加"Rule"的成员变量, 使用返回布尔值lambda表达式作为对应方法响应的前置条件

## 示例插件

```Java
@Slf4j
@BotPlugin("demo")
@SuppressWarnings("unused")
public class DemoPlugin {

    @MessageHandler(name = "echo", commands = "echo", rule = RuleEnum.superuser)
    public void echo(Bot bot, MessageEvent event) {
        //event.getCommandArgs()不包含触发的命令
        //message 支持 String, MessageSegment, Message三种类型
        //1. String -> 纯文本消息
        bot.sendMessage(event, event.getCommandArgs());
        //2. MessageSegment -> 发送文字, 图片, 语音等消息
        bot.sendMessage(event, MessageSegment.text(event.getCommandArgs()));
        bot.sendMessage(event, MessageSegment.image("https://xxx.jpg"));
        //3. Message(List<MessageSegment>) -> 发送组合消息, 比如图文组合;
        bot.sendMessage(event, Message.text(event.getCommandArgs()));
        bot.sendMessage(event, new Message().append("hello world!")
                .append(MessageSegment.image("https://xxx.jpg")));
    }
}
```

参考项目 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server) [nonebot2](https://github.com/nonebot/nonebot2)
