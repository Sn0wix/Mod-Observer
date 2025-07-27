package net.sn0wix_.modObserver.screen;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.sn0wix_.modObserver.ModObserver;

import java.util.List;

//TODO add discord and issue tracker buttons
public class TamperingErrorScreen extends ModsScreen {

    public TamperingErrorScreen(List<ModContainer> detectedOn) {
        super(Text.translatable("screen." + ModObserver.MOD_ID + ".tampering"), detectedOn);
    }


    @Override
    public void close() {
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
