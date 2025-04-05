package org.foodust.catchTheTail.Module.GameModule;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

import java.util.Arrays;

public class TailModule {

    private final CatchTheTail plugin;
    public TailModule(CatchTheTail plugin) {
        this.plugin = plugin;
    }

    public void initializeColors() {
        GameData.activeColors.addAll(Arrays.asList(GameData.COLORS).subList(0, Math.min(GameData.gamePlayers.size(), GameData.COLORS.length)));
    }

    public Material getNextColor(Material currentColor) {
        int index = GameData.activeColors.indexOf(currentColor);
        return GameData.activeColors.get((index + 1) % GameData.activeColors.size());
    }

}
