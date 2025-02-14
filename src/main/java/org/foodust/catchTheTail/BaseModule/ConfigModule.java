package org.foodust.catchTheTail.BaseModule;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.foodust.catchTheTail.CatchTheTail;

import java.io.File;
import java.io.IOException;

public class ConfigModule {
    private final CatchTheTail plugin;

    public ConfigModule(CatchTheTail plugin) {
        this.plugin = plugin;
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
            Bukkit.getLogger().info(e.getMessage());
        }
    }

    public void initialize() {
    }

    public void release() {

    }
}
