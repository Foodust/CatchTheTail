package org.foodust.catchTheTail.Message;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum BaseMessage {

    // prefix
    PREFIX(""),
    PREFIX_C("<gradiant:green:blue><bold>[꼬리잡기]</bold></gradiant> "),

    // command
    COMMAND_(""),

    // 기본
    DEFAULT("기본"),
        // Error
    ERROR("에러"),
    ERROR_COMMAND("<red><bold>잘못된 명령어입니다.")
    ;

    private final String message;

    BaseMessage(String message) {
        this.message = message;
    }

    private static final Map<String, BaseMessage> commandInfo = new HashMap<>();

    static {
        for (BaseMessage baseMessage : EnumSet.range(COMMAND_, COMMAND_)) {
            commandInfo.put(baseMessage.message, baseMessage);
        }
    }

    public static BaseMessage getByMessage(String message) {
        return commandInfo.getOrDefault(message,ERROR);
    }
}
