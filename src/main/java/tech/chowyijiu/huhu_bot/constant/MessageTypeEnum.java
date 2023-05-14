package tech.chowyijiu.huhu_bot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Getter
@RequiredArgsConstructor
public enum MessageTypeEnum {
    null_(""),
    group("group"),
    private_("private");

    private final String type;

}
