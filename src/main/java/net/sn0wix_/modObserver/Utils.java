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
            EntrypointBuilder builder = null;

            if (modContainer.getContainingMod().isEmpty()) {
                builder = new EntrypointBuilder().addIconPath(modContainer.getMetadata().getIconPath(128)).setName(modContainer.getMetadata().getName());

                try {
                    modContainer.getMetadata().getMixinConfigs(EnvType.CLIENT).forEach(builder::addMixin);
                    modContainer.getMetadata().getMixinConfigs(EnvType.SERVER).forEach(builder::addMixin);
                    modContainer.getMetadata().getCustomValue("modmenu").getAsObject().get("badges").getAsArray().forEach(builder::checkLibraryBadge);
                } catch (Exception ignored) {
                }
            }

            containers.put(modContainer.getMetadata().getId(), builder);
        });

        Class<?>[] entryClasses = new Class<?>[]{DedicatedServerModInitializer.class, ClientModInitializer.class, ModInitializer.class};
        String[] entryPoints = new String[]{"server", "client", "main"};

        for (int i = 0; i < entryPoints.length; i++) {
            FabricLoader.getInstance().getEntrypointContainers(entryPoints[i], entryClasses[i]).forEach(modContainer -> {
                EntrypointBuilder builder = containers.get(modContainer.getProvider().getMetadata().getId());
                if (builder != null) {
                    try {
                        builder.addId(Path.of(modContainer.getDefinition().split("::")[0].replace('.', '/') + ".class"));
                    } catch (Exception e) {
                        ModObserver.LOGGER.info("You can pretty much ignore this.");
                        e.printStackTrace();
                    }
                }
            });
        }

        for (Map.Entry<String, EntrypointBuilder> entry : containers.entrySet()) {
            String modid = entry.getKey();
            EntrypointBuilder builder = entry.getValue();

            if (builder != null && !builder.hasLibraryBadge() && !isFabricApi(modid)) {
                boolean mainClassModid = builder.isValidId(modid); //The class contains a string field called MODID with the correct id
                boolean mixins = (builder.mixins.isEmpty() || builder.hasMixinsWithId(modid)); //If the mod has mixins, search for it's modid in the class path
                boolean iconPath = builder.icon.contains(modid); //modid in the mod's icon path

                boolean matchesName = builder.name.toLowerCase().replace(" ", "").equals(modid)
                        || builder.name.toLowerCase().replace(" ", "-").equals(modid)
                        || builder.name.toLowerCase().replace(" ", "_").equals(modid);

                if (!(mainClassModid || mixins || iconPath || matchesName)
                        && !modid.equals("completedshieldfix"/*There is no way of detecting it otherwise*/)) {
                    throw new TamperingErrorScreen.TamperingException(modid);
                }
            }
        }

        return containers.keySet();
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
