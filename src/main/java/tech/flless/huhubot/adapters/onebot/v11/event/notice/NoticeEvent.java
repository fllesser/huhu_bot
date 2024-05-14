package tech.flless.huhubot.adapters.onebot.v11.event.notice;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.adapters.onebot.v11.event.Event;
import tech.flless.huhubot.core.constant.NoticeTypeEnum;
import tech.flless.huhubot.core.constant.PostTypeEnum;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NoticeEvent extends Event {

    private final String postType = PostTypeEnum.notice.name();
    @JsonProperty("notice_type")
    private String noticeType;

    public static NoticeEvent build(final JSONObject jsonObject) {
        String noticeType = jsonObject.getString("notice_type");
        return jsonObject.toJavaObject(NoticeTypeEnum.valueOf(noticeType).getType());
    }


}
