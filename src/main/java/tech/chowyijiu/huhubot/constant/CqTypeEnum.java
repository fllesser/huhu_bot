package tech.chowyijiu.huhubot.constant;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
public enum CqTypeEnum {
    //参考gocq文档 https://docs.go-cqhttp.org/cqcode
    text, //自定义类型

    //收发
    share, //链接分享
    face, //QQ 表情
    image, //图片
    at,   //@某人
    record, //语音 例如[CQ:record,file=http://baidu.com/1.mp3]
    reply, //回复
    xml, //XML 消息
    json, //JSON 消息

    //发
    music, //音乐分享
    poke, //戳一戳  范围: 仅群聊
    video, //短视频, go-cqhttp-v0.9.38 起开始支持发送，需要依赖ffmpeg
    gift,  //礼物, 仅仅支持免费礼物
    node, //合并转发节点
    tts, //仅群聊

    //收
    redbag, //红包
    forward, //合并转发

    //gocq 未支持
    rps, //猜拳魔法表情
    dice, //#掷骰子魔法表情
    shake, //窗口抖动（戳一戳） 发
    contact, //推荐好友/群
    location, //位置
    anonymous //anonymous 当收到匿名消息时, 需要通过 消息事件的群消息 的 anonymous 字段判断

}
