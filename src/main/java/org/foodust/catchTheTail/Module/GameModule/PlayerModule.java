package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Data.TaskData;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        // Store using UUID as key
        GameData.gamePlayers.put(player.getUniqueId(), info);
    }

    public void bindPlayers(Player master, Player slave) {
        PlayerInfo masterInfo = GameData.getPlayerInfo(master);
        PlayerInfo slaveInfo = GameData.getPlayerInfo(slave);

        if (masterInfo != null && slaveInfo != null) {
            // Store slave's existing slaves before modifying relationships
            List<Player> slavesOfSlave = new ArrayList<>(slaveInfo.getSlaves());

            // Clear slave's slaves list since they're being transferred
            slaveInfo.getSlaves().clear();
            slaveInfo.getSlavesUUIDs().clear();

            // Set slave's master
            slaveInfo.setMaster(master);
            slaveInfo.setMasterUUID(master.getUniqueId());

            // Add slave to master's slaves
            masterInfo.getSlaves().add(slave);
            masterInfo.getSlavesUUIDs().add(slave.getUniqueId());

            // Transfer all slave's slaves to master
            for (Player slaveOfSlave : slavesOfSlave) {
                PlayerInfo slaveOfSlaveInfo = GameData.getPlayerInfo(slaveOfSlave);
                if (slaveOfSlaveInfo != null) {
                    slaveOfSlaveInfo.setMaster(master);
                    slaveOfSlaveInfo.setMasterUUID(master.getUniqueId());

                    masterInfo.getSlaves().add(slaveOfSlave);
                    masterInfo.getSlavesUUIDs().add(slaveOfSlave.getUniqueId());
                }
            }

            // 노예 플레이어를 주인 근처로 계속 텔레포트 - 거리 수정
            BukkitTask bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!GameData.isGameRunning || slave.getWorld() != master.getWorld() ||
                            masterInfo.isEliminated() || !slave.isOnline() || !master.isOnline()) {
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
            }.runTaskTimer(plugin, 0L, 20L);
            TaskData.TASKS.add(bukkitTask);
        }
    }
}