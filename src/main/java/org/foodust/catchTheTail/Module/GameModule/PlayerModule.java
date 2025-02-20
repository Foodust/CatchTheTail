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

public class PlayerModule {
    private final CatchTheTail plugin;
    private final TailModule tailModule;

    public PlayerModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.tailModule = new TailModule(plugin);
    }

    public void setupPlayer(Player player) {
        PlayerInfo info = new PlayerInfo(player);
        GameData.gamePlayers.put(player, info);
        // 기본 아이템 지급
        giveBaseItems(player);
    }


    private void giveBaseItems(Player player) {
    }

    public void bindPlayers(Player master, Player slave) {
        PlayerInfo masterInfo = GameData.gamePlayers.get(master);
        PlayerInfo slaveInfo = GameData.gamePlayers.get(slave);

        if (masterInfo != null && slaveInfo != null) {
            try{
                slave.registerAttribute(Attribute.SCALE);
                AttributeInstance attribute = slave.getAttribute(Attribute.SCALE);
                if(attribute != null) {
                    attribute.setBaseValue(0.7);
                }
            }catch (Exception ignore){
            }

            slaveInfo.setMaster(master);
            masterInfo.getSlaves().add(slave);

            // 노예 플레이어를 주인 근처로 계속 텔레포트
            BukkitTask bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!GameData.isGameRunning || slave.getWorld() != master.getWorld() ||masterInfo.isEliminated()) {
                        this.cancel();
                        return;
                    }

                    Location masterLoc = master.getLocation();
                    Location slaveLoc = slave.getLocation();

                    if (slaveLoc.distance(masterLoc) > 2) {
                        Vector direction = masterLoc.toVector().subtract(slaveLoc.toVector()).normalize().multiply(1.1);
                        slave.setVelocity(direction);
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
            TaskData.TASKS.add(bukkitTask);
        }
    }
}
