package tech.flless.huhubot.adapters.onebot.v11.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.flless.huhubot.adapters.onebot.v11.event.notice.*;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Getter
@RequiredArgsConstructor
public enum NoticeTypeEnum {
    notify(NotifyNoticeEvent.class),                    //群成员头衔变更 群成员荣誉变更 群红包运气王提示, 群内戳一戳 好友戳一戳
    group_increase(GroupIncreaseNoticeEvent.class),     //增员
    group_decrease(GroupDecreaseNoticeEvent.class),     //减员
    friend_recall(FriendRecallNoticeEvent.class),       //私聊消息撤回
    group_recall(GroupRecallNoticeEvent.class),         //群聊撤回
    group_admin(GroupAdminNoticeEvent.class),           //群管理员变动
    group_upload(GroupUploadNoticeEvent.class),         //群文件上传
    group_ban(GroupBanNoticeEvent.class),               //群禁言
    friend_add(FriendAddNoticeEvent.class),             //好友添加
    group_card(GroupCardNoticeEvent.class),             //群成员名片更新
    client_status(NoticeEvent.class),                   //其他客户端在线状态变更
    essence(NoticeEvent.class),                         //精华消息变更
    offline_file(NoticeEvent.class);                    //接收到离线文件

    private final Class<? extends NoticeEvent> type;

}
