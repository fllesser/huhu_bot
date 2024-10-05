package tech.flless.huhubot;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.flless.huhubot.config.WxConfig;
import tech.flless.huhubot.plugins.ai.ErnieClient;
import tech.flless.huhubot.plugins.ai.entity.TokenRes;
import tech.flless.huhubot.plugins.ai.entity.WxMessage;

import java.util.ArrayList;

@SuppressWarnings("all")
@Slf4j
@SpringBootTest
public class HuhuBotApplicationTests {


    //@Test
    //public void testImageUtil() throws IOException {
    //    List<String> smallIcons = new ArrayList<>();
    //    for (ShopEntry shopEntry : FortniteApi.getShopEntries()) {
    //        for (ShopEntry.Item item : shopEntry.getItems()) {
    //            smallIcons.add(item.getImages().getSmallIcon());
    //        }
    //    }
    //    BufferedImage[] imgs = smallIcons.stream().map(url -> {
    //        InputStream inputStream = HttpRequest.get(url).execute().bodyStream();
    //        try {
    //            return ImageIO.read(inputStream);
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        return null;
    //    }).toArray(BufferedImage[]::new);
    //    ImageUtil.mergeImage("/Users/yijiuchow/Desktop/1.png", imgs);
    //}

    @Resource
    private ErnieClient ernieClient;


    @Test
    public void test(){
        TokenRes token = ernieClient.getToken(WxConfig.ak, WxConfig.sk);
        ArrayList<WxMessage> wxMessages = new ArrayList<>(1);
        wxMessages.add(new WxMessage("user", "用rust实现一个websocket服务端"));
        String res = ernieClient.getCompletion(token.getAccess_token(), wxMessages);
        log.info("token:{}", token);
        log.info("res:{}", res);
    }



}
