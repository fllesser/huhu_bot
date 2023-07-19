package tech.chowyijiu.huhu_bot.event.notice;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.event.Event;

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
    private String noticeType;

    public static NoticeEvent build(JSONObject jsonObject) {
        String noticeType = jsonObject.getString("notice_type");
        return switch (NoticeTypeEnum.valueOf(noticeType)) {
            case notify -> jsonObject.toJavaObject(NotifyNoticeEvent.class);
            case group_increase -> jsonObject.toJavaObject(GroupIncreaseNoticeEvent.class);
            case group_decrease -> jsonObject.toJavaObject(GroupDecreaseNoticeEvent.class);
            case group_card -> jsonObject.toJavaObject(GroupCardNoticeEvent.class);
            case group_ban -> jsonObject.toJavaObject(GroupBanNoticeEvent.class);
            case group_admin -> jsonObject.toJavaObject(GroupAdminNoticeEvent.class);
            case group_upload -> jsonObject.toJavaObject(GroupUploadNoticeEvent.class);
            case group_recall -> jsonObject.toJavaObject(GroupRecallNoticeEvent.class);
            case friend_recall -> jsonObject.toJavaObject(FriendRecallNoticeEvent.class);
            case friend_add -> jsonObject.toJavaObject(FriendAddNoticeEvent.class);
            case essence, offline_file, client_status -> jsonObject.toJavaObject(NoticeEvent.class);
        };
    }




}
