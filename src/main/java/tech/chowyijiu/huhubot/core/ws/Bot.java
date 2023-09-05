package tech.chowyijiu.huhubot.core.ws;

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
import tech.chowyijiu.huhubot.core.constant.ANSI;
import tech.chowyijiu.huhubot.core.constant.GocqAction;
import tech.chowyijiu.huhubot.core.entity.arr_message.ForwardMessage;
import tech.chowyijiu.huhubot.core.entity.arr_message.Message;
import tech.chowyijiu.huhubot.core.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.core.entity.request.RequestBox;
import tech.chowyijiu.huhubot.core.entity.response.FriendInfo;
import tech.chowyijiu.huhubot.core.entity.response.GroupInfo;
import tech.chowyijiu.huhubot.core.entity.response.GroupMember;
import tech.chowyijiu.huhubot.core.entity.response.SelfInfo;
import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.exception.ActionFailed;
import tech.chowyijiu.huhubot.core.exception.FinishedException;
import tech.chowyijiu.huhubot.core.exception.IllegalMessageTypeException;
import tech.chowyijiu.huhubot.core.utils.MistIdGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Long selfId;
    private final WebSocketSession session;

    private List<GroupInfo> groups;

    public void update() {
        groups = this.getGroupList();
    }


    /**
     * call api 最终调用的方法
     * Send a WebSocket message
     * @param requestBox box
     */
    private void sessionSend(RequestBox requestBox) {
        String text = JSONObject.toJSONString(requestBox);
        try {
            this.session.sendMessage(new TextMessage(text));
        } catch (IOException e) {
            log.info("{}sessionSend error, session[{}], message[{}], exception[{}]{}",
                    ANSI.RED, this.session.getId(), text, e.getMessage(), ANSI.RESET);
        }
    }

    public void callApi(GocqAction action, Map<String, Object> paramsMap) {
        RequestBox requestBox = RequestBox.builder().action(action.name()).params(paramsMap).build();
        this.sessionSend(requestBox);
    }

    private static final Map<Long, EchoData> ECHO_DATA_MAP = new HashMap<>();

    private static EchoData buildEchoDataToMap(long echo) {
        EchoData echoData = new EchoData(echo);
        ECHO_DATA_MAP.put(echo, echoData);
        return echoData;
    }

    public static void transferData(long echo, String data) {
        if (!ECHO_DATA_MAP.containsKey(echo)) return;
        EchoData echoData = ECHO_DATA_MAP.get(echo);
        echoData.syncSetAndNotify(data);
    }

    static class EchoData {

        private static final long timeout = 10000L;

        private final long echo;
        private String data;

        private EchoData(long echo) {
            this.echo = echo;
        }

        private String waitAndGet() {
            try {
                this.wait(timeout);
            } catch (InterruptedException e) {
                throw new ActionFailed("等待响应数据, 出现线程中断异常, echo:" + echo);
            } finally {
                ECHO_DATA_MAP.remove(echo);
            }
            return this.data;
        }

        private synchronized void syncSetAndNotify(String data) {
            this.data = data;
            this.notify();
        }
    }

    /**
     * 用于有响应数据的gocq api
     *
     * @param action    GocqActionEnum
     * @param paramsMap map
     * @return json 字符串数据
     */
    @SuppressWarnings("all")
    public String callApiWaitResp(GocqAction action, Map<String, Object> paramsMap) {
        long echo = MistIdGenerator.nextId();
        RequestBox requestBox = RequestBox.builder().action(action.name()).params(paramsMap).echo(echo).build();
        EchoData echoData = buildEchoDataToMap(echo);
        //因为存在当前线程还没获得锁, 其他线程就抢先获得了锁的情况, 所以先获取锁, 再sessionSend
        synchronized (echoData) {
            this.sessionSend(requestBox);
            return echoData.waitAndGet();
        }
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
    private String callHttpApi(GocqAction action, @Nullable Map<String, Object> paramsMap) {
        String url = "http://127.0.0.1:8899/" + action.name();
        HttpRequest request = HttpRequest.get(url).form(paramsMap);
        HttpResponse response = request.execute();
        return switch (response.getStatus()) {
            case 401 -> throw new ActionFailed("action" + action + " access token 未提供");
            case 403 -> throw new ActionFailed("action" + action + " access token 不符合");
            case 406 -> throw new ActionFailed("action" + action + " Content-Type 不支持" +
                    "(非 application/json 或 application/x-www-form-urlencoded");
            case 404 -> throw new ActionFailed("action:" + action + " API 不存在");
            case 200 -> response.body(); //除上述情况外所有情况 (具体 API 调用是否成功, 需要看 API 的 响应数据
            default -> throw new ActionFailed("action:" + action + " 获取数据失败");
        };
    }

    public void deleteFriend(Long userId) {
        this.callApiWaitResp(GocqAction.delete_friend, Map.of("user_id", userId));
    }

    /**
     * 获取bot信息
     *
     * @return SelfInfo
     */
    public SelfInfo getLoginInfo() {
        String data = this.callApiWaitResp(GocqAction.get_login_info, null);
        return JSONObject.parseObject(data, SelfInfo.class);
    }

    /**
     * 获取好友列表
     *
     * @return List<FriendInfo>
     */
    public List<FriendInfo> getFriendList() {
        String data = this.callApiWaitResp(GocqAction.get_friend_list, null);
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
        String data = this.callApiWaitResp(GocqAction.get_group_member_list,
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
        String data = this.callApiWaitResp(GocqAction.get_group_list, Map.of("no_cache", noCache));
        return JSONArray.parseArray(data, GroupInfo.class);
    }


    public GroupInfo getGroupInfo(Long groupId, boolean noCache) {
        String data = this.callApiWaitResp(GocqAction.get_group_info, Map.of("group_id", groupId, "no_cache", noCache));
        return JSONObject.parseObject(data, GroupInfo.class);
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
        String data = this.callApiWaitResp(GocqAction.get_group_member_info,
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
        this.callApi(GocqAction.set_group_special_title,
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
        this.callApi(GocqAction.set_group_card,
                Map.of("group_id", groupId, "user_id", userId, "card", card));
    }

    /**
     * 群打卡
     *
     * @param groupId group_id
     */
    public void sendGroupSign(Long groupId) {
        this.callApi(GocqAction.send_group_sign, Map.of("group_id", groupId));
    }

    /**
     * 设置群管理员
     *
     * @param groupId group_id
     * @param userId  user_id
     * @param enable  true 为设置, false 为取消
     */
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        this.callApi(GocqAction.set_group_admin,
                Map.of("group_id", groupId, "user_id", userId, "enable", enable));
    }



    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param message    消息 if String 纯文本发送 if Message or MessageSegment 转化
     * autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendGroupMessage(Long groupId, Object message) {
        Map<String, Object> map;
        boolean autoEscape = message instanceof String;
        if (!autoEscape && !(message instanceof MessageSegment) && !(message instanceof Message))
            throw new IllegalMessageTypeException();
        map = Map.of("group_id", groupId, "message", message, "auto_escape", autoEscape);
        this.callApi(GocqAction.send_group_msg, map);
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
        boolean autoEscape = message instanceof String;
        if (!autoEscape && !(message instanceof MessageSegment) && !(message instanceof Message))
            throw new IllegalMessageTypeException();
        map = Map.of("user_id", userId, "message", message , "auto_escape", autoEscape);
        this.callApi(GocqAction.send_private_msg, map);
    }


    /**
     * 撤回消息
     *
     * @param messageId Integer
     */
    public void deleteMsg(Integer messageId) {
        this.callApi(GocqAction.delete_msg, Map.of("message_id", messageId));
    }

    /**
     * 标记消息为已读
     *
     * @param messageId Integer
     */
    public void markMsgAsRead(Integer messageId) {
        this.callApi(GocqAction.mark_msg_as_read, Map.of("message_id", messageId));
    }

    /**
     * 获取转发消息 todo
     *
     * @param messageId Integer
     */
    public void getForwardMsg(Integer messageId) {
        String data = this.callApiWaitResp(GocqAction.get_forward_msg, Map.of("message_id", messageId));
    }

    /**
     * 群组发送合并转发消息
     *
     * @param groupId 群号
     * @param nodes   nodes
     */
    public void sendGroupForwardMsg(Long groupId, List<ForwardMessage> nodes) {
        this.callApi(GocqAction.send_group_forward_msg, Map.of("group_id", groupId, "messages", nodes));
    }

    /**
     * 私聊发送合并转发消息
     *
     * @param userId qq
     * @param nodes  nodes
     */
    public void sendPrivateForwardMsg(Long userId, List<ForwardMessage> nodes) {
        this.callApi(GocqAction.send_private_forward_msg, Map.of("user_id", userId, "messages", nodes));
    }


    /**
     * 根据事件, 来发送对应的消息
     *
     * @param event      event object
     * @param message    消息 Message | MessageSegment | String
     * autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendMessage(Event event, Object message) {
        JSONObject jsonObject = event.getEventJsonObject();
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
        this.callApi(GocqAction.set_group_kick,
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
        this.callApi(GocqAction.set_group_ban, Map.of("group_id", groupId, "user_id", userId, "duration", duration));
    }

    /**
     * 群全体禁言
     *
     * @param groupId group_id
     * @param enable  默认true 禁言
     */
    public void setGroupWholeBan(Long groupId, boolean enable) {
        this.callApi(GocqAction.set_group_whole_ban, Map.of("group_id", groupId, "enable", enable));
    }
}
