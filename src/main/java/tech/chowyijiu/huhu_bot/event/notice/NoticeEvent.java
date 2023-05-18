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

    public static NoticeEvent jsonToNoticeEvent(JSONObject jsonObject) {
        String noticeType = jsonObject.getString("notice_type");
        NoticeEvent noticeEvent;
        switch (NoticeTypeEnum.valueOf(noticeType)) {
            case notify:
                noticeEvent = jsonObject.toJavaObject(NotifyNoticeEvent.class);
                break;
            case group_increase:
                noticeEvent = jsonObject.toJavaObject(GroupIncreaseNoticeEvent.class);
                break;
            case group_decrease:
                noticeEvent = jsonObject.toJavaObject(GroupDecreaseNoticeEvent.class);
                break;
            case group_card:
                noticeEvent = jsonObject.toJavaObject(GroupCardNoticeEvent.class);
                break;
            case group_ban:
                noticeEvent = jsonObject.toJavaObject(GroupBanNoticeEvent.class);
                break;
            case group_admin:
                noticeEvent = jsonObject.toJavaObject(GroupAdminNoticeEvent.class);
                break;
            case group_upload:
                noticeEvent = jsonObject.toJavaObject(GroupUploadNoticeEvent.class);
                break;
            case group_recall:
                noticeEvent = jsonObject.toJavaObject(GroupRecallNoticeEvent.class);
                break;
            case friend_recall:
                noticeEvent = jsonObject.toJavaObject(FriendRecallNoticeEvent.class);
                break;
            case friend_add:
                noticeEvent = jsonObject.toJavaObject(FriendAddNoticeEvent.class);
                break;
            case essence:
            case offline_file:
            case client_status:
            default:
                noticeEvent = jsonObject.toJavaObject(NoticeEvent.class);
                break;
        }
        return noticeEvent;
    }




}
