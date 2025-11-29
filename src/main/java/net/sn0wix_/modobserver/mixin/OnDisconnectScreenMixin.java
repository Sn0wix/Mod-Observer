package net.sn0wix_.modobserver.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.sn0wix_.modobserver.detection.IllegalStates;
import net.sn0wix_.modobserver.screen.IncompatibleModsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

        Text currentSibling = siblings.getLast();

        while (currentSibling.getSiblings().toArray().length != 0) {
            currentSibling = currentSibling.getSiblings().getLast();
        }

        String lastStr = currentSibling.getString();

        if (!lastStr.contains(IllegalStates.IDENTIFIER)) {
            return;
        }

        String jsonData = lastStr.replace(IllegalStates.IDENTIFIER, "");
        cir.setReturnValue(new IncompatibleModsScreen(removeJsonData(reasonCopy), jsonData));
    }

    @Unique
    private static MutableText removeJsonData(Text text) {
        MutableText copy = text.copy();
        copy.getSiblings().removeIf(text1 -> true);

        List<Text> siblings = text.getSiblings();
        for (Text sibling : siblings) {
            MutableText mutableChild = removeJsonData(sibling);

            if (mutableChild.getSiblings().isEmpty() && mutableChild.getString().contains(IllegalStates.IDENTIFIER)) {
                continue;
            }

            copy.append(mutableChild);
        }

        return copy;
    }
}