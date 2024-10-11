package tech.flless.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.ForwardMessage;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.Message;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.MessageSegment;
import tech.flless.huhubot.adapters.onebot.v11.entity.request.RequestBox;
import tech.flless.huhubot.adapters.onebot.v11.entity.response.*;
import tech.flless.huhubot.adapters.onebot.v11.event.Event;
import tech.flless.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.core.constant.ANSI;
import tech.flless.huhubot.core.constant.GocqAction;
import tech.flless.huhubot.core.exception.ActionFailed;
import tech.flless.huhubot.core.exception.FinishedException;
import tech.flless.huhubot.core.exception.IllegalMessageTypeException;
import tech.flless.huhubot.utils.MistIdGenerator;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author elastic chow
 * @date 17/5/2023
 */
@Slf4j
@Getter
@ToString
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class Bot {

    private final Long selfId;
    private final WebSocketSession session;

    //群列表
    private List<GroupInfo> groups;

    private static final Map<Integer, String> EmojiMap;

    static  {
        EmojiMap = new HashMap<>();
        int[] emojiArr = new int[]{4,5,8,9,10,12,14,16,21,23,24,25,26,27,28,29,30,32,33,34,38,39,41,42,43,49,53,60,63,66,74,75,76,78,79,85,89,96,97,98,99,100,101,102,103,104,106,109,111,116,118,120,122,123,124,125,129,144,147,171,173,174,175,176,179,180,181,182,183,201,203,212,214,219,222,227,232,240,243,246,262,264,265,266,267,268,269,270,271,272,273,277,278,281,282,284,285,287,289,290,293,294,297,298,299,305,306,307,314,315,318,319,320,322,324,326,9728,9749,9786,10024,10060,10068,127801,127817,127822,127827,127836,127838,127847,127866,127867,127881,128027,128046,128051,128053,128074,128076,128077,128079,128089,128102,128104,128147,128157,128164,128166,128168,128170,128235,128293,128513,128514,128516,128522,128524,128527,128530,128531,128532,128536,128538,128540,128541,128557,128560,128563};
        for (int id : emojiArr) {
            EmojiMap.put(id, null);
        }
    }

    /**
     * call api 最终调用的方法
     * Send a WebSocket message
     *
     * @param requestBox box
     */
    private void sessionSend(RequestBox requestBox) {
        String text = JSONObject.toJSONString(requestBox);
        try {
            this.session.sendMessage(new TextMessage(text));
        } catch (IOException e) {
            log.info("{}[hb]-ws->[ob][{}]{}, exception[{}]{}",    ANSI.RED, selfId, text, e.getMessage(), ANSI.RESET);
        }
        if (!requestBox.getAction().equals("set_group_card")) {
            log.info("[hb]-ws->[ob][{}]{}", selfId, text);
        }
    }

    public void callApi(GocqAction action, Map<String, Object> paramsMap) {
        RequestBox requestBox = RequestBox.builder().action(action.name()).params(paramsMap).build();
        this.sessionSend(requestBox);
    }

    private static final Map<Long, EchoData> ECHO_DATA_MAP = new ConcurrentHashMap<>();

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
            String res = echoData.waitAndGet();
            log.info("[hb]<-ws-[ob][{}]{}", this.selfId, res);
            return res;
        }
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
        String data = this.callApiWaitResp(GocqAction.get_group_member_list,Map.of("group_id", groupId, "no_cache", noCache));
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

    public List<GroupInfo> getGroupList() {
        this.groups = this.getGroupList(false);
        //去除不需要修改群名的群号
        this.groups.removeIf(group -> group.getGroupId().equals(908253603L));
        return this.groups;
    }

    public GroupInfo getGroupInfo(Long groupId, boolean noCache) {
        String data = this.callApiWaitResp(GocqAction.get_group_info, Map.of("group_id", groupId, "no_cache", noCache));
        return JSONObject.parseObject(data, GroupInfo.class);
    }


    /**
     * 获取消息
     *
     * @param messageId message_id int32 消息id
     * @return MessageInfo
     */
    public MessageInfo getMsg(Integer messageId) {
        String data = this.callApiWaitResp(GocqAction.get_msg, Map.of("message_id", messageId));
        JSONObject jsonObject = JSONObject.parseObject(data);
        return jsonObject.toJavaObject(MessageInfo.class);
        //return JSONObject.parseObject(data, MessageInfo.class);//莫名其妙报错
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
        String data = this.callApiWaitResp(GocqAction.get_group_member_info,Map.of("group_id", groupId, "user_id", userId, "no_cache", noCache));
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
        this.callApi(GocqAction.set_group_special_title,Map.of("group_id", groupId, "user_id", userId, "special_title", specialTitle));
    }

    /**
     * 设置群昵称
     *
     * @param groupId group_id
     * @param userId  user_id
     * @param card    card
     */
    public void setGroupCard(Long groupId, Long userId, String card) {
        this.callApi(GocqAction.set_group_card,Map.of("group_id", groupId, "user_id", userId, "card", card));
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
        this.callApi(GocqAction.set_group_admin,Map.of("group_id", groupId, "user_id", userId, "enable", enable));
    }


    /**
     * 发送群消息
     *
     * @param groupId 群号
     * @param message 消息 if String 纯文本发送 if Message or MessageSegment 转化
     *                autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendGroupMessage(Long groupId, Object message) {
        boolean autoEscape = message instanceof String;
        if (!autoEscape && !(message instanceof MessageSegment) && !(message instanceof Message))
            throw new IllegalMessageTypeException();
        this.callApi(GocqAction.send_group_msg,Map.of("group_id", groupId, "message", message, "auto_escape", autoEscape));
    }


    /**
     * 发送私聊消息
     *
     * @param userId  对方qq
     * @param message 消息
     *                autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendPrivateMessage(Long userId, Object message) {
        boolean autoEscape = message instanceof String;
        if (!autoEscape && !(message instanceof MessageSegment) && !(message instanceof Message))
            throw new IllegalMessageTypeException();
        this.callApi(GocqAction.send_private_msg,Map.of("user_id", userId, "message", message, "auto_escape", autoEscape));
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

    public void sendForwardMsg(MessageEvent event, List<ForwardMessage> nodes) {
        if (event instanceof GroupMessageEvent gme) {
            this.sendGroupForwardMsg(gme.getGroupId(), nodes);
        } else {
            this.sendPrivateForwardMsg(event.getUserId(), nodes);
        }
    }


    /**
     * 根据事件, 来发送对应的消息
     *
     * @param event   event object
     * @param message 消息 Message | MessageSegment | String
     *                autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
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
        this.callApi(GocqAction.set_group_kick,Map.of("group_id", groupId, "user_id", userId, "reject_add_request", rejectAddRequest));
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

    public void setMsgEmojiLike(Integer messageId, Integer emojiId) {
        if (EmojiMap.containsKey(emojiId)) {
            this.callApi(GocqAction.set_msg_emoji_like, Map.of("message_id", messageId, "emoji_id", emojiId));
        }
    }
}
