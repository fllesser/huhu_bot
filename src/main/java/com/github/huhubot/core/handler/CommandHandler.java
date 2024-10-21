package com.github.huhubot.core.handler;


import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@RequiredArgsConstructor
public class CommandHandler extends Handler{

    private final String[] commands;

    /**
     * 命令匹配
     */
    @Override
    public boolean match(final MessageEvent event) {
        String plainText = event.getMessage().plainText();
        for (String command : commands) {
            //匹配前缀命令
            if (plainText.startsWith(command)) {
                //去除触发的command, 并去掉头尾空格
                event.setCommandArgs(plainText.replaceFirst(command, "").trim());
                super.execute(event);
                return super.isBlock();
            }
        }
        return false;
    }

}
