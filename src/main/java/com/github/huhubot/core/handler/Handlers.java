package com.github.huhubot.core.handler;

import java.util.ArrayList;
import java.util.Comparator;

public class Handlers extends ArrayList<Handler> {

    public void sort() {
        this.sort(Comparator.comparingInt(Handler::getPriority));
    }

}
