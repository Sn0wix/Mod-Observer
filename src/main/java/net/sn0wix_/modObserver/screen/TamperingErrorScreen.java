package net.sn0wix_.modObserver.screen;

import net.minecraft.text.Text;
import net.sn0wix_.modObserver.ModObserver;

import java.util.List;

//TODO add discord and issue tracker buttons
public class TamperingErrorScreen extends ModsScreen {

    public TamperingErrorScreen(List<net.fabricmc.loader.api.ModContainer> detectedOn) {
        super(Text.translatable("screen." + ModObserver.MOD_ID + ".tampering"), Container.cast(detectedOn));
    }


    @Override
    public void close() {
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
