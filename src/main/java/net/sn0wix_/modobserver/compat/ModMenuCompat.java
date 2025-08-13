package net.sn0wix_.modobserver.compat;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class ModMenuCompat implements ModMenuApi {
    private static FabricIconHandler iconHandler;

    public static NativeImageBackedTexture getIconImage(String modid) {
        if (iconHandler == null) {
            iconHandler = new FabricIconHandler();
        }
        try {
            return ModMenu.MODS.get(modid).getIcon(iconHandler, 32);
        } catch (Exception e) {
            return iconHandler.createIcon(FabricLoader.getInstance().getModContainer("modmenu").orElseThrow(() -> new RuntimeException("Cannot get ModContainer for Fabric mod with id modmenu")), "assets/modmenu/unknown_icon.png");
        }
    }

    public static void closeIconHandler() {
        iconHandler.close();
        iconHandler = null;
    }
}
