package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.TailInfo;

import java.util.Arrays;

public class TailModule {

    private final CatchTheTail plugin;

    public TailModule(CatchTheTail plugin) {
        this.plugin = plugin;
    }

    public static void initializeColors() {
        TailInfo.activeColors.addAll(Arrays.asList(TailInfo.COLORS).subList(0, Math.min(GameData.gamePlayers.size(), TailInfo.COLORS.length)));
    }

    public static Material getNextColor(Material currentColor) {
        int index = TailInfo.activeColors.indexOf(currentColor);
        return TailInfo.activeColors.get((index + 1) % TailInfo.activeColors.size());
    }

}
