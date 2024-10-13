package tech.flless.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

/**
 * 好友添加事件
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class FriendAddNoticeEvent extends NoticeEvent {

    private final String noticeType = NoticeTypeEnum.friend_add.name();

    @JsonProperty("user_id")
    private Long userId;
}
