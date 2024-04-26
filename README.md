# Huhubot
nonebot2, 但是jvav

## 介绍
1. java(springboot), 基于go-cqhttp, websocket反向连接的qq机器人;
2. 支持被多个go-cqhttp连接;
3. 类似于nonebot2的插件编写方式

## 使用要点
1. go-cqhttp 反向ws地址配置如 ws://127.0.0.1:8888(配置文件自定义, 默认8888)/onebot/v11/ws
2. java -jar huhubot-1.x.jar


## TODO
1. 连续对话
2. 适配频道
3. 跨平台

## 插件编写方式
1. 新建一个类加上`@BotPlugin("指定名称,用于加载时打印日志")` 和 `@SuppressWarnings("unused")`
2. 编写响应的方法, 参数必须为(Bot bot, Event event), 其中event指定其子类, 响应对应的事件
3. 然后在方法上加上@MessageHandler(可指定命令匹配或者关键词匹配) 或者 @NoticeHandler
4. 在这个类中添加类型为tech.chowyijiu.huhu_bot.rule.Rule, 名称为响应方法加"Rule"的成员变量, 使用返回布尔值lambda表达式作为对应方法响应的前置条件

## 配置文件示例
```yaml
bot:
  super-users: [1942422015]
  command-prefixes: []
  test_group: 12334567
  ali-refresh-token: 0sdf95fa1sdfsdfasa5a38fb7adfe1d8f86

logging:
  file:
    name: log/huhubot.log  # 日志文件相对路径及名称
  level:
    root: info
```


## 示例插件

```Java
import annotation.core.tech.flless.huhubot.RuleCheck;

@Slf4j
@BotPlugin("demo")
@SuppressWarnings("unused")
public class DemoPlugin {

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "echo", commands = "echo")
    public void echo(MessageEvent event) {
        //event.getCommandArgs()不包含触发的命令
        //message 支持 String, MessageSegment, Message三种类型
        //event.getBot();
        Bot bot = event.getBot();
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



## 鸣谢
IntelliJ IDEA 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

特别感谢 JetBrains 为开源项目提供免费的 IntelliJ IDEA 等 IDE 的授权
![JetBrains Logo](jb_beam.png)

特别感谢 [nonebot2](https://github.com/nonebot/nonebot2) ,[haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server)