package tech.chowyijiu.huhu_bot.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class RequestEvent extends Event {
    private final String postType = PostTypeEnum.request.name();
    private String echo;
    private String request_type;
}
