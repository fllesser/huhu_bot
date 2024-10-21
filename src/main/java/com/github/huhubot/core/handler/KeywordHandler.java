package com.github.huhubot.core.handler;

import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Setter
@RequiredArgsConstructor
public class KeywordHandler extends Handler{
    private final String[] keywords;

    /**
     * 关键词匹配
     */
    @Override
    public boolean match(final MessageEvent event) {
        String plainText = event.getMessage().plainText();
        for (String keyword : keywords) {
            if (plainText.contains(keyword)) {
                super.execute(event);
                return super.isBlock(); //
            }
        }
        return false;
    }
}
