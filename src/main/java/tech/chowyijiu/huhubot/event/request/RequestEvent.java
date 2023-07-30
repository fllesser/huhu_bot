package tech.chowyijiu.huhubot.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.constant.PostTypeEnum;
import tech.chowyijiu.huhubot.event.Event;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class RequestEvent extends Event {
    private final String postType = PostTypeEnum.request.name();

    private String requestType; //friend group 加好友 入群请求

    private Long userId;    //发送请求的 QQ 号
    private String comment; //验证信息
    private String flag;    //请求 flag, 在调用处理请求的 API 时需要传入

    //request_type 为group时 特有
    private String subType; //append、invite	请求子类型, 分别表示加群请求、邀请登录号入群
    private Long groupId;

}
