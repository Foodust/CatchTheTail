package org.foodust.catchTheTail.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Message.BaseMessage;
import org.foodust.catchTheTail.Module.GameModule.CommandModule;

import java.util.Objects;

// 커맨드 를 할 수 있게 해줍니다!
public class CommandManager implements CommandExecutor {

    private final CommandModule commandModule;

    public CommandManager(CatchTheTail plugin) {
        this.commandModule = new CommandModule(plugin);
        Objects.requireNonNull(plugin.getCommand(BaseMessage.COMMAND_CATCH_THE_TAIL.getMessage())).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand(BaseMessage.COMMAND_CATCH_THE_TAIL.getMessage())).setTabCompleter(new CommandSub());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] data) {
        BaseMessage mainCommand = BaseMessage.getByMessage(label);
        if (!mainCommand.equals(BaseMessage.COMMAND_CATCH_THE_TAIL)) return false;
        BaseMessage byMessage = BaseMessage.getByMessage(data[0]);
        switch (byMessage) {
            case COMMAND_START -> commandModule.commandStart(sender, data);
            case COMMAND_STOP -> commandModule.commandStop(sender, data);
            case COMMAND_ADD -> commandModule.commandAdd(sender, data);
            case COMMAND_ITEM_ADD -> commandModule.commandItemAdd(sender, data);
            case COMMAND_RELOAD -> commandModule.commandReload(sender, data);
        }
        return true;
    }
}
