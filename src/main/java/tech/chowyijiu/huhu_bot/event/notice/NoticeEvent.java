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
        // todo 改成map?
        switch (NoticeTypeEnum.valueOf(noticeType)) {
            case notify:
                return jsonObject.toJavaObject(NotifyNoticeEvent.class);
            case group_increase:
                return jsonObject.toJavaObject(GroupIncreaseNoticeEvent.class);
            case group_decrease:
                return jsonObject.toJavaObject(GroupDecreaseNoticeEvent.class);
            case group_card:
                return jsonObject.toJavaObject(GroupCardNoticeEvent.class);
            case group_ban:
                return jsonObject.toJavaObject(GroupBanNoticeEvent.class);
            case group_admin:
                return jsonObject.toJavaObject(GroupAdminNoticeEvent.class);
            case group_upload:
                return jsonObject.toJavaObject(GroupUploadNoticeEvent.class);
            case group_recall:
                return jsonObject.toJavaObject(GroupRecallNoticeEvent.class);
            case friend_recall:
                return jsonObject.toJavaObject(FriendRecallNoticeEvent.class);
            case friend_add:
                return jsonObject.toJavaObject(FriendAddNoticeEvent.class);
            case essence:
            case offline_file:
            case client_status:
            default:
                return jsonObject.toJavaObject(NoticeEvent.class);
        }
        // NoticeEvent noticeEvent;
        // String className = NoticeEvent.class.getPackage().getName() + "NoticeEvent";
        // Class<? extends NoticeEvent> clazz = Class.forName(className);
        // noticeEvent = jsonObject.toJavaObject(clazz);
        // return noticeEvent;
    }




}
