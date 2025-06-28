package net.sn0wix_.modObserver.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.sn0wix_.modObserver.TamperingErrorScreen;
import net.sn0wix_.modObserver.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class ClientFinishLoadingMixin {
    @Inject(method = "init", at = @At(value = "TAIL"))
    private void injectInit(CallbackInfo ci) {
        try {
            Utils.getMods();
        } catch (TamperingErrorScreen.TamperingException e) {
            MinecraftClient.getInstance().setScreen(e.getScreen());
        }
    }
}
