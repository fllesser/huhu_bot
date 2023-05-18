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
    // 事件
    meta_event,
    // bot通知
    notice,
    // 请求上报
    request

}