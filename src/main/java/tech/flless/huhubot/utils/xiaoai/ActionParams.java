package tech.flless.huhubot.utils.xiaoai;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author elastic chow
 * @date 24/7/2023
 */
@Getter
@Builder
public class ActionParams {

    private String did;
    private Integer siid;
    private Integer aiid;
    private List<String> in;
}
