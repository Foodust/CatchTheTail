package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.Info.PlayerInfo;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;
import org.foodust.catchTheTail.Module.BaseModule.TaskModule;

import java.util.List;

public class GameModule {
    private final CatchTheTail plugin;
    private final PlayerModule playerModule;
    private final TailModule tailModule;
    private final MessageModule messageModule;
    private final TaskModule taskModule;

    public GameModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.playerModule = new PlayerModule(plugin);
        this.tailModule = new TailModule(plugin);
        this.messageModule = new MessageModule(plugin);
        this.taskModule = new TaskModule(plugin);
    }

    // Method to initialize player colors when game starts
    public void initializePlayerColors() {
        GameData.gamePlayers.values().forEach(playerInfo -> {
            Player player = playerInfo.getPlayer();
            if (player != null && player.isOnline()) {
                ItemStack woolItem = player.getInventory().getItem(9);
                if (woolItem != null && isWoolMaterial(woolItem.getType())) {
                    playerInfo.setRealWool(woolItem.getType());
                    messageModule.sendPlayerC(player, "<green>색깔이 " + getWoolColorInKorean(woolItem.getType()) + "로 등록되었습니다!</green>");
                    // 디버그 로그 추가
                    plugin.getLog().info(player.getName() + "의 색상이 " + woolItem.getType().name() + "로 등록됨");
                } else {
                    messageModule.sendPlayerC(player, "<red>색깔이 없습니다.</red>");
                    plugin.getLog().warning(player.getName() + "의 색상 등록 실패 - 아이템 없음");
                }
            }
        });
    }

    private boolean isWoolMaterial(Material material) {
        for (Material color : GameData.COLORS) {
            if (material == color) {
                return true;
            }
        }
        return false;
    }

    public void checkTailCatch(Player attacker, Player victim) {
        // Check if victim is already eliminated
        PlayerInfo victimInfo = GameData.getPlayerInfo(victim);

        // If victim is already eliminated, do nothing
        if (victimInfo.isEliminated()) {
            messageModule.sendPlayerC(attacker, "<red>이미 탈락한 플레이어입니다!</red>");
            return;
        }

        // 이미 잡은 꼬리가 있는지 확인
        PlayerInfo attackerInfo = GameData.getPlayerInfo(attacker);

        Material attackerColorWool = attackerInfo.getRealWool();

        // 디버깅 로그 추가
        plugin.getLog().info("공격자: " + attacker.getName() + ", 색상: " + attackerColorWool);
        plugin.getLog().info("피해자: " + victim.getName() + ", 색상: " + victimInfo.getRealWool());

        // 잡아야 하는 다음 꼬리 색 결정
        Material shouldCatch;

        shouldCatch = tailModule.getNextColor(attackerColorWool);

        // 디버깅 로그 추가
        plugin.getLog().info("잡아야 하는 색상: " + shouldCatch);

        if (victimInfo.getRealWool() == shouldCatch) {
            // 성공적인 꼬리 잡기
            playerModule.bindPlayers(attacker, victim);
            messageModule.sendPlayerC(attacker, "<green>성공적으로 꼬리를 잡았습니다!</green>");
            Bukkit.dispatchCommand(victim, "scale set 0.5");
            Bukkit.dispatchCommand(victim, "scale set pehkui:motion 1.5");
            Bukkit.dispatchCommand(victim, "scale set pehkui:jump_height 1.2");
            Bukkit.dispatchCommand(victim, "scale persist set true");

            GameData.eliminateColors.add(victimInfo.getRealWool());
        } else {
            // 잘못된 꼬리 잡기 - 수정된 부분
            attackerInfo.setEliminated(true);
            playerModule.bindPlayers(victim, attacker);
            Bukkit.dispatchCommand(attacker, "scale set 0.5");
            Bukkit.dispatchCommand(attacker, "scale set pehkui:motion 1.5");
            Bukkit.dispatchCommand(attacker, "scale set pehkui:jump_height 1.2");
            Bukkit.dispatchCommand(attacker, "scale persist set true");
            messageModule.sendPlayerC(attacker, "<bold><red>잘못된 꼬리를 잡아 탈락되었습니다! " + getWoolColorInKorean(shouldCatch) + " 꼬리를 잡아야 합니다.</red>");
            messageModule.broadcastMessageC("<bold><red>" + attacker.getName() + "님이 잘못된 꼬리를 잡아 탈락했습니다!</red>");

            GameData.eliminateColors.add(attackerInfo.getRealWool());
        }
    }

    public void checkAttackSlaveToMaster(EntityDamageByEntityEvent event, Player attacker, Player victim) {
        // 모든 노예 플레이어는 다른 플레이어를 공격할 수 없음
        PlayerInfo attackerInfo = GameData.getPlayerInfo(attacker);
        if (attackerInfo != null && attackerInfo.getMaster() != null) {
            event.setCancelled(true);
            messageModule.sendPlayerC(attacker, "<red>노예는 다른 플레이어를 공격할 수 없습니다!</red>");
            return;
        }

        // 기존 로직 - 노예가 주인을 공격하는 경우
        PlayerInfo victimInfo = GameData.getPlayerInfo(victim);
        if (victimInfo != null && victimInfo.getSlaves().contains(attacker)) {
            event.setCancelled(true);
        }
    }

    public String getWoolColorInKorean(Material woolMaterial) {
        if (woolMaterial == null) return "알 수 없음";

        return switch (woolMaterial) {
            case RED_WOOL -> "빨간색";
            case ORANGE_WOOL -> "주황색";
            case YELLOW_WOOL -> "노란색";
            case GREEN_WOOL -> "초록색";
            case LIGHT_BLUE_WOOL -> "하늘색";
            case PURPLE_WOOL -> "보라색";
            case WHITE_WOOL -> "하얀색";
            default -> "알 수 없음";
        };
    }
}