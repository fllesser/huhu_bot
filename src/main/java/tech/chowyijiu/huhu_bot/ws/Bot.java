package tech.chowyijiu.huhu_bot.ws;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.constant.ANSI;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.message.ForwardMessage;
import tech.chowyijiu.huhu_bot.entity.gocq.request.RequestBox;
import tech.chowyijiu.huhu_bot.entity.gocq.response.FriendInfo;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupInfo;
import tech.chowyijiu.huhu_bot.entity.gocq.response.GroupMember;
import tech.chowyijiu.huhu_bot.entity.gocq.response.SelfInfo;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.exception.gocq.ActionFailed;
import tech.chowyijiu.huhu_bot.exception.gocq.FinishedException;
import tech.chowyijiu.huhu_bot.utils.GocqSyncRequestUtil;
import tech.chowyijiu.huhu_bot.utils.LogUtil;

import java.io.IOException;
import java.util.*;

/**
 * @author elastic chow
 * @date 17/5/2023
 */
@Slf4j
@Getter
@ToString
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class Bot {

    private final Long userId;
    private final WebSocketSession session;

    private List<GroupInfo> groups;

    public void update() {
        groups = this.getGroupList();
    }

    /**
     * 私有 callApi
     */
    private void callApi(GocqActionEnum action, Map<String, Object> paramsMap) {
        RequestBox<Map<String, Object>> requestBox = new RequestBox<>();
        requestBox.setAction(action.getAction());
        requestBox.setParams(paramsMap);
        this.sessionSend(JSONObject.toJSONString(requestBox));
    }

    /**
     * 用于有响应数据的gocq api
     *
     * @param action    GocqActionEnum
     * @param paramsMap map
     * @return json 字符串数据
     */
    private String callApiWithResp(GocqActionEnum action, @Nullable Map<String, Object> paramsMap) {
        RequestBox<Map<String, Object>> requestBox = new RequestBox<>();
        Optional.ofNullable(paramsMap).ifPresent(requestBox::setParams);
        requestBox.setAction(action.getAction());
        String echo = Thread.currentThread().getName() + "_" +
                this.getUserId() + "_" +
                action.getAction() + "_" +
                UUID.randomUUID().toString().replace("-", "");
        requestBox.setEcho(echo);
        //发送请求
        this.sessionSend(JSONObject.toJSONString(requestBox));
        return GocqSyncRequestUtil.sendSyncRequest(echo,5000L);
    }

    /**
     * 校验参数, 并转为Map
     *
     * @param params params
     * @return Map
     */
    private Map<String, Object> checkParamsToMap(Object... params) {
        Map<String, Object> paramsMap = null;
        int length = params.length;
        if (length > 0) {
            paramsMap = new HashMap<>();
            if (length % 2 != 0) {
                log.info("[Bot] callApi invalid params, canceled");
                return null;
            }
            for (int i = 0; i < params.length; i += 2) {
                paramsMap.put(params[i].toString(), params[i + 1]);
            }
        }
        return paramsMap;
    }

    /**
     * 提供给外部调用的callApi
     * callApi(GocqActionEnum action, key, value, key, value ...)
     *
     * @param action GocqActionEnum
     * @param params 参数 key, value
     */
    public void callApi(GocqActionEnum action, Object... params) {
        this.callApi(action, checkParamsToMap(params));
    }


    /**
     * 调用 get api获取数据, 同步, 10s超时
     *
     * @param action GocqActionEnum
     * @param params 参数 key, value, key, value
     * @return json 字符串数据
     */
    public String callApiWithResp(GocqActionEnum action, Object... params) {
        return this.callApiWithResp(action, checkParamsToMap(params));
    }


    /**
     * HTTP request API method
     *
     * @param action    action
     * @param paramsMap parameters
     * @return json String
     */
    @Deprecated
    private String callGetApi(GocqActionEnum action, @Nullable Map<String, Object> paramsMap) {
        String url = "http://127.0.0.1:8899/" + action.getAction();
        HttpRequest request = HttpRequest.get(url).form(paramsMap);
        HttpResponse response = request.execute();
        switch (response.getStatus()) {
            case 401:
                throw new ActionFailed("action" + action.getAction() + " access token 未提供");
            case 403:
                throw new ActionFailed("action" + action.getAction() + " access token 不符合");
            case 406:
                throw new ActionFailed("action" + action.getAction() + " Content-Type 不支持(非 application/json 或 application/x-www-form-urlencoded");
            case 404:
                throw new ActionFailed("action:" + action.getAction() + " API 不存在");
            case 200:
                return response.body(); //除上述情况外所有情况 (具体 API 调用是否成功, 需要看 API 的 响应数据
            default:
                throw new ActionFailed("action:" + action.getAction() + " 获取数据失败");
        }
    }

    public void deleteFriend(Long userId) {
        this.callApiWithResp(GocqActionEnum.DELETE_FRIEND,
                "user_id", userId);
    }

    /**
     * 获取bot信息
     *
     * @return SelfInfo
     */
    public SelfInfo getLoginInfo() {
        String data = this.callApiWithResp(GocqActionEnum.GET_LOGIN_INFO);
        return JSONObject.parseObject(data, SelfInfo.class);
    }

    /**
     * 获取好友列表
     *
     * @return List<FriendInfo>
     */
    public List<FriendInfo> getFriendList() {
        String data = this.callApiWithResp(GocqActionEnum.GET_FRIEND_LIST);
        return JSONArray.parseArray(data, FriendInfo.class);
    }


    /**
     * 获取群成员列表
     * GocqActionEnum.GET_GROUP_MEMBER_LIST
     *
     * @param groupId groupId
     * @param noCache 为true时, 不使用缓存
     **/
    public List<GroupMember> getGroupMembers(Long groupId, boolean noCache) {
        String data = this.callApiWithResp(GocqActionEnum.GET_GROUP_MEMBER_LIST,
                "group_id", groupId, "no_cache", noCache);
        return JSONArray.parseArray(data, GroupMember.class);
    }

    /**
     * 获取群列表
     *
     * @param noCache 默认false, 使用缓存
     * @return List<GroupInfo>
     */
    public List<GroupInfo> getGroupList(boolean noCache) {
        String data = this.callApiWithResp(GocqActionEnum.GET_GROUP_LIST,
                "no_cache", noCache);
        return JSONArray.parseArray(data, GroupInfo.class);
    }

    /**
     * 不使用缓存获取群列表
     *
     * @return List<GroupInfo>
     */
    public List<GroupInfo> getGroupList() {
        //todo 想想在哪里更新groups最好, 目前感觉最好是 入群/退群更新,
        // 或者GroupMessageEvent进来,看看groups里有没有这个groupId
        this.groups = this.getGroupList(true);
        return this.groups;
    }

    /**
     * 获取群成员详细信息
     * GocqActionEnum.GET_GROUP_MEMBER_INFO
     *
     * @param groupId groupId
     * @param userId  userId
     * @param noCache 为true时, 不使用缓存
     * @return GroupMember
     */
    public GroupMember getGroupMember(Long groupId, Long userId, boolean noCache) {
        String data = this.callApiWithResp(GocqActionEnum.GET_GROUP_MEMBER_INFO,
                "group_id", groupId, "user_id", userId, "no_cache", noCache);
        return JSONObject.parseObject(data, GroupMember.class);
    }

    /**
     * 设置群头衔, 仅可在机器人为群主时有效
     *
     * @param groupId      group_id
     * @param userId       user_id
     * @param specialTitle special_title
     */
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        this.callApi(GocqActionEnum.SET_GROUP_SPECIAL_TITLE,
                "group_id", groupId, "user_id", userId, "special_title", specialTitle);
    }

    /**
     * 设置群昵称
     *
     * @param groupId group_id
     * @param userId  user_id
     * @param card    card
     */
    public void setGroupCard(Long groupId, Long userId, String card) {
        this.callApi(GocqActionEnum.SET_GROUP_CARD,
                "group_id", groupId, "user_id", userId, "card", card);
    }

    /**
     * 群打卡
     *
     * @param groupId group_id
     */
    public void sendGroupSign(Long groupId) {
        this.callApi(GocqActionEnum.SEND_GROUP_SIGN,
                "group_id", groupId);
    }

    /**
     * 设置群管理员
     * @param groupId group_id
     * @param userId user_id
     * @param enable true 为设置, false 为取消
     */
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        this.callApi(GocqActionEnum.SET_GROUP_ADMIN,
                "group_id", groupId, "user_id", userId, "enable", enable);
    }

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param message    消息
     * @param autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendGroupMessage(Long groupId, String message, boolean autoEscape) {
        this.callApi(GocqActionEnum.SEND_GROUP_MSG,
                "group_id", groupId, "message", message, "auto_escape", autoEscape);
    }

    /**
     * 发送私聊消息
     *
     * @param userId     对方qq
     * @param message    消息
     * @param autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendPrivateMessage(Long userId, String message, boolean autoEscape) {
        this.callApi(GocqActionEnum.SEND_PRIVATE_MSG,
                "user_id", userId, "message", message, "auto_escape", autoEscape);
    }


    /**
     * 撤回消息
     *
     * @param messageId Integer
     */
    public void deleteMsg(Integer messageId) {
        this.callApi(GocqActionEnum.DELETE_MSG,
                "message_id", messageId);
    }

    /**
     * 标记消息为已读
     *
     * @param messageId Integer
     */
    public void markMsgAsRead(Integer messageId) {
        this.callApi(GocqActionEnum.MARK_MSG_AS_READ,
                "message_id", messageId);
    }

    /**
     * 获取转发消息 todo
     *
     * @param messageId Integer
     */
    public void getForwardMsg(Integer messageId) {
        String data = this.callApiWithResp(GocqActionEnum.GET_FORWARD_MSG,
                "message_id", messageId);
    }

    /**
     * 群组发送合并转发消息
     *
     * @param groupId 群号
     * @param nodes   nodes
     */
    public void sendGroupForwardMsg(Long groupId, List<ForwardMessage> nodes) {
        this.callApi(GocqActionEnum.SEND_GROUP_FORWARD_MSG,
                "group_id", groupId, "messages", nodes);
    }

    /**
     * 私聊发送合并转发消息
     *
     * @param userId qq
     * @param nodes  nodes
     */
    public void sendPrivateForwardMsg(Long userId, List<ForwardMessage> nodes) {
        this.callApi(GocqActionEnum.SEND_PRIVATE_FORWARD_MSG,
                "user_id", userId, "messages", nodes);
    }



    /**
     * 根据事件, 来发送对应的消息
     *
     * @param event      event object
     * @param message    消息
     * @param autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendMessage(Event event, String message, boolean autoEscape) {
        JSONObject jsonObject = event.getJsonObject();
        Long groupId = jsonObject.getLong("group_id");
        if (groupId != null) {
            this.sendGroupMessage(groupId, message, autoEscape);
        } else {
            Long userId = jsonObject.getLong("user_id");
            //if (this.userId.equals(userId)) return;
            if (userId != null) this.sendPrivateMessage(userId, message, autoEscape);
        }
    }


    public void kickGroupMember(Long groupId, Long userId, boolean rejectAddRequest) {
        this.callApi(GocqActionEnum.SET_GROUP_KICK,
                "group_id", groupId, "user_id", userId, "reject_add_request", rejectAddRequest);
    }


    public void finish(Event event, String message) {
        this.sendMessage(event, message, false);
        throw new FinishedException();
    }

    /**
     * call api 最终调用的方法
     * Send a WebSocket message
     *
     * @param text text
     */
    public void sessionSend(String text) {
        try {
            this.session.sendMessage(new TextMessage(text));
        } catch (IOException e) {
            log.info("{}sessionSend error, session[{}], message[{}], exception[{}]{}",
                    LogUtil.buildArgsWithColor(ANSI.YELLOW, this.session.getId(), text, e.getMessage()));
        }
    }


}
