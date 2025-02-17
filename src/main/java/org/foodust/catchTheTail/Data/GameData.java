package org.foodust.catchTheTail.Data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Data.Info.TailInfo;

import java.util.*;


public class GameData {
    public static Map<Player, PlayerInfo> gamePlayers = new HashMap<>();
    public static TailInfo tailInfo;
    public static Map<Integer, List<ItemStack>> baseItems = new HashMap<>(); // 플레이어 번호별 기본 아이템
    public static boolean isGameRunning = false;

    public static void release(){
        isGameRunning = false;
        gamePlayers.clear();
        baseItems.clear();
    }

}

