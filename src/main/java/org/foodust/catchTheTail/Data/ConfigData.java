package org.foodust.catchTheTail.Data;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.foodust.catchTheTail.CatchTheTail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigData {
    public static Map<String, String> messages = new HashMap<>();
    public static Map<String, SoundInfo> sounds = new HashMap<>();

    public static void loadConfig(CatchTheTail plugin) {
        File configFile = new File(plugin.getDataFolder(), "message.yml");
        if (!configFile.exists()) {
            // 파일이 존재하지 않으면 기본 설정을 생성
            plugin.saveResource("message.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Load messages
        if (config.contains("settings.messages")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("settings.messages")).getKeys(false)) {
                messages.put(key, config.getString("settings.messages." + key));
            }
        }

        // Load sounds
        if (config.contains("settings.sounds")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("settings.sounds")).getKeys(false)) {
                String soundName = config.getString("settings.sounds." + key + ".sound", Sound.ENTITY_EXPERIENCE_ORB_PICKUP.toString());
                float volume = (float) config.getDouble("settings.sounds." + key + ".volume", 1.0);
                float pitch = (float) config.getDouble("settings.sounds." + key + ".pitch", 1.0);
                Sound sound;
                try {
                    sound = Registry.SOUNDS.get(Objects.requireNonNull(NamespacedKey.fromString(soundName)));
                } catch (Exception ignore) {
                    sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
                }
                if (sound != null) {
                    sounds.put(key, new SoundInfo(sound, volume, pitch));
                }
            }
        }
    }

    public static String getMessage(String key) {
        return messages.getOrDefault(key, "");
    }

    public static String getMessage(String key, Map<String, String> replacements) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    public static SoundInfo getSound(String key) {
        return sounds.getOrDefault(key, null);
    }

    public static void clear() {
        messages.clear();
        sounds.clear();
    }

    public record SoundInfo(Sound sound, float volume, float pitch) {
    }
}