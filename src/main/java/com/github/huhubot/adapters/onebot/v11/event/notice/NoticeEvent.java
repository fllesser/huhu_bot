package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;
import com.github.huhubot.adapters.onebot.v11.constant.PostTypeEnum;

import java.util.StringJoiner;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
public class NoticeEvent extends Event {

    private final String postType = PostTypeEnum.notice.name();
    @JsonProperty("notice_type")
    private String noticeType;

    public static NoticeEvent build(final JSONObject jsonObject) {
        String noticeType = jsonObject.getString("notice_type");
        return jsonObject.toJavaObject(NoticeTypeEnum.valueOf(noticeType).getType());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "[", "](Undefined Notice)").toString();
    }
}
