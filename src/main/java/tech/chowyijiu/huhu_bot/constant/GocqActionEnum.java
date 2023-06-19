package tech.chowyijiu.huhu_bot.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Getter
@RequiredArgsConstructor
public enum GocqActionEnum {

    //https://docs.go-cqhttp.org/api 参考gocq api

    SEND_MSG("send_msg","发送消息"),
    SEND_PRIVATE_MSG("send_private_msg","发送私聊消息"),
    SEND_GROUP_MSG("send_group_msg","发送群消息"),
    SEND_GROUP_FORWARD_MSG("send_group_forward_msg","转发群合并消息"),
    SEND_PRIVATE_FORWARD_MSG("send_private_forward_msg","转发私聊合并消息"),

    SET_GROUP_CARD("set_group_card", "设置群名片"),
    SET_GROUP_SPECIAL_TITLE("set_group_special_title", "设置群专属头衔"),
    SET_GROUP_KICK("set_group_kick", "群组踢人"),

    DELETE_FRIEND("delete_friend", "删除好友"),
    DELETE_MSG("delete_msg","撤回消息"),

    MARK_MSG_AS_READ("mark_msg_as_read", "标记消息为已读"),



    //get 有响应数据 需要使用 bot.callSyncGetApi
    GET_GROUP_MEMBER_INFO("get_group_member_info", "获取群成员信息"),
    GET_FRIEND_LIST("get_friend_list", "获取好友列表"),
    GET_GROUP_MEMBER_LIST("get_group_member_list","获取群成员列表"),
    GET_FORWARD_MSG("get_forward_msg","获取合并转发内容"),
    GET_WORD_SLICES(".get_word_slices","获取中文分词"),
    GET_MSG("get_msg","根据message_id获取消息详情"),
    GET_GROUP_LIST("get_group_list","获取群列表"),
    GET_LOGIN_INGO("get_login_info","获取登录号信息"),
    UPLOAD_PRIVATE_FILE("upload_private_file","上传私聊文件"),
    DOWNLOAD_FILE("download_file","下载文件到缓存目录");

    private final String action;
    private final String remarks;
}
