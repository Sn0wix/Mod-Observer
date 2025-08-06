package net.sn0wix_.modObserver.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.ModObserver;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModsScreen extends Screen {
    public final List<Container> detectedOn;
    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 43, 30);
    private ModsListWidget listWidget;


    public ModsScreen(Text title, List<Container> detectedOn) {
        super(title);
        this.detectedOn = detectedOn;

        detectedOn.forEach(mod -> {
            if (mod.hasIcon()) {
                NativeImageBackedTexture icon = mod.getIcon();
                icon.setFilter(false, false);
                MinecraftClient.getInstance().getTextureManager().registerTexture(mod.getIconId(), icon);
            }
        });
    }

    public ModsScreen(List<Container> detectedOn) {
        this(Text.translatable("screen." + ModObserver.MOD_ID + ".kick"), detectedOn);
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
        column.add(new TextWidget(this.width, 9, this.title, this.textRenderer));
        column.add(new TextWidget(this.width, 9, Text.translatable("text." + ModObserver.MOD_ID + ".incompatible_mods"), this.textRenderer));

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

    public static class Container {
        private final String name;
        private final String modid;
        private final String issues;
        private final String homepage;
        private final String icon;

        public Container(net.fabricmc.loader.api.ModContainer modContainer) {
            this(modContainer.getMetadata().getName(), modContainer.getMetadata().getId(),
                    modContainer.getMetadata().getContact().get("issues").orElse(""),
                    modContainer.getMetadata().getContact().get("homepage").orElse(""),
                    modContainer.getMetadata().getIconPath(32).orElse(""));
        }

        public Container(String name, String modid, String issues, String homepage, String icon) {
            this.name = name;
            this.modid = modid;
            this.issues = issues;
            this.homepage = homepage;
            this.icon = icon;
        }

        public static List<Container> cast(List<ModContainer> list) {
            List<Container> containers = new ArrayList<>(list.size());
            list.forEach(entry -> containers.add(new Container(entry)));
            return containers;
        }

        public String getName() {
            return name;
        }

        public String getModid() {
            return modid;
        }

        public URI getIssues() {
            return issues.isEmpty() ? URI.create("") : URI.create(issues);
        }

        public URI getHomepage() {
            return homepage.isEmpty() ? URI.create("") : URI.create(homepage);
        }

        public boolean hasIcon() {
            return !icon.isEmpty();
        }

        public String getIconPath() {
            return icon;
        }

        public Identifier getIconId() {
            return Identifier.of(ModObserver.MOD_ID, getModid() + "_icon");
        }

        public NativeImageBackedTexture getIcon() {
            try {
                Path path = FabricLoader.getInstance().getModContainer(getModid()).get().findPath(getIconPath()).get();
                InputStream inputStream = Files.newInputStream(path);
                NativeImage image = NativeImage.read(Objects.requireNonNull(inputStream));
                Validate.validState(image.getHeight() == image.getWidth(), "Must be square icon");
                return new NativeImageBackedTexture(() -> Identifier.of(ModObserver.MOD_ID, path.toString()).toString(), image);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
