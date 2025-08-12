package net.sn0wix_.modObserverPlugin.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.sn0wix_.modObserverPlugin.ModObserver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;

public class JsonLoader {
    private static final Gson gson = new Gson();
    private static File whitelist;
    private static File blacklist;
    private static File required;

    private static Map<String, Object> whitelistMap;
    private static Map<String, Object> blacklistMap;
    private static Map<String, Object> requiredMap;


    public static void init() {
        whitelist = new File(ModObserver.getInstance().getDataFolder(), "whitelist.json");
        blacklist = new File(ModObserver.getInstance().getDataFolder(), "blacklist.json");
        required = new File(ModObserver.getInstance().getDataFolder(), "required_mods.json");

        try {
            if (!whitelist.exists()) {
                writeNewFile(whitelist);
            }

            if (!blacklist.exists()) {
                writeNewFile(blacklist);
            }

            if (!required.exists()) {
                writeNewFile(required);
            }

            initMaps();
        } catch (IOException e) {
            ModObserver.LOGGER.severe("Could not create/load json configuration files! Expect the plugin to crash.");
            ModObserver.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void initMaps() throws IOException {
        whitelistMap = loadJsonFromFile(whitelist);
        blacklistMap = loadJsonFromFile(blacklist);
        requiredMap = loadJsonFromFile(required);
    }

    private static void writeNewFile(File file) throws IOException {
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("{}");
        writer.close();
    }


    public static Map<String, Object> getWhitelistMap() {
        return whitelistMap;
    }

    public static Map<String, Object> getBlacklistMap() {
        return blacklistMap;
    }

    public static Map<String, Object> getRequiredMap() {
        return requiredMap;
    }

    private static Map<String, Object> loadJsonFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            try {
                return gson.fromJson(reader, type);
            }catch (JsonSyntaxException e) {
                ModObserver.LOGGER.severe("There is a syntax error in the file " + file.getName());
                throw new RuntimeException(e);
            }

        }
    }

    public static Map<String, Object> loadJsonFromString(String content) {
        return gson.fromJson(content, new TypeToken<Map<String, Object>>() {}.getType());
    }
}
