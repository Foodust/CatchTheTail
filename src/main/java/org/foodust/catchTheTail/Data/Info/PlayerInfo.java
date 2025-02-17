package org.foodust.catchTheTail.Data.Info;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.foodust.catchTheTail.Data.GameData;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerInfo {
    private Player player;
    private DyeColor tailColor;      // 실제 꼬리 색
    private DyeColor fakeTailColor;  // 가짜 꼬리 색
    private Player master;           // 노예가 된 경우 주인
    private List<Player> slaves;     // 자신의 노예들
    private boolean eliminated;      // 탈락 여부

    public PlayerInfo(Player player) {
        this.player = player;
        this.slaves = new ArrayList<>();
        this.eliminated = false;
    }
}
