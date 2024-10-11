package tech.flless.huhubot.adapters.onebot.v11.event.notice;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.core.constant.NoticeTypeEnum;

/**
 * @author elastic chow
 * @date 18/5/2023
 */

@Getter
@Setter
@ToString
public class NotifyNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.notify.name();
    @JsonProperty("sub_type")
    private String subType;
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("sender_id")
    private Long senderId;
    @JsonProperty("target_id")
    private Long targetId;
    //private String title;
}
