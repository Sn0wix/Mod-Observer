package net.sn0wix_.modObserver;

import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class TamperingErrorScreen extends Screen {
    private final String detectedOn;

    public TamperingErrorScreen(String detectedOn) {
        super(Text.translatable("text." + ModObserver.MOD_ID + ".tampering_detected"));
        this.detectedOn = detectedOn;
    }


    @Override
    public void init() {
        int height = (this.height / 2) + (this.height / 4);
        assert client != null;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), button -> client.scheduleStop()).dimensions((this.width / 2) - 100, height, 200, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("text." + ModObserver.MOD_ID + ".issue_tracker"), button -> ConfirmLinkScreen.open(this, "https://curseforge.com/minecraft/mc-mods/mod-observer/issues", true)).dimensions((this.width / 2) - 150, height - 25, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("text." + ModObserver.MOD_ID + ".discord"), button -> ConfirmLinkScreen.open(this, "https://discord.gg/nNYHDryaj3", true)).dimensions((this.width / 2) + 10, height - 25, 150, 20).build());
        height = height + 30;
        this.addDrawableChild(new TextWidget(this.width, height, Text.translatable("text." + ModObserver.MOD_ID + ".tampering.detected", detectedOn), client.textRenderer));
        height = height + 30;
        this.addDrawableChild(new TextWidget(this.width, height, Text.translatable("text." + ModObserver.MOD_ID + ".tampering.false_positive"), client.textRenderer));
    }


    @Override
    public void close() {
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }


    public static class TamperingException extends Exception {
        private final String detectedOn;

        TamperingException(String detectedOn) {
            this.detectedOn = detectedOn;
        }

        public TamperingErrorScreen getScreen() {
            return new TamperingErrorScreen(detectedOn);
        }
    }
}
