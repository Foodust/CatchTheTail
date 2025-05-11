package org.foodust.catchTheTail.Event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent; // 추가된 import
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
    private final CatchTheTail plugin;

    public PlayerEvent(CatchTheTail plugin) {
        this.plugin = plugin;
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

        // 플레이어가 다른 플레이어에 의해 죽었을 때만 꼬리 잡기 로직 실행
        if (attacker != null) {
            gameModule.checkTailCatch(attacker, victim);
            gameEndModule.checkGameEnd();
        }

        // 모든 죽음(지옥 포함)에서 마스터-노예 관계 유지
        // 별도 처리 없이 PlayerInfo 객체가 보존되므로 관계가 유지됨
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

    /**
     * 플레이어 리스폰 이벤트 처리 - 노예가 리스폰할 때 주인에게 텔레포트
     */
    @EventHandler
    public void playerRespawnEvent(PlayerRespawnEvent event) {
        if (!GameData.isGameRunning) return;
        Player player = event.getPlayer();

        // 플레이어 정보 가져오기
        PlayerInfo playerInfo = GameData.getPlayerInfo(player);
        if (playerInfo == null) return;

        // 플레이어가 노예인 경우(마스터가 있는 경우) 마스터에게 텔레포트
        if (playerInfo.getMaster() != null && playerInfo.getMaster().isOnline()) {
            // 리스폰 후 약간의 딜레이를 두고 텔레포트 실행
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.teleport(playerInfo.getMaster().getLocation());
                messageModule.sendPlayerC(player, "<yellow>주인에게 텔레포트 되었습니다!</yellow>");

                // 스케일 속성 다시 적용 (리스폰 후 초기화 방지)
                Bukkit.dispatchCommand(player, "scale set 0.5");
                Bukkit.dispatchCommand(player, "scale set pehkui:motion 1.5");
                Bukkit.dispatchCommand(player, "scale set pehkui:jump_height 1.2");
                Bukkit.dispatchCommand(player, "scale persist set true");
            }, 5L); // 짧은 딜레이로 리스폰이 완전히 처리된 후 실행
        }
    }
}