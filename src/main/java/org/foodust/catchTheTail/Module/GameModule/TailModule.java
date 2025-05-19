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

        // 색상이 activeColors에 없는 경우에 대한 처리
        if (index == -1) {
            plugin.getLog().warning("색상을 찾을 수 없음: " + currentColor);
            // 첫 번째 활성 색상 반환
            for (Material color : GameData.activeColors) {
                if (!GameData.eliminateColors.contains(color)) {
                    return color;
                }
            }
            return GameData.activeColors.get(0); // 모든 색상이 탈락한 경우 기본값 반환
        }

        // 현재 색상의 다음 색상을 찾되, 탈락한 색상은 건너뜀
        int nextIndex = (index + 1) % GameData.activeColors.size();
        Material nextColor = GameData.activeColors.get(nextIndex);

        // 탈락한 색상을 건너뛰며 다음 색상 찾기
        while (GameData.eliminateColors.contains(nextColor)) {
            nextIndex = (nextIndex + 1) % GameData.activeColors.size();
            nextColor = GameData.activeColors.get(nextIndex);

            // 모든 색상이 탈락했을 경우 무한 루프 방지
            if (nextIndex == index) {
                plugin.getLog().warning("남은 색상이 없음");
                return currentColor; // 더 이상 잡을 색상이 없으면 현재 색상 반환
            }
        }

        plugin.getLog().info("현재 색상: " + currentColor + ", 다음 색상: " + nextColor +
                ", 탈락 색상: " + GameData.eliminateColors);
        return nextColor;
    }
}