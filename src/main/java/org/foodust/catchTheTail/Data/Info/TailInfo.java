package org.foodust.catchTheTail.Data.Info;

import lombok.Getter;
import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TailInfo {
    public static final DyeColor[] COLORS = {
            DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.LIGHT_BLUE,
            DyeColor.PURPLE, DyeColor.WHITE
    };

    public static List<DyeColor> activeColors = new ArrayList<>();


}
