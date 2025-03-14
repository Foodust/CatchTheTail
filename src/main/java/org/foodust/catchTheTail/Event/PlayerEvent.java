package org.foodust.catchTheTail.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Module.GameModule.GameEndModule;
import org.foodust.catchTheTail.Module.GameModule.GameModule;
import org.foodust.catchTheTail.Module.GameModule.PlayerModule;

public class PlayerEvent implements Listener {

    private final GameModule gameModule;
    private final GameEndModule gameEndModule;
    public PlayerEvent(CatchTheTail plugin) {
        this.gameModule = new GameModule(plugin);
        this.gameEndModule = new GameEndModule(plugin);
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        if (!GameData.isGameRunning) return;
        Player victim = event.getEntity();
        if (!(victim.getKiller() instanceof Player attacker)) return;
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
}
