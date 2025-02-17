package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
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
        PlayerInfo attackerInfo = GameData.gamePlayers.get(attacker);
        PlayerInfo victimInfo = GameData.gamePlayers.get(victim);

        if (attackerInfo == null || victimInfo == null) return;

        DyeColor shouldCatch =  TailModule.getNextColor(attackerInfo.getTailColor());

        if (victimInfo.getTailColor() == shouldCatch) {
            // 성공적인 꼬리 잡기
            playerModule.bindPlayers(attacker, victim);
        } else {
            // 잘못된 꼬리 잡기
            attackerInfo.setEliminated(true);
            playerModule.bindPlayers(victim, attacker);
        }
    }
}
