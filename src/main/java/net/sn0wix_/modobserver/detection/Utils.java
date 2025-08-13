package net.sn0wix_.modobserver.detection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.sn0wix_.modobserver.ModObserver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

public class Utils {
    public static String getModsJson() {
        return toJson(getModsList());
    }

    private static LinkedHashMap<String, Object> toJsonMap(LinkedHashMap<ModEntry, Object> map) {
        LinkedHashMap<String, Object> jsonReadyMap = new LinkedHashMap<>();

        for (Map.Entry<ModEntry, Object> mapEntry : map.entrySet()) {
            if (mapEntry.getValue() instanceof LinkedHashMap<?, ?> linkedHashMap && !linkedHashMap.isEmpty()) {
                jsonReadyMap.put(
                        mapEntry.getKey().getId() + " " + mapEntry.getKey().getHash(),
                        toJsonMap((LinkedHashMap<ModEntry, Object>) linkedHashMap)
                );
            } else {
                jsonReadyMap.put(mapEntry.getKey().getId(), mapEntry.getKey().getHash());
            }
        }

        return jsonReadyMap;
    }

    public static String toJson(LinkedHashMap<ModEntry, Object> map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJsonMap(map));
    }

    public static LinkedHashMap<ModEntry, Object> getModsList() {
        LinkedHashMap<ModEntry, Object> mods = new LinkedHashMap<>();

        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            if (modContainer.getContainingMod().isEmpty()) {
                mods.put(new ModEntry(modContainer, Utils.getHash(modContainer)), Utils.getChildren(modContainer));
            }
        });

        return mods;
    }


    private static LinkedHashMap<ModEntry, Object> getChildren(ModContainer entry) {
        LinkedHashMap<ModEntry, Object> result = new LinkedHashMap<>();

        if (entry.getContainedMods().isEmpty()) return result;

        entry.getContainedMods().forEach(container -> {
            if (!container.getContainedMods().isEmpty()) {
                result.put(new ModEntry(container), getChildren(container));
            } else {
                result.put(new ModEntry(container), List.of());
            }
        });

        return result;
    }

    public static String getHash(ModContainer container) {
        if (container.getOrigin().getPaths().isEmpty() || container.getOrigin().getPaths().getFirst().toFile().isDirectory())
            return "";

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream fis = new FileInputStream(container.getOrigin().getPaths().getFirst().toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            ModObserver.LOGGER.error("Can not create hash of " + container.getMetadata().getId());
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static boolean isFabricApi(String modid) {
        if (List.of("mixinextras", "minecraft", "fabric-api", "fabric-api-base", "fabricloader", "java", "fabric-renderer-indigo").contains(modid)) {
            return true;
        }

        //fabric-[something]-v[number]
        try {
            Integer.parseInt(String.valueOf(modid.charAt(modid.length() - 1)));
        } catch (NumberFormatException ignored) {
            //last char is not a number
            return false;
        }

        return modid.startsWith("fabric-") && modid.lastIndexOf("-v") == modid.length() - 3;
    }
}
