package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.modChecking.ModEntry;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Util {
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
