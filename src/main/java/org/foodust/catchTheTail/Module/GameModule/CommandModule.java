package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.command.CommandSender;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Module.BaseModule.ConfigModule;

public class CommandModule {

    private final CatchTheTail plugin;
    private final ConfigModule configModule;
    private final PlayerModule playerModule;

    public CommandModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.configModule = new ConfigModule(plugin);
        this.playerModule = new PlayerModule(plugin);
    }

    public void commandStart(CommandSender sender, String[] data) {

    }

    public void commandStop(CommandSender sender, String[] data) {
        configModule.initialize();
    }

    public void commandAdd(CommandSender sender, String[] data) {

    }

    public void commandItemAdd(CommandSender sender, String[] data) {

    }

    public void commandReload(CommandSender sender, String[] data) {

    }

}
