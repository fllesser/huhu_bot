package tech.chowyijiu.huhubot.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.constant.NoticeTypeEnum;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupBanNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.group_ban.name();


    private String subType;
    private Long userId;
    private Long groupId;
    private Long operatorId;
    private Long duration;
}
