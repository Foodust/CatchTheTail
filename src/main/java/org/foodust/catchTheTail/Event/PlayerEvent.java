package org.foodust.catchTheTail.Event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Module.BaseModule.ConfigModule;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;
import org.foodust.catchTheTail.Module.GameModule.GameEndModule;
import org.foodust.catchTheTail.Module.GameModule.GameModule;
import org.foodust.catchTheTail.Module.GameModule.PlayerModule;

import java.util.UUID;

public class PlayerEvent implements Listener {

    private final GameModule gameModule;
    private final GameEndModule gameEndModule;
    private final PlayerModule playerModule;
    private final MessageModule messageModule;
    private final ConfigModule configModule;

    public PlayerEvent(CatchTheTail plugin) {
        this.gameModule = new GameModule(plugin);
        this.gameEndModule = new GameEndModule(plugin);
        this.playerModule = new PlayerModule(plugin);
        this.messageModule = new MessageModule(plugin);
        this.configModule = new ConfigModule(plugin);
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        if (!GameData.isGameRunning) return;
        Player victim = event.getEntity();
        Player attacker = victim.getKiller();
        if (attacker == null) return;
        gameModule.checkTailCatch(attacker, victim);
        gameEndModule.checkGameEnd();
    }

    @EventHandler
    public void playerEntityDamageEvent(EntityDamageByEntityEvent event) {
        if (!GameData.isGameRunning) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        gameModule.checkAttackSlaveToMaster(event, attacker, victim);
    }

    // Add PlayerQuitEvent handler to save player state when they disconnect
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        if (!GameData.isGameRunning) return;
        Player player = event.getPlayer();

        // Don't remove data, just mark as disconnected
        PlayerInfo playerInfo = GameData.getPlayerInfo(player);
        if (playerInfo != null) {
            playerInfo.setDisconnected(true);
            playerInfo.setLastKnownName(player.getName());
        }
    }

    // Add PlayerJoinEvent handler to restore player state when they reconnect
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        if (!GameData.isGameRunning) return;
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if player was in the game
        if (GameData.gamePlayers.containsKey(playerUUID)) {
            PlayerInfo playerInfo = GameData.gamePlayers.get(playerUUID);
            playerInfo.setPlayer(player);
            playerInfo.setDisconnected(false);

            // Restore player's state
            if (playerInfo.getMasterUUID() != null) {
                // Reconnect with master if they're online
                Player master = Bukkit.getPlayer(playerInfo.getMasterUUID());
                if (master != null && master.isOnline()) {
                    playerInfo.setMaster(master);
                    playerModule.bindPlayers(master, player);
                }
            }

            // Restore scale for slaves
            if (playerInfo.getMaster() != null) {
                Bukkit.dispatchCommand(player, "scale set 0.5");
                Bukkit.dispatchCommand(player, "scale set pehkui:motion 1.5");
                Bukkit.dispatchCommand(player, "scale set pehkui:jump_height 1.2");
                Bukkit.dispatchCommand(player, "scale persist set true");
            }
        }
    }
}