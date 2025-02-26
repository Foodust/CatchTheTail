package org.foodust.catchTheTail.Module.GameModule;

import com.mojang.brigadier.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.TailInfo;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

import java.util.Arrays;

public class TailModule {

    private final CatchTheTail plugin;
    private final MessageModule messageModule;
    public TailModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.messageModule = new MessageModule(plugin);
    }

    public void initializeColors() {
        TailInfo.activeColors.addAll(Arrays.asList(TailInfo.COLORS).subList(0, Math.min(GameData.gamePlayers.size(), TailInfo.COLORS.length)));
    }

    public Material getNextColor(Material currentColor) {
        for (Material activeColor : TailInfo.activeColors) {
            Bukkit.broadcast(Component.text(activeColor.name()));
        }
        int index = TailInfo.activeColors.indexOf(currentColor);
        return TailInfo.activeColors.get((index + 1) % TailInfo.activeColors.size());
    }

}
