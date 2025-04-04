package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.ConfigData;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.TaskData;
import org.foodust.catchTheTail.Module.BaseModule.ConfigModule;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;
import org.foodust.catchTheTail.Module.BaseModule.TaskModule;

import java.util.*;

public class CommandModule {

    private final CatchTheTail plugin;
    private final TaskModule taskModule;
    private final ConfigModule configModule;
    private final PlayerModule playerModule;
    private final TailModule tailModule;
    private final MessageModule messageModule;

    public CommandModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.configModule = new ConfigModule(plugin);
        this.playerModule = new PlayerModule(plugin);
        this.messageModule = new MessageModule(plugin);
        this.tailModule = new TailModule(plugin);
        this.taskModule = new TaskModule(plugin);
    }

    public void commandStart(CommandSender sender, String[] data) {
        if (GameData.gamePlayers.isEmpty()) {
            messageModule.sendPlayerC(sender, "등록된 플레이어가 없습니다.");
            return;
        }
        GameData.isGameRunning = true;

        BukkitTask bukkitTask = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                if (time++ >= 3) {
                    // 각 플레이어에게 기본 아이템 지급
                    GameData.gamePlayers.forEach((player, playerInfo) -> {
                        configModule.getBaseItem(player, playerInfo.getIndex());
                    });


                    // 게임 시작 메시지 전송
                    String startMessage = ConfigData.getMessage("game_start");
                    messageModule.broadcastMessageC(startMessage.isEmpty() ?
                            "<green>게임이 시작되었습니다!</green>" : startMessage);

                    // 게임 시작 사운드 재생
                    ConfigData.SoundInfo startSound = ConfigData.getSound("game_start");
                    if (startSound != null) {
                        GameData.gamePlayers.keySet().forEach(player -> {
                            player.playSound(player, startSound.sound(), 1f, 1f);
                        });
                    }

                    // 게임 시작 안내 액션바 메시지 전송
                    String instructionMessage = ConfigData.getMessage("game_start_instruction");
                    if (!instructionMessage.isEmpty()) {
                        GameData.gamePlayers.keySet().forEach(player -> {
                            messageModule.sendTitle(player, instructionMessage,0,1,0);
                        });
                    }
                    this.cancel();
                } else {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        messageModule.sendTitle(player, ConfigData.getMessage("game_start_" + time), 0, 1, 0);
                    });
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
        TaskData.TASKS.add(bukkitTask);


        tailModule.initializeColors();
    }

    public void commandStop(CommandSender sender, String[] data) {
        configModule.initialize();
        messageModule.broadcastMessageC("게임이 종료 되었습니다.");
    }

    public void commandAdd(CommandSender sender, String[] data) {
        if (data.length < 2) {
            messageModule.sendPlayerC(sender, "플레이어를 지정해야 합니다.");
            return;
        }
        List<String> playerNames = new ArrayList<>(new HashSet<>(Arrays.asList(data).subList(1, data.length)));
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                messageModule.sendPlayerC(sender, playerName + " 는 등록되지 않았습니다.");
            } else {
                playerModule.setupPlayer(player, String.valueOf(i + 1)); // 인덱스를 1부터 시작하도록 수정
                messageModule.sendPlayerC(sender, playerName + " 는 등록 되었습니다.");
            }
        }
    }

    public void commandItemAdd(CommandSender sender, String[] data) {
        if (data.length < 2) {
            messageModule.sendPlayerC(sender, "번호를 지정해주세요.");
            return;
        }
        if (!(sender instanceof Player player))
            return;
        if (configModule.setBaseItem(player, data[1])) {
            messageModule.sendPlayerC(sender, "아이템 설정이 완료 되었습니다. 번호 : " + data[1]);
        } else {
            messageModule.sendPlayerC(sender, "<red>아이템 설정이 잘못 되었습니다.</red>");
        }
    }

    public void commandReload(CommandSender sender, String[] data) {
        configModule.initialize();
        messageModule.sendPlayerC(sender, "리로드 되었습니다.");
    }

}
