package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;
import org.foodust.catchTheTail.Module.BaseModule.TaskModule;

import java.util.List;
import java.util.Objects;

public class GameModule {
    private final CatchTheTail plugin;
    private final PlayerModule playerModule;
    private final TailModule tailModule;
    private final MessageModule messageModule;
    private final TaskModule taskModule;

    public GameModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.playerModule = new PlayerModule(plugin);
        this.tailModule = new TailModule(plugin);
        this.messageModule = new MessageModule(plugin);
        this.taskModule = new TaskModule(plugin);
    }

    // Method to initialize player colors when game starts
    public void initializePlayerColors() {
        GameData.gamePlayers.values().forEach(playerInfo -> {
            Player player = playerInfo.getPlayer();
            if (player != null && player.isOnline()) {
                ItemStack woolItem = player.getInventory().getItem(0);
                if (woolItem != null && isWoolMaterial(woolItem.getType())) {
                    playerInfo.setWoolColor(woolItem.getType());
                }
            }
        });
    }

    private boolean isWoolMaterial(Material material) {
        for (Material color : GameData.COLORS) {
            if (material == color) {
                return true;
            }
        }
        return false;
    }

    public void checkTailCatch(Player attacker, Player victim) {
        // Check if victim is already a slave
        PlayerInfo victimInfo = GameData.getPlayerInfo(victim);
        if (victimInfo != null && victimInfo.getMaster() != null) {
            return;
        }

        ItemStack attackerColorWool = attacker.getInventory().getItem(0);
        ItemStack victimColorWool = victim.getInventory().getItem(0);

        if (attackerColorWool == null || victimColorWool == null)
            return;

        // 이미 잡은 꼬리가 있는지 확인
        PlayerInfo attackerInfo = GameData.getPlayerInfo(attacker);
        List<Player> attackerSlaves = attackerInfo.getSlaves();

        // 잡아야 하는 다음 꼬리 색 결정
        Material shouldCatch;

        if (attackerSlaves.isEmpty()) {
            // 첫 번째 꼬리를 잡는 경우
            shouldCatch = tailModule.getNextColor(attackerColorWool.getType());
        } else {
            // 이미 꼬리가 있는 경우, 마지막 꼬리의 다음 색깔을 확인
            Player lastSlave = attackerSlaves.get(attackerSlaves.size() - 1);
            ItemStack lastSlaveColorWool = lastSlave.getInventory().getItem(0);

            // 마지막 꼬리가 색깔을 가지고 있지 않은 경우(예외 상황)
            shouldCatch = tailModule.getNextColor(Objects.requireNonNullElse(lastSlaveColorWool, attackerColorWool).getType());
        }

        // Store the victim's color for reconnection
        assert victimInfo != null;

        if (victimColorWool.getType() == shouldCatch) {
            // 성공적인 꼬리 잡기
            playerModule.bindPlayers(attacker, victim);
            messageModule.sendPlayerC(attacker, "<green>성공적으로 꼬리를 잡았습니다!</green>");
            Bukkit.dispatchCommand(victim,"scale set 0.5");
            Bukkit.dispatchCommand(victim,"scale set pehkui:motion 1.5");
            Bukkit.dispatchCommand(victim,"scale set pehkui:jump_height 1.2");
            Bukkit.dispatchCommand(victim,"scale persist set true");
        } else {
            // 잘못된 꼬리 잡기 - 수정된 부분
            attackerInfo.setEliminated(true);
            playerModule.bindPlayers(victim, attacker);

            Bukkit.dispatchCommand(attacker,"scale set 0.5");
            Bukkit.dispatchCommand(attacker,"scale set pehkui:motion 1.5");
            Bukkit.dispatchCommand(attacker,"scale set pehkui:jump_height 1.2");
            Bukkit.dispatchCommand(attacker,"scale persist set true");
            // 피해자를 그 자리에서 부활
            taskModule.runBukkitTaskLater(() -> {
                victim.spawnAt(victim.getLocation());
            }, 20L);

            messageModule.sendPlayerC(attacker, "<bold><red>잘못된 꼬리를 잡아 탈락되었습니다! " + shouldCatch.name() + " 꼬리를 잡아야 합니다.</red>");
            messageModule.broadcastMessageC("<bold><red>" + attacker.getName() + "님이 잘못된 꼬리를 잡아 탈락했습니다!</red>");
        }
    }

    public void checkAttackSlaveToMaster(EntityDamageByEntityEvent event, Player attacker, Player victim) {
        // 모든 노예 플레이어는 다른 플레이어를 공격할 수 없음
        PlayerInfo attackerInfo = GameData.getPlayerInfo(attacker);
        if (attackerInfo != null && attackerInfo.getMaster() != null) {
            event.setCancelled(true);
            messageModule.sendPlayerC(attacker, "<red>노예는 다른 플레이어를 공격할 수 없습니다!</red>");
            return;
        }

        // 기존 로직 - 노예가 주인을 공격하는 경우
        PlayerInfo victimInfo = GameData.getPlayerInfo(victim);
        if (victimInfo != null && victimInfo.getSlaves().contains(attacker)) {
            event.setCancelled(true);
        }
    }
}