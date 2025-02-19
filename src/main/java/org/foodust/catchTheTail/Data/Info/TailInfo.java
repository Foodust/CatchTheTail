package org.foodust.catchTheTail.Data.Info;

import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TailInfo {
    public static final Material[] COLORS = {
            Material.RED_WOOL,Material.ORANGE_WOOL,Material.YELLOW_WOOL,
            Material.GREEN_WOOL,Material.LIGHT_BLUE_WOOL,Material.PURPLE_WOOL, Material.WHITE_WOOL,
    };

    public static List<Material> activeColors = new ArrayList<>();


}
