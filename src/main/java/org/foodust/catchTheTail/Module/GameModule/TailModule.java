package org.foodust.catchTheTail.Module.GameModule;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

import java.util.Arrays;

public class TailModule {

    private final CatchTheTail plugin;
    public TailModule(CatchTheTail plugin) {
        this.plugin = plugin;
    }

    public void initializeColors() {
        GameData.activeColors.clear(); // 기존 리스트 초기화
        GameData.activeColors.addAll(Arrays.asList(GameData.COLORS).subList(0, Math.min(GameData.gamePlayers.size(), GameData.COLORS.length)));

        // 디버깅: 초기화된 색상 목록 출력
        plugin.getLog().info("초기화된 색상 목록: " + GameData.activeColors);
    }

    public Material getNextColor(Material currentColor) {
        int index = GameData.activeColors.indexOf(currentColor);

        // 색상이 activeColors에 없는 경우에 대한 처리 추가
        if (index == -1) {
            plugin.getLog().warning("색상을 찾을 수 없음: " + currentColor);
            return GameData.activeColors.get(0); // 첫 번째 색상 반환
        }

        Material nextColor = GameData.activeColors.get((index + 1) % GameData.activeColors.size());
        plugin.getLog().info("현재 색상: " + currentColor + ", 다음 색상: " + nextColor);
        return nextColor;
    }
}