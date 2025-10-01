package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.nio.file.Path;
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
                    if (!builder.bl && !isFabricApi(modid) && !modid.equals("completedshieldfix"/*There is no way of detecting it otherwise*/)) {
                        throw new TamperingErrorScreen.TamperingException(modid);
                    }
                }

                set.add(modid);
            }
        }

        return set;
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
