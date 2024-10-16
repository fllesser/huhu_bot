# Huhubot
nonebot, but jvav 

onebotv11 sdk

## 介绍
1. java17(springboot3), 支持onebotv11协议实现(gocqhttp,napchat,llonebot)反向ws连接
2. 支持多个连接
3. 类似于nonebot2的插件编写方式

## 快速开始
1. go-cqhttp 配置文件修改项(必需),napchat 仅需填写ws-reverse即可
    ```yaml
    message:
      # 上报数据类型
      post-format: array
      # 是否上报自身消息
      report-self-message: true
    
    servers:
      # 反向 Websocket, 端口, 默认8888, 如需修改, 请在application.yml中设置
      - ws-reverse:
          universal: ws://127.0.0.1:8888/onebot/v11/ws
    
    ```
2. clone本仓库，直接在 plugins 文件里编写插件即可

## TODO
1. 连续对话
2. 适配频道(鸽)
3. 跨平台(鸽)


## 插件编写方式
1. 新建一个类加上`@BotPlugin("指定名称,用于加载时打印日志")` 和 `@SuppressWarnings("unused")`
2. 编写响应的方法, 参数必须为(Event event), 其中event可以指定其子类, 响应对应的事件
3. 然后在方法上加上@MessageHandler(可指定命令匹配或者关键词匹配) 或者 @NoticeHandler
4. 在这个类中添加类型为tech.flless.huhu_bot.rule.Rule, 名称为响应方法加"Rule"的成员变量, 使用返回布尔值lambda表达式作为对应方法响应的前置条件

## 示例插件

```Java
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