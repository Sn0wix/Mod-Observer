package net.sn0wix_.modobserver.screen;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.sn0wix_.modobserver.ModObserver;
import net.sn0wix_.modobserver.compat.ModMenuCompat;
import net.sn0wix_.modobserver.detection.IllegalStates;
import net.sn0wix_.modobserver.screen.gui.ModsListWidget;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class ModsScreen extends Screen {
    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 43, 30);
    private ModsListWidget listWidget;
    public final Map<IllegalStates, List<String>> detectedOn;


    public ModsScreen(Text title, String kickJsonData) {
        super(title);

        //Serialize the data
        Type type = new TypeToken<Map<IllegalStates, List<String>>>() {}.getType();
        detectedOn = new GsonBuilder().create().fromJson(kickJsonData, type);
    }

    public ModsScreen(String kickJsonData) {
        this(Text.translatable("screen.mod_observer.mods_list"), kickJsonData);
    }

    protected void init() {
        this.initHeader();
        this.initBody();
        this.initFooter();
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    protected void initHeader() {
        DirectionalLayoutWidget column = DirectionalLayoutWidget.vertical().spacing(10);
        column.add(new TextWidget(textRenderer.getWidth(this.title), 9, this.title, this.textRenderer).alignCenter());

        this.layout.addHeader(column);
    }

    protected void initBody() {
        listWidget = new ModsListWidget(this, client);
        this.layout.addBody(listWidget);
    }

    protected void initFooter() {
        this.layout.addFooter(new ButtonWidget.Builder(Text.translatable("gui.toMenu"), button ->
                client.setScreen(new MultiplayerScreen(new TitleScreen()))).size((int) (this.width / 1.8), 20).build());
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.listWidget != null) {
            this.listWidget.position(this.width, this.layout);
        }
    }

    @Override
    public void close() {
        super.close();

        if (ModObserver.HAS_MODMENU) {
            ModMenuCompat.closeIconHandler();
        }
    }

    public record Container(String name, String modid, String issues, String homepage) {
            public Container(ModContainer modContainer) {
                this(modContainer.getMetadata().getName(), modContainer.getMetadata().getId(),
                        modContainer.getMetadata().getContact().get("issues").orElse(""),
                        modContainer.getMetadata().getContact().get("homepage").orElse(""));
            }

        public URI getIssues() {
                return issues.isEmpty() ? URI.create("") : URI.create(issues);
            }

            public URI getHomepage() {
                return homepage.isEmpty() ? URI.create("") : URI.create(homepage);
            }
        }
}
