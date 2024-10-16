package tech.flless.huhubot.config;

import tech.flless.huhubot.utils.IocUtil;

public class GlobalConfig {

    public static final BotConfig botCf;
    public static final ApiSpaceConfig apiSpaceCf;
    public static final ReechoConfig reechoCf;
    public static final ErrieConfig wxCf;

    static {
        botCf = IocUtil.getBean(BotConfig.class);
        apiSpaceCf = IocUtil.getBean(ApiSpaceConfig.class);
        reechoCf = IocUtil.getBean(ReechoConfig.class);
        wxCf = IocUtil.getBean(ErrieConfig.class);
    }
}
