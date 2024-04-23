package tech.flless.huhubot.adapters.onebot.v11.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.core.constant.NoticeTypeEnum;

/**
 * @author elastic chow
 * @date 18/5/2023
 */

@Getter
@Setter
@ToString
public class NotifyNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.notify.name();
    private String subType;
    private Long userId;

    private Long groupId;
    private Long senderId;
    private Long targetId;
    //private String title;
}
