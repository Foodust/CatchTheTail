package org.foodust.catchTheTail.Data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameData {
    // Change to use UUID as key instead of Player object
    public static Map<UUID, PlayerInfo> gamePlayers = new HashMap<>();
    public static Map<Integer, List<ItemStack>> baseItems = new HashMap<>(); // 플레이어 번호별 기본 아이템
    public static boolean isGameRunning = false;

    public static final Material[] COLORS = {
            Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
            Material.GREEN_WOOL, Material.LIGHT_BLUE_WOOL, Material.PURPLE_WOOL, Material.WHITE_WOOL,
    };

    public static List<Material> activeColors = new ArrayList<>();
    public static List<Material> eliminateColors = new ArrayList<>();

    public static void release() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Bukkit.dispatchCommand(player, "scale reset");
        });

        isGameRunning = false;
        gamePlayers.clear();
        baseItems.clear();
        activeColors.clear();
        eliminateColors.clear();
    }

    // Helper method to find PlayerInfo by Player object
    public static PlayerInfo getPlayerInfo(Player player) {
        if (player == null) return null;
        return gamePlayers.get(player.getUniqueId());
    }
}