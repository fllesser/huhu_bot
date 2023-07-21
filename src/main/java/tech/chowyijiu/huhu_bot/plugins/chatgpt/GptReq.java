package tech.chowyijiu.huhu_bot.plugins.chatgpt;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import lombok.Builder;
import lombok.Getter;
import tech.chowyijiu.huhu_bot.config.BotConfig;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 20/7/2023
 */
public class GptReq {

    private static final Map<Long, Session> clientMap = new HashMap<>();

    @Builder
    @Getter
    private static class Session {
        private Long id;
        private LocalDateTime expireTime;
        private OpenAiClient client;

        public boolean checkExpire() {
            return LocalDateTime.now().isAfter(expireTime);
        }
    }

    public static String chat(Long userId, String question) {
        Session session = clientMap.get(userId);
        OpenAiClient client;
        if (session == null || session.checkExpire()) {
            client = OpenAiClient.builder()
                    .apiKey(BotConfig.chatGptKey)
                    .keyStrategy(new KeyRandomStrategy())
                    .build();
            clientMap.put(userId,
                    Session.builder().expireTime(LocalDateTime.now().plusMinutes(5)).client(client).build());
        } else {
            client = session.getClient();
        }
        Message message = Message.builder().role(Message.Role.USER).content(question).build();
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(List.of(message)).build();
        StringBuilder sb = new StringBuilder();
        sb.append("[GPT3.5]");
        try {
            ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
            chatCompletionResponse.getChoices().forEach(e -> sb.append(e.getMessage().getContent()));
            return sb.toString();
        } catch (Exception e) {
            clientMap.remove(userId);
            return " api key invoked";
        }

    }
}

