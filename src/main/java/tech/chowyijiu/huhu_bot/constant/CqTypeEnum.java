package tech.chowyijiu.huhu_bot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Getter
@RequiredArgsConstructor
public enum CqTypeEnum {
    image("image"),
    reply("reply"),
    poke("poke"),
    share("share"),

    at("at");
    public final String type;
}
