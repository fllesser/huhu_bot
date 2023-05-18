package tech.chowyijiu.huhu_bot.entity.gocq.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;

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
    //private String title;
}
