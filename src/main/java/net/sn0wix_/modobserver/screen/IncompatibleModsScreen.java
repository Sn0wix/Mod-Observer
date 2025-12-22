package net.sn0wix_.modobserver.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.sn0wix_.modobserver.ModObserver;

public class IncompatibleModsScreen extends Screen {
    String jsonData;
    Text reason;
    private final DirectionalLayoutWidget grid = DirectionalLayoutWidget.vertical();

    public IncompatibleModsScreen(Text kickMessage, String jsonData) {
        super(Text.translatable("connect.failed"));
        this.jsonData = jsonData;
        this.reason = kickMessage;
    }

    @Override
    protected void init() {
        this.grid.getMainPositioner().alignHorizontalCenter().margin(10);
        this.grid.add(new TextWidget(this.title, this.textRenderer));
        this.grid.add(new MultilineTextWidget(reason, this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
        this.grid.getMainPositioner().margin(2);

        this.grid.add(ButtonWidget.builder(Text.translatable("gui.mod_observer.show_mods"), button -> {
                    try {
                        this.client.setScreen(new ModsScreen(jsonData));
                    } catch (IllegalStateException e) {
                        ModObserver.LOGGER.error("Could not create IncompatibleModsScreen. Invalid json data!", e);
                        MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.UNSECURE_SERVER_WARNING,
                                Text.literal("Unable to create mods screen!"), Text.literal("Could not parse mods json data sent by the server.")));
                    }
                })
                .dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());

        this.grid.add(ButtonWidget.builder(Text.translatable("gui.toMenu"), button -> client.setScreen(new MultiplayerScreen(new TitleScreen())))
                .dimensions(this.width / 2 - 100, (this.height / 4 + 120 + 12) + 10, 200, 20).build());


        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        SimplePositioningWidget.setPos(this.grid, this.getNavigationFocus());
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.title, reason);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
