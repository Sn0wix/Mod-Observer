package net.sn0wix_.modObserverPlugin;

import com.google.common.collect.ImmutableList;
import net.sn0wix_.modObserverPlugin.modChecking.ModEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Util {
    public static List<String> getOnlinePlayers() {
        ImmutableList<? extends Player> players = ImmutableList.copyOf(ModObserver.getInstance().getServer().getOnlinePlayers());
        List<String> names = new ArrayList<>(players.size());
        ModObserver.getInstance().getServer().getOnlinePlayers().forEach(player -> names.add(player.getName()));
        return names;
    }

    public static boolean containsEntry(Map<ModEntry, Object> map, ModEntry entry) {
        AtomicBoolean bl = new AtomicBoolean(false);
        map.keySet().forEach(key -> {
            if (key.equals(entry)) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static ModEntry getEntry(Map<ModEntry, Object> map, ModEntry entry) {
        AtomicReference<ModEntry> returnValue = new AtomicReference<>();
        map.keySet().forEach(key -> {
            if (key.equals(entry)) {
                returnValue.set(key);
            }
        });

        return returnValue.get();
    }

    public static Object getValue(Map<ModEntry, Object> map, ModEntry entry) {
        AtomicReference<Object> returnValue = new AtomicReference<>();
        map.forEach((key, value) -> {
            if (key.equals(entry)) {
                returnValue.set(value);
            }
        });

        return returnValue.get();
    }
}
