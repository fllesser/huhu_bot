package tech.chowyijiu.huhu_bot.plugins.vedioResource;

import lombok.Builder;
import lombok.Getter;

/**
 * @author elastic chow
 * @date 18/7/2023
 */
@Builder
@Getter
public class WillSendData {

    private String title;
    private String description;
    private String type;
    private String url;

}
