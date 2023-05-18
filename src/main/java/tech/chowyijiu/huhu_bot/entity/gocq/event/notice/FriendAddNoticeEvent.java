package tech.chowyijiu.huhu_bot.entity.gocq.event.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;

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

    private Long userId;
}
