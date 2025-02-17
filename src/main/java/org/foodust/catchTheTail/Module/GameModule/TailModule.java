package org.foodust.catchTheTail.Module.GameModule;

import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Data.Info.TailInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class TailModule {

    private final CatchTheTail plugin;

    public TailModule(CatchTheTail plugin) {
        this.plugin = plugin;
    }

    public static void initializeColors() {
        TailInfo.activeColors.addAll(Arrays.asList(TailInfo.COLORS).subList(0, Math.min(GameData.gamePlayers.size(), TailInfo.COLORS.length)));
    }

    public static DyeColor getNextColor(DyeColor currentColor) {
        int index = TailInfo.activeColors.indexOf(currentColor);
        return TailInfo.activeColors.get((index + 1) % TailInfo.activeColors.size());
    }

    public void giveTailWools(Player player) {
        PlayerInfo info = GameData.gamePlayers.get(player);
        if (info == null) return;

        // 플레이어의 현재 색상 인덱스 계산
        int playerIndex = new ArrayList<>(GameData.gamePlayers.keySet()).indexOf(player);
        DyeColor realTailColor = TailInfo.activeColors.get(playerIndex % TailInfo.activeColors.size());

        // 실제 꼬리 양털
        ItemStack realTail = createWool(realTailColor, "§r실제 꼬리");
        player.getInventory().setItem(0, realTail);

        // 가짜 꼬리 양털 (다음 색상 사용)
        DyeColor fakeTailColor = getNextColor(realTailColor);
        ItemStack fakeTail = createWool(fakeTailColor, "§r가짜 꼬리");
        player.getInventory().setItem(1, fakeTail);

        // PlayerInfo 업데이트
        info.setTailColor(realTailColor);
        info.setFakeTailColor(fakeTailColor);
    }

    private ItemStack createWool(DyeColor color, String displayName) {
        Material woolMaterial = Material.valueOf(color.name() + "_WOOL");
        ItemStack wool = new ItemStack(woolMaterial, 1);
        ItemMeta meta = wool.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName));
            wool.setItemMeta(meta);
        }
        return wool;
    }
}
