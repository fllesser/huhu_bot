package tech.chowyijiu.huhu_bot;

import cn.hutool.http.HttpRequest;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.chowyijiu.huhu_bot.entity.message.Message;
import tech.chowyijiu.huhu_bot.entity.message.MessageSegment;
import tech.chowyijiu.huhu_bot.plugins.fortnite.FortniteApi;
import tech.chowyijiu.huhu_bot.plugins.fortnite.ShopEntry;
import tech.chowyijiu.huhu_bot.plugins.resource_search.AliYunApi;
import tech.chowyijiu.huhu_bot.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@SuppressWarnings("all")
public class HuhuBotApplicationTests {


    @Test
    public void test2() {
        System.out.println(AliYunApi.signInList());
    }

    //@Test
    public void test1() {
        String privateMessageJson = "{\"post_type\":\"message\",\"message_type\":\"private\",\"time\":1684227772,\"self_id\":1487248817,\"sub_type\":\"friend\",\"raw_message\":\"。。\",\"font\":0,\"sender\":{\"age\":0,\"nickname\":\"Oswald Kan Mon May 15 2023 CST\",\"sex\":\"unknown\",\"user_id\":1942422015},\"message_id\":-2100915033,\"user_id\":1942422015,\"target_id\":1487248817,\"message\":\"。。\"}";
        String groupMessageJson = "{\"post_type\":\"message\",\"message_type\":\"group\",\"time\":1684228450,\"self_id\":1487248817,\"sub_type\":\"normal\",\"anonymous\":null,\"font\":0,\"message_seq\":6181,\"raw_message\":\"群消息\",\"sender\":{\"age\":0,\"area\":\"\",\"card\":\"ID：Jarid Harris\",\"level\":\"\",\"nickname\":\"Oswald Kan Mon May 15 2023 CST\",\"role\":\"owner\",\"sex\":\"unknown\",\"title\":\"\",\"user_id\":1942422015},\"user_id\":1942422015,\"group_id\":669026253,\"message\":\"群消息\",\"message_id\":-2058339141}";
    }

    //@Test
    public void testRegex() {
        String cq = "[CQ:at,qq=1487248817,file=https://dsfsdfsfsd,image=sdfsdfs.png]sdfsdf[CQ:file,qq=1487817,file=https://dsfsdfsfsd]";
        String regex = "\\[CQ:([a-z]+)((,([a-z]+)=([\\w:/.]+))+)]";
    }

    // @Test
    public void testMessage() {
        String cq1 = "测试[CQ:image,file=http://baidu.com/1.jpg]";
        String cq2 = "[CQ:image,file=http://baidu.com/1.jpg]";
        String cq3 = "[CQ:image,file=http://baidu.com/1.jpg]测试";
        String cq4 = "测试[CQ:image,file=https://baidu.com/1.jpg]sfdsaf[CQ:face,id=123]sdfsdf";
        Message message1 = Message.build(cq4);
        System.out.println(message1);
        System.out.println(message1.toArrayString());
        Message message2 = new Message();
        message2.add(MessageSegment.at(1942422015L));
        message2.add("测试");
        message2.add(MessageSegment.image("https://baidu.com/1.jpg", 0));
        System.out.println(message2);
    }

    //@Test
    public void testImageUtil() throws IOException {
        List<String> smallIcons = new ArrayList<>();
        for (ShopEntry shopEntry : FortniteApi.getShopEntries()) {
            for (ShopEntry.Item item : shopEntry.getItems()) {
                smallIcons.add(item.getImages().getSmallIcon());
            }
        }
        BufferedImage[] imgs = smallIcons.stream().map(url -> {
            InputStream inputStream = HttpRequest.get(url).execute().bodyStream();
            try {
                return ImageIO.read(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).toArray(BufferedImage[]::new);
        ImageUtil.mergeImage("/Users/yijiuchow/Desktop/1.png", imgs);
    }


    //@Test
    public void testHdHive() {
        //System.out.println(HdhiveReq.get1("asdfs "));
        //System.out.println(AliYunApi.dailySignIn("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI5ZWE2NjQ2MDc4ZmM0MzNmOTc1MWIyMzRmNjhhOWU2NSIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiVklFVy5BTExcIixcIlNUT1JBR0UuQUxMXCIsXCJTVE9SQUdFRklMRS5MSVNUXCIsXCJCQVRDSFwiLFwiT0FVVEguQUxMXCIsXCJJTUFHRS5BTExcIixcIklOVklURS5BTExcIixcIkFDQ09VTlQuQUxMXCIsXCJTWU5DTUFQUElORy5MSVNUXCIsXCJTWU5DTUFQUElORy5ERUxFVEVcIl0sXCJyb2xlXCI6XCJ1c2VyXCIsXCJyZWZcIjpcImh0dHBzOi8vd3d3LmFsaXl1bmRyaXZlLmNvbS9cIixcImRldmljZV9pZFwiOlwiYjhlMTc1OTNmNTU0NDYxYjhkMDI4MTcxODlkMDZiMTlcIn0iLCJleHAiOjE2ODk3Njg0MjUsImlhdCI6MTY4OTc2MTE2NX0.Xoj6OenC04Cb3Wy6YwnhgBvxLJGClp2Re7_YTqv2JC8iC_ZBaDUFGuXdqlmCK7t01jLSGufeNKlXpIxBUAE-_wISlKpysMdBSkQGfCjeKMhaenUrb8Q7SPBkaFtnFqbsUzij6rVaVP4KgBjjrwh2VvC00nSFcWTd5Csmy-3LBHw"));
        OpenAiStreamClient client = OpenAiStreamClient.builder()
                .apiKey(List.of("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",
                        "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",
                        "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",
                        "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",
                        "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"))
                //自定义key的获取策略：默认KeyRandomStrategy
                //.keyStrategy(new KeyRandomStrategy())
                .keyStrategy(new KeyRandomStrategy())
                //自己做了代理就传代理地址，没有可不不传
//                .apiHost("https://自己代理的服务器地址/")
                .build();
        ConsoleEventSourceListener eventSourceListener = new ConsoleEventSourceListener();
        com.unfbx.chatgpt.entity.chat.Message message = com.unfbx.chatgpt.entity.chat.Message.builder().role(com.unfbx.chatgpt.entity.chat.Message.Role.USER).content("你好啊我的伙伴！").build();
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(List.of(message)).build();
        client.streamChatCompletion(chatCompletion, eventSourceListener);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
