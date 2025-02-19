package org.foodust.catchTheTail.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Module.GameModule.GameModule;
import org.foodust.catchTheTail.Module.GameModule.PlayerModule;

public class PlayerEvent implements Listener {

    private final GameModule gameModule;

    public PlayerEvent(CatchTheTail plugin) {
        this.gameModule = new GameModule(plugin);
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (!(victim.getKiller() instanceof Player attacker)) return;
        gameModule.checkTailCatch(attacker, victim);
    }
}
