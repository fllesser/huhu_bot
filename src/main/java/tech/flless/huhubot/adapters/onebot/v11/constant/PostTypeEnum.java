package tech.flless.huhubot.adapters.onebot.v11.constant;

/**
 * @author elastic chow
 * @date 13/5/2023
 */

public enum PostTypeEnum {
    // 普通消息
    message,
    // message与message_sent的数据是一致的,
    // 区别仅在于后者是bot发出的消息.
    // gocq默认配置下不会上报message_sent, 仅在配置message下report-self-message项为true时上报
    message_sent,
    // 事件
    meta_event,
    // bot通知
    notice,
    // 请求上报
    request

//    // 普通消息
//    message(MessageEvent::build),
//    // message与message_sent的数据是一致的,
//    // 区别仅在于后者是bot发出的消息.
//    // gocq默认配置下不会上报message_sent, 仅在配置message下report-self-message项为true时上报
//    message_sent(MessageEvent::build),
//    // 事件
//    meta_event(j -> j.toJavaObject(MetaEvent.class)),
//    // bot通知
//    notice(j -> j.toJavaObject(NoticeEvent.class)),
//    // 请求上报
//    request(j -> j.toJavaObject(RequestEvent.class));
//
//    private final Function<JSONObject, Event> function;
}