package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;
import org.foodust.catchTheTail.Module.BaseModule.TaskModule;

import java.util.List;

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

    public boolean checkTailCatch(Player attacker, Player victim) {
        ItemStack attackerColorWool = attacker.getInventory().getItem(0);
        ItemStack victimColorWool = victim.getInventory().getItem(0);

        if (attackerColorWool == null || victimColorWool == null) return false;

        // 이미 잡은 꼬리가 있는지 확인
        PlayerInfo attackerInfo = GameData.gamePlayers.get(attacker);
        List<Player> attackerSlaves = attackerInfo.getSlaves();

        // 잡아야 하는 다음 꼬리 색 결정
        Material shouldCatch;

        if (attackerSlaves.isEmpty()) {
            // 첫 번째 꼬리를 잡는 경우
            shouldCatch = tailModule.getNextColor(attackerColorWool.getType());
        } else {
            // 이미 꼬리가 있는 경우, 마지막 꼬리의 다음 색깔을 확인
            Player lastSlave = attackerSlaves.getLast();
            ItemStack lastSlaveColorWool = lastSlave.getInventory().getItem(0);

            if (lastSlaveColorWool != null) {
                shouldCatch = tailModule.getNextColor(lastSlaveColorWool.getType());
            } else {
                // 마지막 꼬리가 색깔을 가지고 있지 않은 경우(예외 상황)
                shouldCatch = tailModule.getNextColor(attackerColorWool.getType());
            }
        }

        if (victimColorWool.getType() == shouldCatch) {
            // 성공적인 꼬리 잡기
            playerModule.bindPlayers(attacker, victim);
            messageModule.sendPlayerC(attacker, "<green>성공적으로 " + victimColorWool.getType().name() + " 꼬리를 잡았습니다!</green>");
            return true;
        } else {
            // 잘못된 꼬리 잡기 - 수정된 부분
            // 공격자를 탈락시키고 크기를 작게 만든다
            attackerInfo.setEliminated(true);

            try {
                attacker.registerAttribute(Attribute.SCALE);
                AttributeInstance attribute = attacker.getAttribute(Attribute.SCALE);
                if (attribute != null) {
                    attribute.setBaseValue(0.5); // 크기를 더 작게 설정
                }
            } catch (Exception ignore) {
            }

            // 피해자를 그 자리에서 부활
            taskModule.runBukkitTaskLater(() -> {
                victim.spawnAt(victim.getLocation());
                victim.setHealth(victim.getMaxHealth());
            }, 1L);

            messageModule.sendPlayerC(attacker, "<red>잘못된 꼬리를 잡아 탈락되었습니다! " + shouldCatch.name() + " 꼬리를 잡아야 합니다.</red>");
            messageModule.broadcastMessageC("<red>" + attacker.getName() + "님이 잘못된 꼬리를 잡아 탈락했습니다!</red>");
            return false;
        }
    }

    public void checkAttackSlaveToMaster(EntityDamageByEntityEvent event, Player attacker, Player victim) {
        // 모든 노예 플레이어는 다른 플레이어를 공격할 수 없음
        PlayerInfo attackerInfo = GameData.gamePlayers.get(attacker);
        if (attackerInfo != null && attackerInfo.getMaster() != null) {
            event.setCancelled(true);
            messageModule.sendPlayerC(attacker, "<red>노예는 다른 플레이어를 공격할 수 없습니다!</red>");
            return;
        }

        // 기존 로직 - 노예가 주인을 공격하는 경우
        PlayerInfo victimInfo = GameData.gamePlayers.get(victim);
        if (victimInfo != null && victimInfo.getSlaves().contains(attacker)) {
            event.setCancelled(true);
        }
    }
}