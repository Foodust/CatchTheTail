package org.foodust.catchTheTail.Data;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameData {
    public static Map<Player, PlayerInfo> gamePlayers = new HashMap<>();
    public static Map<Integer, List<ItemStack>> baseItems = new HashMap<>(); // 플레이어 번호별 기본 아이템
    public static boolean isGameRunning = false;

    public static void release() {
        isGameRunning = false;
        gamePlayers.clear();
        baseItems.clear();

        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                player.registerAttribute(Attribute.SCALE);
                AttributeInstance attribute = player.getAttribute(Attribute.SCALE);
                if (attribute != null) {
                    attribute.setBaseValue(1);
                }
            } catch (Exception ignore) {
            }
        });
    }

}

