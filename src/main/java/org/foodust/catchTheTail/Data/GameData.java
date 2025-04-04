package org.foodust.catchTheTail.Data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameData {
    public static Map<Player, PlayerInfo> gamePlayers = new HashMap<>();
    public static Map<Integer, List<ItemStack>> baseItems = new HashMap<>(); // 플레이어 번호별 기본 아이템
    public static boolean isGameRunning = false;

    public static final Material[] COLORS = {
            Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
            Material.GREEN_WOOL, Material.LIGHT_BLUE_WOOL, Material.PURPLE_WOOL, Material.WHITE_WOOL,
    };

    public static List<Material> activeColors = new ArrayList<>();

    public static void release() {

        Bukkit.getOnlinePlayers().forEach(player -> {
            Bukkit.dispatchCommand(player, "scale reset");
        });

        isGameRunning = false;
        gamePlayers.clear();
        baseItems.clear();
        activeColors.clear();

    }

}

