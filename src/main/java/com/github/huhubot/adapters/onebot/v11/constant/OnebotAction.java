package com.github.huhubot.adapters.onebot.v11.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elastic chow
 * &#064;date  13/5/2023
 */
@Getter
@RequiredArgsConstructor
public enum OnebotAction {

    //https://docs.go-cqhttp.org/api 参考gocq api

    send_msg("发送消息", true),
    send_private_msg("发送私聊消息", true),
    send_group_msg("发送群消息", true),
    send_group_forward_msg("群聊转发合并消息", true),
    send_private_forward_msg("私聊转发合并消息", true),
    send_group_sign("群打卡", false),
    _send_group_notice("发送群公告", false),
    send_forward_msg("伪造合并转发", true),

    set_group_card("设置群名片", false),
    set_group_special_title( "设置群专属头衔", false),
    set_group_kick("群组踢人", false),
    set_group_admin("设置群管理员", false),
    set_group_ban("群单人禁言", false),
    set_group_whole_ban("全体禁言",false),
    set_qq_profile("设置登录号信息", false),
    set_msg_emoji_like("表情回应", false),
    group_poke("戳一戳", false),
    //_set_model_show("设置在线机型", false),

    get_group_member_info("获取群成员信息", true),
    get_friend_list("获取好友列表", true),
    get_group_member_list("获取群成员列表", true),
    get_forward_msg("获取合并转发内容", true),
    get_msg("根据message_id获取消息详情",true),
    get_group_info("获取群详细信息", true),
    get_group_list("获取群列表", true),
    get_login_info("获取登录号信息", true),
    get_online_clients("获取当前账号在线客户端列表", true),
    get_image("获取图片信息", true),

    can_send_image("检查是否可以发送图片", true),
    delete_friend("删除好友", false),
    delete_msg("撤回消息", false),
    ocr_image("图片OCR接口, 仅支持接受的图片", true),
    mark_msg_as_read("标记消息为已读", false),
    check_url_safely("检查链接安全性", true),
    upload_private_file("上传私聊文件", true),
    download_file("下载文件到缓存目录", true);

    private final String remark;
    private final boolean hasResp;
}
