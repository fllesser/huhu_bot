package com.github.huhubot.utils;

import ch.qos.logback.core.pattern.color.ANSIConstants;

public class ANSIUtil {

    public static String cyan(String str) {
        return ANSIConstants.ESC_START + ANSIConstants.CYAN_FG + str + ANSIConstants.RESET;
    }

    public static String red(String str) {
        return ANSIConstants.ESC_START + ANSIConstants.RED_FG + str + ANSIConstants.RESET;
    }
}
