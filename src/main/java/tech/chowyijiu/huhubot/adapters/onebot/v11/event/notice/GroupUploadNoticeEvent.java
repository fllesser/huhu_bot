package tech.chowyijiu.huhubot.adapters.onebot.v11.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.core.constant.NoticeTypeEnum;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class GroupUploadNoticeEvent extends NoticeEvent {

    private final String noticeType = NoticeTypeEnum.group_upload.name();

    @JSONField(name = "user_id")
    private Long userId;
    @JSONField(name = "group_id")
    private Long groupId;
    private String file;
}
