package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.sn0wix_.modObserver.detection.ModEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;

public class Utils {
    public static Set<String> getMods() throws TamperingErrorScreen.TamperingException {
        HashMap<String, EntrypointBuilder> containers = new HashMap<>(FabricLoader.getInstance().getAllMods().size());

        FabricLoaderImpl.INSTANCE.getModsInternal().forEach(modContainer -> {
            EntrypointBuilder builder = new EntrypointBuilder().addIconPath(modContainer.getMetadata().getIconPath(128)).setName(modContainer.getMetadata().getName());

            try {
                modContainer.getMetadata().getMixinConfigs(EnvType.CLIENT).forEach(builder::addMixin);
                modContainer.getMetadata().getMixinConfigs(EnvType.SERVER).forEach(builder::addMixin);
                modContainer.getMetadata().getCustomValue("modmenu").getAsObject().get("badges").getAsArray().forEach(builder::setBl);
            } catch (Exception ignored) {
            }

            containers.put(modContainer.getMetadata().getId(), builder);
        });

        Class<?>[] classes = new Class<?>[]{DedicatedServerModInitializer.class, ClientModInitializer.class, ModInitializer.class};
        String[] strings = new String[]{"server", "client", "main"};

        for (int i = 0; i < strings.length; i++) {
            FabricLoader.getInstance().getEntrypointContainers(strings[i], classes[i]).forEach(modContainer -> {
                EntrypointBuilder builder = containers.get(modContainer.getProvider().getMetadata().getId()) == null ? new EntrypointBuilder() : containers.get(modContainer.getProvider().getMetadata().getId());

                try {
                    builder.addId(Path.of(modContainer.getDefinition().split("::")[0].replace('.', '/') + ".class"));
                } catch (Exception e) {
                    ModObserver.LOGGER.info("You can pretty much ignore this.");
                    e.printStackTrace();
                }
            });
        }

        Set<String> set = new LinkedHashSet<>(containers.keySet().size());

        for (Map.Entry<String, EntrypointBuilder> entry : containers.entrySet()) {
            EntrypointBuilder builder = entry.getValue();
            String modid = entry.getKey();

            if (!builder.modids.isEmpty()) {
                set.add(builder.getValidId());
            } else if (!builder.mixins.isEmpty()) {
                if (!(builder.icon.contains(modid) || builder.hasMixinsWithId(modid)) && !(builder.name.toLowerCase().replace(" ", "").equals(modid) || builder.name.toLowerCase().replace(" ", "-").equals(modid) || builder.name.toLowerCase().replace(" ", "_").equals(modid))) {
                    if (!builder.bl) {
                        throw new TamperingErrorScreen.TamperingException(modid);
                    }
                }

                set.add(modid);
            }
        }

        return set;
    }

    public static LinkedHashMap<ModEntry, Object> getChildren(ModContainer entry) {
        LinkedHashMap<ModEntry, Object> result = new LinkedHashMap<>();

        if (entry.getContainedMods().isEmpty()) return result;

        entry.getContainedMods().forEach(container -> {
            if (!container.getContainedMods().isEmpty()) {
                result.put(new ModEntry(container), new LinkedHashMap<>());
            } else {
                result.put(new ModEntry(container), List.of());
            }
        });

        return result;
    }

    public static String getSHA256(ModContainer container) {
        if (container.getOrigin().getPaths().isEmpty() || container.getOrigin().getPaths().getFirst().toFile().isDirectory()) return "";

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
            ModObserver.LOGGER.error("Can not hash mod file of mod " + container.getMetadata().getId());
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
}
