package tech.chowyijiu.huhubot.adapters.onebot.v11.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.core.constant.NoticeTypeEnum;

/**
 * 群管理员变动事件
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupAdminNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.group_admin.name();
    private String subType; //approve、invite 事件子类型, 分别表示管理员已同意入群、管理员邀请入群
    private Long userId;
    private Long groupId;
}
