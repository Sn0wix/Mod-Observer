package net.sn0wix_.modObserver.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;

//TODO
public class IncompatibleModsScreen extends Screen {
    List<ModsScreen.Container> detectedOn;
    Text reason;
    private final DirectionalLayoutWidget grid = DirectionalLayoutWidget.vertical();

    public IncompatibleModsScreen(List<ModsScreen.Container> detectedOn, Text reason) {
        super(Text.translatable("connect.failed"));
        this.detectedOn = detectedOn;
        this.reason = reason;
    }

    @Override
    protected void init() {
        this.grid.getMainPositioner().alignHorizontalCenter().margin(10);
        this.grid.add(new TextWidget(this.title, this.textRenderer));
        this.grid.add(new MultilineTextWidget(reason, this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
        this.grid.getMainPositioner().margin(2);

        this.grid.add(ButtonWidget.builder(Text.translatable("gui.mod_observer.show_mods"), button -> this.client.setScreen(new ModsScreen(detectedOn)))
                .dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());

        this.grid.add(ButtonWidget.builder(Text.translatable("gui.toMenu"), button -> client.setScreen(new MultiplayerScreen(new TitleScreen())))
                .dimensions(this.width / 2 - 100, (this.height / 4 + 120 + 12) + 10, 200, 20).build());


        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
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
