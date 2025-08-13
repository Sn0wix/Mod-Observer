package net.sn0wix_.modObserver.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.sn0wix_.modObserver.detection.IllegalStates;
import net.sn0wix_.modObserver.screen.IncompatibleModsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientCommonNetworkHandler.class)
public class OnDisconnectScreenMixin {
    @Inject(at = @At("RETURN"), method = "createDisconnectedScreen", cancellable = true)
    public void injectReturn(DisconnectionInfo info, CallbackInfoReturnable<Screen> cir) {
        Text reason = info.reason();
        if (!reason.getString().contains(IllegalStates.IDENTIFIER)) {
            return;
        }

        MutableText reasonCopy = reason.copy();
        List<Text> siblings = reasonCopy.getSiblings();

        if (siblings.isEmpty()) {
            return;
        }

        Text last = siblings.getLast();
        String lastStr = last.getString();

        if (!lastStr.contains(IllegalStates.IDENTIFIER)) {
            return;
        }

        String jsonData = lastStr.replace(IllegalStates.IDENTIFIER, "");
        siblings.removeLast();

        cir.setReturnValue(new IncompatibleModsScreen(reasonCopy, jsonData));
    }
}