package tech.chowyijiu.huhu_bot.plugins.chatgpt;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import tech.chowyijiu.huhu_bot.config.BotConfig;

import java.util.List;

/**
 * @author elastic chow
 * @date 20/7/2023
 */
public class GptReq {

    public static String chat(Long userId, String question) {
        OpenAiClient client = OpenAiClient.builder()
                    .apiKey(BotConfig.chatGptKey)
                    .keyStrategy(new KeyRandomStrategy())
                    .build();
        Message message = Message.builder().role(Message.Role.USER).content(question).build();
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(List.of(message)).build();
        StringBuilder sb = new StringBuilder();
        sb.append("[GPT3.5]");
        try {
            ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
            chatCompletionResponse.getChoices().forEach(e -> sb.append(e.getMessage().getContent()));
            return sb.toString();
        } catch (Exception e) {
            return "error: api key invoked";
        }

    }
}

