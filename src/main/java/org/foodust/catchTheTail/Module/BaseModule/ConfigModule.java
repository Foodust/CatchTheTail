package org.foodust.catchTheTail.Module.BaseModule;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.foodust.catchTheTail.CatchTheTail;
import org.foodust.catchTheTail.Data.AnimateData;
import org.foodust.catchTheTail.Data.GameData;
import org.foodust.catchTheTail.Data.TaskData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigModule {

    private final ItemModule itemModule;
    private final ItemSerialize itemSerialize;
    private final CatchTheTail plugin;

    public ConfigModule(CatchTheTail plugin) {
        this.plugin = plugin;
        this.itemModule = new ItemModule();
        this.itemSerialize = new ItemSerialize();
    }

    public FileConfiguration getConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            // 파일이 존재하지 않으면 기본 설정을 생성
            plugin.saveResource(fileName, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig(FileConfiguration config, String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLog().info(e.getMessage());
        }
    }

    public void initialize() {
        release();
    }

    public void release() {
        GameData.release();
        TaskData.release();
        AnimateData.release();
    }

    public Boolean setBaseItem(Player player, String index) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == Material.AIR) return false;
        String serialized = itemSerialize.serializeItem(itemInMainHand);
        FileConfiguration config = getConfig("baseItem.yml");
        List<String> keys = Objects.requireNonNull(config.getConfigurationSection(index))
                .getKeys(false)
                .stream()
                .toList();
        String lastKey = keys.isEmpty() ? "0" : keys.get(keys.size() - 1);
        int i = Integer.parseInt(lastKey) + 1;
        config.set(index + "." + i, serialized);
        saveConfig(config, "baseItem.yml");
        return true;
    }

    public void getBaseItem(Player player, String index) {
        FileConfiguration config = getConfig("baseItem.yml");
        try {
            for (String key : Objects.requireNonNull(config.getConfigurationSection(index)).getKeys(false)) {
                String base = config.getString(index + "." + key);
                ItemStack itemStack = itemSerialize.deserializeItem(base);
                if (itemStack == null) continue;
                player.getInventory().addItem(itemStack);
            }
        } catch (Exception ignore) {
        }
    }
}
