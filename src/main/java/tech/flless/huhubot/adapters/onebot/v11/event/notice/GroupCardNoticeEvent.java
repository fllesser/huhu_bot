package tech.flless.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.core.constant.NoticeTypeEnum;

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
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("card_new")
    private String cardNew;
    @JsonProperty("card_old")
    private String cardOld;
}
