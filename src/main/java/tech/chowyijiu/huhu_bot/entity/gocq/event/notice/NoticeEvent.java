package tech.chowyijiu.huhu_bot.entity.gocq.event.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.response.WsResp;

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

    public static NoticeEvent respToEvent(WsResp wsResp) {
        NoticeEvent noticeEvent = null;
        switch (NoticeTypeEnum.valueOf(wsResp.getNoticeType())) {
            case notify:
                noticeEvent = new NotifyNoticeEvent();
                break;
            case group_increase:
                noticeEvent = new GroupIncreaseNoticeEvent();
                break;
            case group_decrease:
                noticeEvent = new GroupDecreaseNoticeEvent();
                break;
            case group_card:
                noticeEvent = new GroupCardNoticeEvent();
                break;
            case group_ban:
                noticeEvent = new GroupBanNoticeEvent();
                break;
            case group_admin:
                noticeEvent = new GroupAdminNoticeEvent();
                break;
            case group_upload:
                noticeEvent = new GroupUploadNoticeEvent();
                break;
            case group_recall:
                noticeEvent = new GroupRecallNoticeEvent();
                break;
            case friend_recall:
                noticeEvent = new FriendRecallNoticeEvent();
                break;
            case friend_add:
                noticeEvent = new FriendAddNoticeEvent();
                break;
            case essence:
            case offline_file:
            case client_status:
            default:
                noticeEvent = new NoticeEvent();
                break;
        }
        BeanUtils.copyProperties(wsResp, noticeEvent);
        return noticeEvent;
    }




}
