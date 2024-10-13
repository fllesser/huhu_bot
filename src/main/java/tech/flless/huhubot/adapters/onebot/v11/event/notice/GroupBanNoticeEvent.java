package tech.flless.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupBanNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.group_ban.name();

    @JsonProperty("sub_type")
    private String subType;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("duration")
    private Long duration;
}
