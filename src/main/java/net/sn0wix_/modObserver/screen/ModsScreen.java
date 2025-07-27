package net.sn0wix_.modObserver.screen;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.sn0wix_.modObserver.ModObserver;

import java.util.List;

//TODO add a scrollable list with issue tracker button and homepage button
public class ModsScreen extends Screen {
    private List<ModContainer> detectedOn;

    public ModsScreen(Text title, List<ModContainer> detectedOn) {
        super(title);
        this.detectedOn = detectedOn;
    }

    public ModsScreen(List<ModContainer> detectedOn) {
        super(Text.translatable("screen." + ModObserver.MOD_ID + ".kick"));
        this.detectedOn = detectedOn;
    }

    @Override
    public void init() {
        super.init();
    }
}
