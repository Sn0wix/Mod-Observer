package net.sn0wix_.modObserver.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.sn0wix_.modObserver.ModObserver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(TitleScreen.class)
public abstract class ClientFinishLoadingMixin {
    @Inject(method = "init", at = @At(value = "TAIL"))
    private void injectInit(CallbackInfo ci) {
        try {
            ModObserver.getMods();
        } catch (ModObserver.TamperingException e) {
            MinecraftClient.getInstance().setScreen(e.getScreen());
        }catch (IOException e) {
            System.out.println("Something happened with the conflicts file");
            e.printStackTrace();
        }
    }
}
