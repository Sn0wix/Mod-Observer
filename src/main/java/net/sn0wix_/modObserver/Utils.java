package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.sn0wix_.modObserver.detection.EntrypointBuilder;
import net.sn0wix_.modObserver.detection.ModEntry;
import net.sn0wix_.modObserver.detection.tampering.TamperingException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;

public class Utils {

    @Deprecated
    public static Set<String> getMods() throws TamperingException {
        HashMap<String, EntrypointBuilder> containers = new HashMap<>(FabricLoader.getInstance().getAllMods().size());

        FabricLoaderImpl.INSTANCE.getModsInternal().forEach(modContainer -> {
            EntrypointBuilder builder = new EntrypointBuilder(modContainer).addIconPath(modContainer.getMetadata().getIconPath(128)).addName(modContainer.getMetadata().getName());

            try {
                modContainer.getMetadata().getMixinConfigs(EnvType.CLIENT).forEach(builder::addMixin);
                modContainer.getMetadata().getMixinConfigs(EnvType.SERVER).forEach(builder::addMixin);
                modContainer.getMetadata().getCustomValue("modmenu").getAsObject().get("badges").getAsArray().forEach(builder::checkLibrary);
            } catch (Exception ignored) {
            }

            containers.put(modContainer.getMetadata().getId(), builder);
        });

        Class<?>[] classes = new Class<?>[]{DedicatedServerModInitializer.class, ClientModInitializer.class, ModInitializer.class};
        String[] strings = new String[]{"server", "client", "main"};

        for (int i = 0; i < strings.length; i++) {
            FabricLoader.getInstance().getEntrypointContainers(strings[i], classes[i]).forEach(modContainer -> {
                EntrypointBuilder builder = containers.get(modContainer.getProvider().getMetadata().getId()) == null ? new EntrypointBuilder(modContainer.getProvider()) : containers.get(modContainer.getProvider().getMetadata().getId());

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

            if (!builder.getModids().isEmpty()) {
                set.add(builder.getValidId());
            } else if (!builder.getMixins().isEmpty()) {
                if (!(builder.getIcon().contains(modid) || builder.hasMixinsWithId(modid)) && !(builder.getName().toLowerCase().replace(" ", "").equals(modid) || builder.getName().toLowerCase().replace(" ", "-").equals(modid) || builder.getName().toLowerCase().replace(" ", "_").equals(modid))) {
                    if (!builder.hasLibraryBadge()) {
                        throw new TamperingException(List.of(builder.getContainer()));
                    }
                }

                set.add(modid);
            }
        }

        return set;
    }

    public static void checkTampering() throws TamperingException {
        HashMap<String, EntrypointBuilder> containers = new HashMap<>(FabricLoader.getInstance().getAllMods().size());

        FabricLoaderImpl.INSTANCE.getModsInternal().forEach(modContainer -> {
            EntrypointBuilder builder = new EntrypointBuilder(modContainer).addIconPath(modContainer.getMetadata().getIconPath(128)).addName(modContainer.getMetadata().getName());

            try {
                modContainer.getMetadata().getMixinConfigs(EnvType.CLIENT).forEach(builder::addMixin);
                modContainer.getMetadata().getMixinConfigs(EnvType.SERVER).forEach(builder::addMixin);
                modContainer.getMetadata().getCustomValue("modmenu").getAsObject().get("badges").getAsArray().forEach(builder::checkLibrary);
            } catch (Exception ignored) {
            }

            containers.put(modContainer.getMetadata().getId(), builder);
        });

        Class<?>[] classes = new Class<?>[]{DedicatedServerModInitializer.class, ClientModInitializer.class, ModInitializer.class};
        String[] strings = new String[]{"server", "client", "main"};

        for (int i = 0; i < strings.length; i++) {
            FabricLoader.getInstance().getEntrypointContainers(strings[i], classes[i]).forEach(modContainer -> {
                EntrypointBuilder builder = containers.get(modContainer.getProvider().getMetadata().getId()) == null ? new EntrypointBuilder(modContainer.getProvider()) : containers.get(modContainer.getProvider().getMetadata().getId());

                try {
                    builder.addId(Path.of(modContainer.getDefinition().split("::")[0].replace('.', '/') + ".class"));
                } catch (Exception ignored) {
                }
            });
        }

        ArrayList<ModContainer> tamperedMods = new ArrayList<>(0);

        for (Map.Entry<String, EntrypointBuilder> entry : containers.entrySet()) {
            EntrypointBuilder builder = entry.getValue();
            String modid = entry.getKey();

            //TODO improve detection checks
            if (!builder.getMixins().isEmpty()) {
                if (!(builder.getIcon().contains(modid) || builder.hasMixinsWithId(modid)) && !(builder.getName().toLowerCase().replace(" ", "").equals(modid) || builder.getName().toLowerCase().replace(" ", "-").equals(modid) || builder.getName().toLowerCase().replace(" ", "_").equals(modid))) {
                    if (!builder.hasLibraryBadge()) {
                        tamperedMods.add(builder.getContainer());
                    }
                }
            }
        }

        if (!tamperedMods.isEmpty()) {
            throw new TamperingException(tamperedMods);
        }
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

    public static String getHash(ModContainer container) {
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
            ModObserver.LOGGER.error("Can not create hash of " + container.getMetadata().getId());
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
