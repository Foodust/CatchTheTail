package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

import java.util.List;

public class GameModule {
    private final CatchTheTail plugin;
    private final PlayerModule playerModule;
    private final TailModule tailModule;
    private final MessageModule messageModule;
    public GameModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.playerModule = new PlayerModule(plugin);
        this.tailModule = new TailModule(plugin);
        this.messageModule = new MessageModule(plugin);
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
            // 잘못된 꼬리 잡기
            playerModule.bindPlayers(victim, attacker);
            messageModule.sendPlayerC(attacker, "<red>잘못된 꼬리를 잡았습니다! " + shouldCatch.name() + " 꼬리를 잡아야 합니다.</red>");
            return false;
        }
    }

    public void checkAttackSlaveToMaster(EntityDamageByEntityEvent event, Player attacker, Player victim) {
        PlayerInfo victimInfo = GameData.gamePlayers.get(victim);
        if (victimInfo != null && victimInfo.getSlaves().contains(attacker)) {
            event.setCancelled(true);
        }
    }
}
