package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.MessageResp;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
public class NoticeEvent extends Event {

    private String noticeType;
    private String subType;

    public static NoticeEvent respToEvent(MessageResp messageResp) {
        switch (NoticeTypeEnum.valueOf(messageResp.getNoticeType())) {
            case group_increase:
            case group_card:
            case group_ban:
            case group_admin:
            case essence:
            case friend_add:
            case group_recall:
            case group_upload:
            case offline_file:
            case client_status:
            case friend_recall:
            case group_decrease:
            case notify:
                matchNotifyNotice(messageResp.getSubType());
                break;
        }

        NoticeEvent noticeEvent = new NoticeEvent();
        BeanUtils.copyProperties(messageResp, noticeEvent);
        return noticeEvent;
    }

    public static void matchNotifyNotice(String subType) {
        if (subType != null) {
            switch (SubTypeEnum.valueOf(subType)) {
                case poke:
                case kick:
                case title:
                case honor:
                case leave:
                case approve:
                case lucky_king:
                    break;
                default:
                    break;
            }
        }
    }
}
