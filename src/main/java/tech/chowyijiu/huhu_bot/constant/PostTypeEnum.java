package tech.chowyijiu.huhu_bot.constant;

import lombok.RequiredArgsConstructor;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@RequiredArgsConstructor
public enum PostTypeEnum {
    // 普通消息
    message,
    // message与message_sent的数据是一致的,
    // 区别仅在于后者是bot发出的消息.
    // 默认配置下不会上报message_sent, 仅在配置message下report-self-message项为true时上报
    message_sent,
    // 事件
    meta_event,
    // bot通知
    notice,
    // 请求上报
    request

}