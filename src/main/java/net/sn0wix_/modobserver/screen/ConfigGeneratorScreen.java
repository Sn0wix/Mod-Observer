package net.sn0wix_.modobserver.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.sn0wix_.modobserver.ModObserver;
import net.sn0wix_.modobserver.detection.ModEntry;
import net.sn0wix_.modobserver.detection.Utils;
import net.sn0wix_.modobserver.screen.gui.CheckboxWidget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigGeneratorScreen extends Screen {
    private final DirectionalLayoutWidget grid = DirectionalLayoutWidget.vertical().spacing(10);
    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 43, 70);
    private final Screen parent;

    private CheckboxWidget modmenuWidget;
    private CheckboxWidget childrenWidget;
    private CheckboxWidget fabricApiWidget;
    private CheckboxWidget hashesWidget;


    public ConfigGeneratorScreen(Screen parent) {
        super(Text.translatable("screen.mod_observer.config_generator"));
        this.parent = parent;
    }

    protected void init() {
        this.initHeader();
        this.initBody();
        this.initFooter();
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    protected void initHeader() {
        DirectionalLayoutWidget column = DirectionalLayoutWidget.vertical();
        column.add(new TextWidget(textRenderer.getWidth(this.title), 9, this.title, this.textRenderer).alignCenter());

        this.layout.addHeader(column);
    }

    protected void initBody() {
        modmenuWidget = new CheckboxWidget.Builder(Text.translatable("text.mod_observer.include.modmenu"), textRenderer).build();
        childrenWidget = new CheckboxWidget.Builder(Text.translatable("text.mod_observer.include.children"), textRenderer).build();
        fabricApiWidget = new CheckboxWidget.Builder(Text.translatable("text.mod_observer.include.fabric_api"), textRenderer).build();
        hashesWidget = new CheckboxWidget.Builder(Text.translatable("text.mod_observer.include.hashes"), textRenderer).build();

        modmenuWidget.onPress();
        childrenWidget.onPress();
        fabricApiWidget.onPress();
        hashesWidget.onPress();

        grid.add(modmenuWidget);
        grid.add(hashesWidget);
        grid.add(childrenWidget);
        grid.add(fabricApiWidget);

        this.layout.addBody(grid);
    }

    protected void initFooter() {
        DirectionalLayoutWidget layoutWidget = DirectionalLayoutWidget.vertical().spacing(2);

        layoutWidget.add(ButtonWidget.builder(Text.translatable("text.mod_observer.generate_config"), (button -> {
            String dateTime = LocalDate.now() + "T" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
            dateTime = dateTime.replace(":", "_").replace("-", "_");


            File file = new File("config/" + ModObserver.MOD_ID + "/" + dateTime + ".json");

            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                FileWriter writer = new FileWriter(file);
                LinkedHashMap<ModEntry, Object> map = Utils.getModsList();

                Iterator<Map.Entry<ModEntry, Object>> iterator = map.entrySet().iterator();
                Map.Entry<ModEntry, Object> entry;

                while (iterator.hasNext()) {
                    entry = iterator.next();

                    if ((!includeFabricApi() && Utils.isFabricApi(entry.getKey().getId())) ||
                            (!includeModMenu() && entry.getKey().getId().equals("modmenu"))) {
                        iterator.remove();
                    }

                    if (!includeHashes() && !entry.getKey().getHash().isEmpty()) {
                        entry.getKey().removeHash();
                    }

                    if (!includeChildren()) {
                        entry.setValue(Map.of());
                    }
                }

                writer.write(Utils.toJson(map));
                writer.close();
                Util.getOperatingSystem().open(file.getParentFile());
            } catch (IOException e) {
                ModObserver.LOGGER.error("Failed to create config file: ", e);
            }

        })).size(200, 20).build());

        layoutWidget.add(new ButtonWidget.Builder(Text.translatable("gui.back"),
                button -> this.client.setScreen(parent)).size(200, 20).build());


        this.layout.addFooter(layoutWidget);
    }

    public boolean includeChildren() {
        return childrenWidget.isChecked();
    }

    public boolean includeHashes() {
        return hashesWidget.isChecked();
    }

    public boolean includeModMenu() {
        return modmenuWidget.isChecked();
    }

    public boolean includeFabricApi() {
        return fabricApiWidget.isChecked();
    }

    protected void refreshWidgetPositions() {
        SimplePositioningWidget.setPos(this.grid, this.getNavigationFocus());
        this.layout.refreshPositions();
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(parent);
    }
}
