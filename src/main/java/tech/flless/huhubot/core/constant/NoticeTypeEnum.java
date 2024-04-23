package tech.flless.huhubot.core.constant;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
public enum NoticeTypeEnum {
    notify,             //群成员头衔变更 群成员荣誉变更 群红包运气王提示, 群内戳一戳 好友戳一戳
    group_increase,     //增员
    group_decrease,     //减员
    friend_recall,      //私聊消息撤回
    group_recall,       //群聊撤回
    group_admin,        //群管理员变动
    group_upload,       //群文件上传
    group_ban,          //群禁言
    friend_add,         //好友添加
    group_card,         //群成员名片更新
    client_status,      //其他客户端在线状态变更
    essence,            //精华消息变更
    offline_file,       //接收到离线文件
}
