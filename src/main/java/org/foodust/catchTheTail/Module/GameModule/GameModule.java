package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;

public class GameModule {
    private final CatchTheTail plugin;
    private final PlayerModule playerModule;

    public GameModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.playerModule = new PlayerModule(plugin);
    }

    public void checkTailCatch(Player attacker, Player victim) {
        ItemStack attackerColorWool = attacker.getInventory().getItem(0);
        ItemStack victimColorWool = victim.getInventory().getItem(0);
        if (attackerColorWool == null || victimColorWool == null) return;
        Material shouldCatch = TailModule.getNextColor(attackerColorWool.getType());

        if (victimColorWool.getType() == shouldCatch) {
            // 성공적인 꼬리 잡기
            playerModule.bindPlayers(attacker, victim);
        } else {
            // 잘못된 꼬리 잡기
            playerModule.bindPlayers(victim, attacker);
        }
    }

    public void checkAttackSlaveToMaster(EntityDamageByEntityEvent event, Player attacker, Player victim) {
        PlayerInfo victimInfo = GameData.gamePlayers.get(victim);
        PlayerInfo attackerInfo = GameData.gamePlayers.get(attacker);

        if (victimInfo.getSlaves().contains(attacker)) {
            event.setCancelled(true);
        }
    }
}
