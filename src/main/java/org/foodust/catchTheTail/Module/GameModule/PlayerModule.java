package org.foodust.catchTheTail.Module.GameModule;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Data.TaskData;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

public class PlayerModule {
    private final CatchTheTail plugin;
    private final TailModule tailModule;
    private final MessageModule messageModule;

    public PlayerModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.tailModule = new TailModule(plugin);
        this.messageModule = new MessageModule(plugin);
    }

    public void setupPlayer(Player player, String index) {
        PlayerInfo info = PlayerInfo.builder()
                .player(player)
                .index(index)
                .build();
        GameData.gamePlayers.put(player, info);
    }

    public void bindPlayers(Player master, Player slave) {
        PlayerInfo masterInfo = GameData.gamePlayers.get(master);
        PlayerInfo slaveInfo = GameData.gamePlayers.get(slave);

        if (masterInfo != null && slaveInfo != null) {

            slaveInfo.setMaster(master);
            masterInfo.getSlaves().add(slave);

            // 노예 플레이어를 주인 근처로 계속 텔레포트 - 거리 수정
            BukkitTask bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!GameData.isGameRunning || slave.getWorld() != master.getWorld() || masterInfo.isEliminated()) {
                        this.cancel();
                        return;
                    }

                    Location masterLoc = master.getLocation();
                    Location slaveLoc = slave.getLocation();

                    if (slaveLoc.distance(masterLoc) > 10) { // 거리 10칸으로 수정
                        Vector direction = masterLoc.toVector().subtract(slaveLoc.toVector()).normalize().multiply(1.5).add(new Vector(0, 0.5, 0));
                        slave.setVelocity(direction);
                        messageModule.sendPlayerActionBar(slave, "<yellow>주인에게 당겨집니다...</yellow>");
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
            TaskData.TASKS.add(bukkitTask);
        }
    }
}