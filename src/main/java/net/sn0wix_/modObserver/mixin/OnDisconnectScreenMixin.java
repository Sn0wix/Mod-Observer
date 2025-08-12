package net.sn0wix_.modObserver.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.DisconnectionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientCommonNetworkHandler.class)
public class OnDisconnectScreenMixin {
    @Inject(at = @At("RETURN"), method = "createDisconnectedScreen")
    public void injectReturn(DisconnectionInfo info, CallbackInfoReturnable<Screen> cir) {

    }
}
