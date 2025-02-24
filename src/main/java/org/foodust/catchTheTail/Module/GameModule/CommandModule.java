package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Module.BaseModule.ConfigModule;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

public class CommandModule {

    private final CatchTheTail plugin;
    private final ConfigModule configModule;
    private final PlayerModule playerModule;
    private final MessageModule messageModule;

    public CommandModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.configModule = new ConfigModule(plugin);
        this.playerModule = new PlayerModule(plugin);
        this.messageModule = new MessageModule(plugin);
    }

    public void commandStart(CommandSender sender, String[] data) {
        if (GameData.gamePlayers.isEmpty()) {
            messageModule.sendPlayerC(sender, "등록된 플레이어가 없습니다.");
            return;
        }

        GameData.gamePlayers.forEach((player, playerInfo) -> {
            playerModule.giveBaseItems(player, playerInfo.getIndex());
        });

        GameData.isGameRunning = true;
        messageModule.broadcastMessageC("게임이 시작 되었습니다!");
    }

    public void commandStop(CommandSender sender, String[] data) {
        configModule.initialize();
    }

    public void commandAdd(CommandSender sender, String[] data) {
        if (data.length < 2) {
            messageModule.sendPlayerC(sender, "플레이어를 지정해야 합니다.");
            return;
        }
        for (int i = 0; i < data.length; i++) {
            String playerName = data[i];
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                messageModule.sendPlayerC(sender, playerName + " 는 등록되지 않았습니다.");
            } else {
                playerModule.setupPlayer(player, i);
            }
        }
    }

    public void commandItemAdd(CommandSender sender, String[] data) {

    }

    public void commandReload(CommandSender sender, String[] data) {
        messageModule.sendPlayerC(sender, "리로드 되었습니다.");
    }

}
