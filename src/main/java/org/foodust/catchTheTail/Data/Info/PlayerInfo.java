package org.foodust.catchTheTail.Data.Info;

import lombok.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    // Add UUID references to maintain associations when players disconnect
    @Builder.Default
    private UUID masterUUID = null;
    @Builder.Default
    private List<Player> slaves = new ArrayList<>();
    @Builder.Default
    private List<UUID> slavesUUIDs = new ArrayList<>();
    @Builder.Default
    private boolean eliminated = false;
    // Add disconnected flag to track player state
    @Builder.Default
    private boolean disconnected = false;
    @Builder.Default
    private String lastKnownName = "";

    private Material realWool;

    @Builder.Default
    private String index = "-1";
}