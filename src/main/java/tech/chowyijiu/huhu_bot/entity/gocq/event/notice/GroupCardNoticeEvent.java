package tech.chowyijiu.huhu_bot.entity.gocq.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;

/**
 * 群名片变动事件
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupCardNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.group_card.name();
    private Long userId;
    private Long groupId;

    private String cardNew;
    private String cardOld;
}
