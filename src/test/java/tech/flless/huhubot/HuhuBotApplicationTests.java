package tech.flless.huhubot;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.flless.huhubot.plugins.ai.MainPlugin;
import tech.flless.huhubot.plugins.ai.entity.CompletionRes;

@SuppressWarnings("all")
@Slf4j
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


}
