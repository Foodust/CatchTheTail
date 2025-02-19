package org.foodust.catchTheTail.Data.Info;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerInfo {
    private Player player;
    private Player master;           // 노예가 된 경우 주인
    private List<Player> slaves;     // 자신의 노예들
    private boolean eliminated;      // 탈락 여부

    public PlayerInfo(Player player) {
        this.player = player;
        this.slaves = new ArrayList<>();
        this.eliminated = false;
    }
}
