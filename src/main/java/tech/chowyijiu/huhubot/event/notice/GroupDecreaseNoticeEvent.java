package tech.chowyijiu.huhubot.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.constant.NoticeTypeEnum;

/**
 * 群成员减少事件
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupDecreaseNoticeEvent extends NoticeEvent {

    private final String noticeType = NoticeTypeEnum.group_decrease.name();

    private String subType; //leave、kick、kick_me 事件子类型, 分别表示主动退群、成员被踢、登录号被踢
    private Long userId;
    private Long groupId;
    private Long operatorId;
}
