package tech.flless.huhubot.plugins.chatgpt;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;
import tech.flless.huhubot.config.BotConfig;

import java.util.List;

/**
 * @author elastic chow
 * @date 20/7/2023
 */

@Component
public class GptClient {

    private OpenAiClient client;

    public String chat(String question) {
        if (client == null) {
            client = OpenAiClient.builder().apiKey(BotConfig.chatGptKey).keyStrategy(new MyKeyStrategy()).build();
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
            client.getApiKey().remove(MyKeyStrategy.curKey);
            return "error: api key invoked";
        }
    }
}

