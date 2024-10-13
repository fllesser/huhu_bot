package tech.flless.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

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

    @JsonProperty("sub_type")
    private String subType; //leave、kick、kick_me 事件子类型, 分别表示主动退群、成员被踢、登录号被踢
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("operator_id")
    private Long operatorId;
}
