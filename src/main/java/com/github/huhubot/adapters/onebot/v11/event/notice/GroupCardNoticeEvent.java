package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

import java.util.StringJoiner;

/**
 * 群名片变动事件
 *
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
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

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "[", "]")
                .add("groupId=" + groupId)
                .add("userId=" + userId)
                .add("cardNew='" + cardNew + "'")
                .add("cardOld='" + cardOld + "'")
                .toString();
    }
}
