package tech.chowyijiu.huhubot;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import tech.chowyijiu.huhubot.plugins.fortnite.FortniteApi;
import tech.chowyijiu.huhubot.plugins.fortnite.ShopEntry;
import tech.chowyijiu.huhubot.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@SuppressWarnings("all")
@Slf4j
public class HuhuBotApplicationTests {


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




}
