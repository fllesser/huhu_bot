package com.github.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.huhubot.adapters.onebot.v11.entity.response.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.github.huhubot.adapters.onebot.v11.entity.message.ForwardMessage;
import com.github.huhubot.adapters.onebot.v11.entity.message.Message;
import com.github.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import com.github.huhubot.adapters.onebot.v11.entity.request.RequestBox;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.constant.OnebotAction;
import com.github.huhubot.core.exception.ActionFailed;
import com.github.huhubot.core.exception.IllegalMessageTypeException;
import com.github.huhubot.utils.MistIdGenerator;
import com.github.huhubot.utils.ThreadPoolUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

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

    private static final Set<Integer> emojiSet;

    static {
        emojiSet = new HashSet<>(200);
        int[] emojiArr = new int[]{4, 5, 8, 9, 10, 12, 14, 16, 21, 23, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34, 38, 39, 41, 42, 43, 49, 53, 60, 63, 66,
                74, 75, 76, 78, 79, 85, 89, 96, 97, 98, 99, 100, 101, 102, 103, 104, 106, 109, 111, 116, 118, 120, 122, 123, 124, 125, 129, 144, 147,
                171, 173, 174, 175, 176, 179, 180, 181, 182, 183, 201, 203, 212, 214, 219, 222, 227, 232, 240, 243, 246, 262, 264, 265, 266, 267,
                268, 269, 270, 271, 272, 273, 277, 278, 281, 282, 284, 285, 287, 289, 290, 293, 294, 297, 298, 299, 305, 306, 307, 314, 315, 318,
                319, 320, 322, 324, 326, 9728, 9749, 9786, 10024, 10060, 10068, 127801, 127817, 127822, 127827, 127836, 127838, 127847,
                127866, 127867, 127881, 128027, 128046, 128051, 128053, 128074, 128076, 128077, 128079, 128089, 128102, 128104, 128147,
                128157, 128164, 128166, 128168, 128170, 128235, 128293, 128513, 128514, 128516, 128522, 128524, 128527, 128530, 128531,
                128532, 128536, 128538, 128540, 128541, 128557, 128560, 128563};
        for (int id : emojiArr) {
            emojiSet.add(id);
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
            log.error("[hb]-ws->[ob-{}]{}, exception[{}]", selfId, text, e.getMessage());
        }
        log.info("[hb]-ws->[ob-{}] action.{}[echo={},params={}]", selfId, requestBox.getAction(), requestBox.getEcho(), JSONObject.toJSONString(requestBox.getParams()));
    }

    public void callApi(OnebotAction action, Map<String, Object> paramsMap) {
        RequestBox requestBox = RequestBox.builder().action(action.name()).params(paramsMap).build();
        this.sessionSend(requestBox);
    }

    //应该没有线程安全问题（确信
    private static final Map<Long, EchoData> echoDataMap = new HashMap<>();

    protected static void setAndNotify(long echo, Object data) {
        if (!echoDataMap.containsKey(echo)) return;
        EchoData echoData = echoDataMap.get(echo);
        synchronized (echoData) {
            echoData.data = data;
            echoData.notify();
        }
    }

    static class EchoData {

        private static final long timeout = 10000L;

        private final long echo;
        private Object data; //JSONArray | JsonObject

        private EchoData(long echo) {
            this.echo = echo;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", "action.resp" + "[", "]")
                    .add("echo=" + echo)
                    .add("data=" + data)
                    .toString();
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
    public Object callApiWaitResp(OnebotAction action, Map<String, Object> paramsMap) {
        long echo = MistIdGenerator.nextId();
        RequestBox requestBox = RequestBox.builder().action(action.name()).params(paramsMap).echo(echo).build();
        EchoData echoData = new EchoData(echo);
        echoDataMap.put(echo, echoData);
        //因为存在当前线程还没获得锁, 其他线程就抢先获得了锁的情况, 所以先获取锁, 再sessionSend
        synchronized (echoData) {
            this.sessionSend(requestBox);
            try {
                echoData.wait(10000L);
            } catch (InterruptedException e) {
                throw new ActionFailed("等待响应数据, 出现线程中断异常, " + requestBox.toString());
            } finally {
                echoDataMap.remove(echo);
            }
        }
        if (echoData.data == null) throw new ActionFailed(requestBox.toString());
        log.info("[hb]<-ws-[ob-{}] {}]", this.selfId, echoData);
        return echoData.data;
    }


    public void deleteFriend(Long userId) {
        this.callApi(OnebotAction.delete_friend, Map.of("user_id", userId));
    }

    /**
     * 获取bot信息
     *
     * @return SelfInfo
     */
    public SelfInfo getLoginInfo() {
        Object data = this.callApiWaitResp(OnebotAction.get_login_info, null);
        if (data instanceof JSONObject jsonObject) {
            return jsonObject.toJavaObject(SelfInfo.class);
        } else return null;
    }

    /**
     * 获取好友列表
     *
     * @return List<FriendInfo>
     */
    public List<FriendInfo> getFriendList() {
        Object data = this.callApiWaitResp(OnebotAction.get_friend_list, null);
        if (data instanceof JSONArray array) {
            return array.toJavaList(FriendInfo.class);
        } else return List.of();
    }


    /**
     * 获取群成员列表
     *
     * @param groupId groupId
     * @param noCache 为true时, 不使用缓存
     **/
    public List<GroupMember> getGroupMembers(Long groupId, boolean noCache) {
        Object data = this.callApiWaitResp(OnebotAction.get_group_member_list, Map.of("group_id", groupId, "no_cache", noCache));
        if (data instanceof JSONArray array) {
            return array.toJavaList(GroupMember.class);
        } else return List.of();
    }

    /**
     * 获取群列表
     *
     * @param noCache 默认false, 使用缓存
     * @return List<GroupInfo>
     */
    public List<GroupInfo> getGroupList(boolean noCache) {
        Object data = this.callApiWaitResp(OnebotAction.get_group_list, Map.of("no_cache", noCache));
        if (data instanceof JSONArray array) {
            return array.toJavaList(GroupInfo.class);
        } else return List.of();
    }

    public List<GroupInfo> getGroupList() {
        this.groups = this.getGroupList(false);
        //去除不需要修改群名的群号
        this.groups.removeIf(group -> group.getGroupId().equals(908253603L));
        return this.groups;
    }

    public GroupInfo getGroupInfo(Long groupId, boolean noCache) {
        Object data = this.callApiWaitResp(OnebotAction.get_group_info, Map.of("group_id", groupId, "no_cache", noCache));
        if (data instanceof JSONObject json) {
            return json.toJavaObject(GroupInfo.class);
        } else return null;
    }


    /**
     * 获取消息
     *
     * @param messageId message_id int32 消息id
     * @return MessageInfo
     */
    public MessageInfo getMsg(Integer messageId) {
        Object data = this.callApiWaitResp(OnebotAction.get_msg, Map.of("message_id", messageId));
        if (data instanceof JSONObject json) {
            return json.toJavaObject(MessageInfo.class);
        } else return null;
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
        Object data = this.callApiWaitResp(OnebotAction.get_group_member_info, Map.of("group_id", groupId, "user_id", userId, "no_cache", noCache));
        if (data instanceof JSONObject json) {
            return json.toJavaObject(GroupMember.class);
        } else return null;
    }

    /**
     * 设置群头衔, 仅可在机器人为群主时有效
     *
     * @param groupId      group_id
     * @param userId       user_id
     * @param specialTitle special_title
     */
    public void setGroupSpecialTitle(Long groupId, Long userId, String specialTitle) {
        this.callApi(OnebotAction.set_group_special_title, Map.of("group_id", groupId, "user_id", userId, "special_title", specialTitle));
    }

    /**
     * 设置群昵称
     *
     * @param groupId group_id
     * @param userId  user_id
     * @param card    card
     */
    public void setGroupCard(Long groupId, Long userId, String card) {
        this.callApi(OnebotAction.set_group_card, Map.of("group_id", groupId, "user_id", userId, "card", card));
    }

    /**
     * 群打卡
     *
     * @param groupId group_id
     */
    public void sendGroupSign(Long groupId) {
        this.callApi(OnebotAction.send_group_sign, Map.of("group_id", groupId));
    }

    /**
     * 设置群管理员
     *
     * @param groupId group_id
     * @param userId  user_id
     * @param enable  true 为设置, false 为取消
     */
    public void setGroupAdmin(Long groupId, Long userId, boolean enable) {
        this.callApi(OnebotAction.set_group_admin, Map.of("group_id", groupId, "user_id", userId, "enable", enable));
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
        this.callApi(OnebotAction.send_group_msg, Map.of("group_id", groupId, "message", message, "auto_escape", autoEscape));
    }


    public Future<Integer> asyncSendGroupMessage(Long groupId, Object message) {
        return ThreadPoolUtil.AsyncExecutor.submit(() -> {
            boolean autoEscape = message instanceof String;
            if (!autoEscape && !(message instanceof MessageSegment) && !(message instanceof Message))
                throw new IllegalMessageTypeException();
            Object data = this.callApiWaitResp(OnebotAction.send_group_msg, Map.of("group_id", groupId, "message", message, "auto_escape", autoEscape));
            if (data instanceof JSONObject jsonObject) {
                return jsonObject.getInteger("message_id");
            } else return null;
        });
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
        this.callApi(OnebotAction.send_private_msg, Map.of("user_id", userId, "message", message, "auto_escape", autoEscape));
    }

    public Future<Integer> asyncSendPrivateMessage(Long userId, Object message) {
        return ThreadPoolUtil.AsyncExecutor.submit(() -> {
            boolean autoEscape = message instanceof String;
            if (!autoEscape && !(message instanceof MessageSegment) && !(message instanceof Message))
                throw new IllegalMessageTypeException();
            Object data = this.callApiWaitResp(OnebotAction.send_private_msg, Map.of("user_id", userId, "message", message, "auto_escape", autoEscape));
            if (data instanceof JSONObject jsonObject) {
                return jsonObject.getInteger("message_id");
            } else return null;
        });
    }


    /**
     * 撤回消息
     *
     * @param messageId Integer
     */
    public void deleteMsg(Integer messageId) {
        this.callApi(OnebotAction.delete_msg, Map.of("message_id", messageId));
    }

    /**
     * 标记消息为已读
     *
     * @param messageId Integer
     */
    public void markMsgAsRead(Integer messageId) {
        this.callApi(OnebotAction.mark_msg_as_read, Map.of("message_id", messageId));
    }

    /**
     * @param messageId Integer
     */
    public String getForwardMsg(Integer messageId) {
        return this.callApiWaitResp(OnebotAction.get_forward_msg, Map.of("message_id", messageId)).toString();
    }

    /**
     * 群组发送合并转发消息
     *
     * @param groupId 群号
     * @param nodes   nodes
     */
    public void sendGroupForwardMsg(Long groupId, List<ForwardMessage> nodes) {
        this.callApi(OnebotAction.send_group_forward_msg, Map.of("group_id", groupId, "messages", nodes));
    }

    /**
     * 私聊发送合并转发消息
     *
     * @param userId qq
     * @param nodes  nodes
     */
    public void sendPrivateForwardMsg(Long userId, List<ForwardMessage> nodes) {
        this.callApi(OnebotAction.send_private_forward_msg, Map.of("user_id", userId, "messages", nodes));
    }


    public void sendForwardMsg(MessageEvent event, List<ForwardMessage> nodes) {
        if (event instanceof GroupMessageEvent gme) {
            this.sendGroupForwardMsg(gme.getGroupId(), nodes);
        } else {
            this.sendPrivateForwardMsg(event.getUserId(), nodes);
        }
    }

    public int buildForwardMsg(Long groupId, List<ForwardMessage> nodes) {
        Object resp = this.callApiWaitResp(OnebotAction.send_forward_msg, Map.of("group_id" ,groupId, "messages", nodes));
        if (resp instanceof JSONObject jsonObject) {
            return jsonObject.getInteger("message_id");
        } else return -1;
    }




    /**
     * 根据事件, 来发送对应的消息
     * 建议使用event.reply(Object message)
     *
     * @param event   event object
     * @param message 消息 Message | MessageSegment | String
     *                autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    @Deprecated
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

    public void setGroupKick(Long groupId, Long userId, boolean rejectAddRequest) {
        this.callApi(OnebotAction.set_group_kick, Map.of("group_id", groupId, "user_id", userId, "reject_add_request", rejectAddRequest));
    }


    /**
     * 群单人禁言
     *
     * @param groupId  group_id
     * @param userId   user_id
     * @param duration duration 单位秒 default 30 * 60 | 0 表示取消禁言
     */
    public void setGroupBan(Long groupId, Long userId, int duration) {
        this.callApi(OnebotAction.set_group_ban, Map.of("group_id", groupId, "user_id", userId, "duration", duration));
    }

    /**
     * 群全体禁言
     *
     * @param groupId group_id
     * @param enable  默认true 禁言
     */
    public void setGroupWholeBan(Long groupId, boolean enable) {
        this.callApi(OnebotAction.set_group_whole_ban, Map.of("group_id", groupId, "enable", enable));
    }

    public void setMsgEmojiLike(Integer messageId, Integer emojiId) {
        if (emojiSet.contains(emojiId)) {
            this.callApi(OnebotAction.set_msg_emoji_like, Map.of("message_id", messageId, "emoji_id", emojiId));
        }
    }

    public void groupPoke(Long groupId, Long userId) {
        this.callApi(OnebotAction.group_poke, Map.of("group_id", groupId, "user_id", userId));
    }
}
