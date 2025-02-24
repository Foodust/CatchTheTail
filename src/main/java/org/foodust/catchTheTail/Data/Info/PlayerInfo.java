package org.foodust.catchTheTail.Data.Info;

import lombok.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfo {
    @Builder.Default
    private Player player = null;
    @Builder.Default
    private Player master = null;
    @Builder.Default
    private List<Player> slaves = new ArrayList<>();
    @Builder.Default
    private boolean eliminated = false;      // 탈락 여부
    @Builder.Default
    private int index = -1;

}
