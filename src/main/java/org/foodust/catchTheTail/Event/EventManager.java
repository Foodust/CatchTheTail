package org.foodust.catchTheTail.Event;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.foodust.catchTheTail.CatchTheTail;

// Event Listener 들을 등록하기 위해 만들어 졌습니다
public class EventManager {
    public EventManager(Server server, Plugin plugin) {
        server.getPluginManager().registerEvents(new PlayerEvent((CatchTheTail) plugin), plugin);
    }
}
