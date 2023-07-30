package tech.chowyijiu.huhubot.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.constant.NoticeTypeEnum;

/**
 * 群成员增加事件
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupIncreaseNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.group_increase.name();

    //事件子类型
    //approve分别表示管理员已同意入群  invite 管理员邀请入群
    private String subType;
    private Long userId;
    private Long groupId;
    private Long operatorId;
}