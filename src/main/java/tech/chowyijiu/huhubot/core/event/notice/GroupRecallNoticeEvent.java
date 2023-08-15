package tech.chowyijiu.huhubot.core.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.core.constant.NoticeTypeEnum;

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

    @JSONField(name = "user_id")
    private Long userId;
    @JSONField(name = "group_id")
    private Long groupId;
    @JSONField(name = "operator_id")
    private Long operatorId;
    @JSONField(name = "message_id")
    private Long messageId;

}
