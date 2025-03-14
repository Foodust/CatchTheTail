package org.foodust.catchTheTail.Module.GameModule;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.ConfigData;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Module.BaseModule.ConfigModule;
import org.foodust.catchTheTail.Module.BaseModule.MessageModule;

import java.util.HashMap;
import java.util.Map;

public class GameEndModule {
    private final CatchTheTail plugin;
    private final MessageModule messageModule;
    private final ConfigModule configModule;

    public GameEndModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.messageModule = new MessageModule(plugin);
        this.configModule = new ConfigModule(plugin);
    }

    /**
     * Checks if the game should end and handles the ending if needed
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean checkGameEnd() {
        if (!GameData.isGameRunning)
            return false;

        // Count active masters (players who aren't eliminated and aren't slaves)
        long activeMasters = GameData.gamePlayers.entrySet().stream()
                .filter(entry -> !entry.getValue().isEliminated() && entry.getValue().getMaster() == null)
                .count();

        // If there's only one or zero active masters, end the game
        if (activeMasters <= 1) {
            // Find the winner (if there is one)
            Player winner = null;
            if (activeMasters == 1) {
                winner = GameData.gamePlayers.entrySet().stream()
                        .filter(entry -> !entry.getValue().isEliminated() && entry.getValue().getMaster() == null)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);
            }

            endGame(winner);
            return true;
        }

        return false;
    }

    /**
     * Ends the game and announces the winner
     *
     * @param winner The winning player, or null if there is no winner
     */
    public void endGame(Player winner) {
        if (!GameData.isGameRunning)
            return;

        if (winner != null) {
            // Announce winner
            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", winner.getName());
            String winMessage = ConfigData.getMessage("game_win", replacements);
            messageModule.broadcastMessageC(winMessage);

            // Play win sound for all players
            ConfigData.SoundInfo winSound = ConfigData.getSound("game_win");
            if (winSound != null) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.playSound(player.getLocation(), winSound.sound(), 1, 1);
                });
            }
        }

        // Reset the game
        configModule.initialize();
    }
}