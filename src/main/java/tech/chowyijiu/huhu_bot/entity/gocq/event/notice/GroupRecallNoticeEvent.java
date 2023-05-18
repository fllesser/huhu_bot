package tech.chowyijiu.huhu_bot.entity.gocq.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;

/**
 * 群聊撤回事件
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupRecallNoticeEvent extends NoticeEvent {

    private final String noticeType = NoticeTypeEnum.group_recall.name();
    private Long userId;
    private Long groupId;
    private Long operatorId;
    private Long messageId;

}
