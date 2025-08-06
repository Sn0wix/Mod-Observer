package net.sn0wix_.modObserver.compat;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class ModMenuCompat {
    private static FabricIconHandler iconHandler;

    public static NativeImageBackedTexture getIconImage(String modid) {
        if (iconHandler == null) {
            iconHandler = new FabricIconHandler();
        }

        return ModMenu.MODS.get(modid).getIcon(iconHandler, 32);
    }

    public static void closeIconHandler() {
        iconHandler.close();
        iconHandler = null;
    }
}
