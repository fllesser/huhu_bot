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
import tech.chowyijiu.huhu_bot.entity.arr_message.ForwardMessage;
import tech.chowyijiu.huhu_bot.entity.arr_message.Message;
import tech.chowyijiu.huhu_bot.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhu_bot.entity.request.RequestBox;
import tech.chowyijiu.huhu_bot.entity.response.FriendInfo;
import tech.chowyijiu.huhu_bot.entity.response.GroupInfo;
import tech.chowyijiu.huhu_bot.entity.response.GroupMember;
import tech.chowyijiu.huhu_bot.entity.response.SelfInfo;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.exception.ActionFailed;
import tech.chowyijiu.huhu_bot.exception.FinishedException;
import tech.chowyijiu.huhu_bot.utils.LogUtil;
import tech.chowyijiu.huhu_bot.utils.StringUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

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

    private static final long timeout = 10000L;

    //多个Bot对象共用
    private static final Map<String, LinkedBlockingDeque<String>> respMap = new HashMap<>();

    public static void putEchoResult(String echo, String data) {
        if (respMap.containsKey(echo)) respMap.get(echo).offer(data);
    }

    /***
     * 等待响应
     * @param echo 回声
     * @return String
     */
    private static String waitResp(String echo) {
        log.info("{}Blocking waits for gocq api resp, echo:{}{}", ANSI.BLUE, echo, ANSI.RESET);
        LinkedBlockingDeque<String> deque = new LinkedBlockingDeque<>(1);
        respMap.put(echo, deque);
        try {
            String resp = deque.poll(timeout, TimeUnit.MILLISECONDS);
            if (!StringUtil.hasLength(resp))
                throw new ActionFailed("echo:" + echo + ", api请求超时, 或者该api无响应数据");
            log.info("{}Accepted a response for echo:{}{}", ANSI.BLUE, echo, ANSI.RESET);
            return resp;
        } catch (InterruptedException e) {
            throw new ActionFailed("等待响应数据线程中断异常, echo:" + echo);
        } finally {
            respMap.remove(echo);
        }
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

    /**
     * 私有 callApi
     */
    public void callApi(GocqActionEnum action, Map<String, Object> paramsMap) {
        RequestBox requestBox = new RequestBox();
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
    public String callApiWithResp(GocqActionEnum action, Map<String, Object> paramsMap) {
        RequestBox requestBox = new RequestBox();
        Optional.ofNullable(paramsMap).ifPresent(requestBox::setParams);
        requestBox.setAction(action.getAction());
        //curThread__userId__action__uuid
        String echo = Thread.currentThread().getName() + "." + this.getUserId() + "." +
                action.getAction() + "." + UUID.randomUUID();
        requestBox.setEcho(echo);
        //发送请求
        this.sessionSend(JSONObject.toJSONString(requestBox));
        return waitResp(echo);
    }




    /**
     * HTTP request API method
     * gocq 需配置 http
     *
     * @param action    action
     * @param paramsMap parameters
     * @return json String
     */
    @Deprecated
    private String callHttpApi(GocqActionEnum action, @Nullable Map<String, Object> paramsMap) {
        String url = "http://127.0.0.1:8899/" + action.getAction();
        HttpRequest request = HttpRequest.get(url).form(paramsMap);
        HttpResponse response = request.execute();
        return switch (response.getStatus()) {
            case 401 -> throw new ActionFailed("action" + action.getAction() + " access token 未提供");
            case 403 -> throw new ActionFailed("action" + action.getAction() + " access token 不符合");
            case 406 -> throw new ActionFailed("action" + action.getAction() + " Content-Type 不支持" +
                    "(非 application/json 或 application/x-www-form-urlencoded");
            case 404 -> throw new ActionFailed("action:" + action.getAction() + " API 不存在");
            case 200 -> response.body(); //除上述情况外所有情况 (具体 API 调用是否成功, 需要看 API 的 响应数据
            default -> throw new ActionFailed("action:" + action.getAction() + " 获取数据失败");
        };
    }

    public void deleteFriend(Long userId) {
        this.callApiWithResp(GocqActionEnum.DELETE_FRIEND, Map.of("user_id", userId));
    }

    /**
     * 获取bot信息
     *
     * @return SelfInfo
     */
    public SelfInfo getLoginInfo() {
        String data = this.callApiWithResp(GocqActionEnum.GET_LOGIN_INFO, null);
        return JSONObject.parseObject(data, SelfInfo.class);
    }

    /**
     * 获取好友列表
     *
     * @return List<FriendInfo>
     */
    public List<FriendInfo> getFriendList() {
        String data = this.callApiWithResp(GocqActionEnum.GET_FRIEND_LIST, null);
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
                Map.of("group_id", groupId, "no_cache", noCache));
        return JSONArray.parseArray(data, GroupMember.class);
    }

    /**
     * 获取群列表
     *
     * @param noCache 默认false, 使用缓存
     * @return List<GroupInfo>
     */
    public List<GroupInfo> getGroupList(boolean noCache) {
        String data = this.callApiWithResp(GocqActionEnum.GET_GROUP_LIST, Map.of("no_cache", noCache));
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
                Map.of("group_id", groupId, "user_id", userId, "no_cache", noCache));
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
                Map.of("group_id", groupId, "user_id", userId, "special_title", specialTitle));
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
                Map.of("group_id", groupId, "user_id", userId, "card", card));
    }

    /**
     * 群打卡
     *
     * @param groupId group_id
     */
    public void sendGroupSign(Long groupId) {
        this.callApi(GocqActionEnum.SEND_GROUP_SIGN, Map.of("group_id", groupId));
    }

    /**
     * 设置群管理员
     *
     * @param groupId group_id
     * @param userId  user_id
     * @param enable  true 为设置, false 为取消
     */
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        this.callApi(GocqActionEnum.SET_GROUP_ADMIN,
                Map.of("group_id", groupId, "user_id", userId, "enable", enable));
    }



    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param message    消息
     * autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendGroupMessage(Long groupId, Object message) {
        Map<String, Object> map;
        if (message instanceof String s) {
            map = Map.of("group_id", groupId, "message", s, "auto_escape", true);
        } else if (message instanceof MessageSegment || message instanceof Message) {
            map = Map.of("group_id", groupId, "message", message, "auto_escape", false);
        } else throw new IllegalArgumentException("The type of message must be one of the Message,MessageSegment,String");
        this.callApi(GocqActionEnum.SEND_GROUP_MSG, map);
    }


    /**
     * 发送私聊消息
     *
     * @param userId     对方qq
     * @param message    消息
     * autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendPrivateMessage(Long userId, Object message) {
        Map<String, Object> map;
        if (message instanceof String s) {
            map = Map.of("user_id", userId, "message", s, "auto_escape", true);
        } else if (message instanceof MessageSegment || message instanceof Message) {
            map = Map.of("user_id", userId, "message", message, "auto_escape", false);
        } else throw new IllegalArgumentException("The type of message must be one of the Message,MessageSegment,String");
        this.callApi(GocqActionEnum.SEND_PRIVATE_MSG, map);
    }


    /**
     * 撤回消息
     *
     * @param messageId Integer
     */
    public void deleteMsg(Integer messageId) {
        this.callApi(GocqActionEnum.DELETE_MSG, Map.of("message_id", messageId));
    }

    /**
     * 标记消息为已读
     *
     * @param messageId Integer
     */
    public void markMsgAsRead(Integer messageId) {
        this.callApi(GocqActionEnum.MARK_MSG_AS_READ,
                Map.of("message_id", messageId));
    }

    /**
     * 获取转发消息 todo
     *
     * @param messageId Integer
     */
    public void getForwardMsg(Integer messageId) {
        String data = this.callApiWithResp(GocqActionEnum.GET_FORWARD_MSG,
                Map.of("message_id", messageId));
    }

    /**
     * 群组发送合并转发消息
     *
     * @param groupId 群号
     * @param nodes   nodes
     */
    public void sendGroupForwardMsg(Long groupId, List<ForwardMessage> nodes) {
        this.callApi(GocqActionEnum.SEND_GROUP_FORWARD_MSG,
                Map.of("group_id", groupId, "messages", nodes));
    }

    /**
     * 私聊发送合并转发消息
     *
     * @param userId qq
     * @param nodes  nodes
     */
    public void sendPrivateForwardMsg(Long userId, List<ForwardMessage> nodes) {
        this.callApi(GocqActionEnum.SEND_PRIVATE_FORWARD_MSG,
                Map.of("user_id", userId, "messages", nodes));
    }


    /**
     * 根据事件, 来发送对应的消息
     *
     * @param event      event object
     * @param message    消息 Message | MessageSegment | String
     * autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendMessage(Event event, Object message) {
        JSONObject jsonObject = event.getJsonObject();
        Long groupId = jsonObject.getLong("group_id");
        if (groupId != null) {
            this.sendGroupMessage(groupId, message);
        } else {
            Long userId = jsonObject.getLong("user_id");
            //if (this.userId.equals(userId)) return;
            assert userId != null;
            this.sendPrivateMessage(userId, message);
        }
    }

    @Deprecated
    public void finish(Event event, String message) {
        this.sendMessage(event, message);
        throw new FinishedException();
    }


    public void setGroupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        this.callApi(GocqActionEnum.SET_GROUP_KICK,
                Map.of("group_id", groupId, "user_id", userId, "reject_add_request", rejectAddRequest));
    }


    /**
     * 群单人禁言
     *
     * @param groupId  group_id
     * @param userId   user_id
     * @param duration duration 单位秒 default 30 * 60 | 0 表示取消禁言
     */
    public void setGroupBan(Long groupId, Long userId, int duration) {
        this.callApi(GocqActionEnum.SET_GROUP_BAN,
                Map.of("group_id", groupId, "user_id", userId, "duration", duration));
    }

    /**
     * 群全体禁言
     *
     * @param groupId group_id
     * @param enable  默认true 禁言
     */
    public void setGroupWholeBan(Long groupId, boolean enable) {
        this.callApi(GocqActionEnum.SET_GROUP_WHOLE_BAN, Map.of("group_id", groupId, "enable", enable));
    }
}
