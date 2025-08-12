package net.sn0wix_.modObserver.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.sn0wix_.modObserver.ModObserver;
import net.sn0wix_.modObserver.compat.ModMenuCompat;
import net.sn0wix_.modObserver.detection.IllegalStates;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class ModsListWidget extends ElementListWidget<ModsListWidget.Entry> {
    private int maxKeyNameLength;

    public ModsListWidget(ModsScreen parent, MinecraftClient client) {
        super(client, parent.width, parent.layout.getContentHeight(), parent.layout.getHeaderHeight(), 20);

        /*parent.detectedOn.forEach(container -> {
            int i = client.textRenderer.getWidth(Text.literal(container.getName()));

            if (i > this.maxKeyNameLength) {
                this.maxKeyNameLength = i;
            }

            this.addEntry(new ModEntry(container, parent));
        });*/
    }

    @Override
    public int getRowWidth() {
        return getWidth();
    }

    @Override
    protected int getScrollbarX() {
        return this.getX() + this.width / 2 - 340 / 2 + 340 + 10;
    }

    public class TitleEntry extends Entry {
        private IllegalStates state;

        public TitleEntry(IllegalStates state) {
            this.state = state;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return null;
        }

        @Override
        public List<? extends Element> children() {
            return null;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {

        }
    }

    public class ModEntry extends Entry {
        Identifier iconLocation;
        private final ModsScreen.Container container;
        private final ButtonWidget issuesButton;
        private final ButtonWidget homepageButton;
        private TextWidget name;

        public ModEntry(ModsScreen.Container container, ModsScreen parent) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            this.container = container;

            this.issuesButton = ButtonWidget.builder(Text.translatable("text.mod_observer.issue_tracker"),
                            ConfirmLinkScreen.opening(parent, container.getIssues(), false))
                    .size(textRenderer.getWidth(Text.translatable("text.mod_observer.issue_tracker")) + 8, 20).build();

            this.homepageButton = ButtonWidget.builder(Text.translatable("text.mod_observer.homepage"),
                            ConfirmLinkScreen.opening(parent, container.getHomepage(), false))
                    .size(textRenderer.getWidth(Text.translatable("text.mod_observer.homepage")) + 8, 20).build();

            Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(container.getModid());


            try {
                Path path = modContainer.get().getOrigin().getPaths().getFirst();
                this.name = new TextWidget(Text.literal(container.getName()), textRenderer) {
                    @Override
                    public void onClick(double mouseX, double mouseY) {
                        try {
                            Util.getOperatingSystem().open(modContainer.get().getOrigin().getPaths().getFirst().getParent());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                name.setTooltip(Tooltip.of(Text.literal(path.getFileName().toString())));
                name.active = true;
            } catch (Exception e) {
                this.name = new TextWidget(Text.literal(container.getName()), textRenderer);
                name.setTooltip(Tooltip.of(Text.literal("id: " + container.getModid())));
            }


            this.issuesButton.active = !container.getIssues().toString().isEmpty();
            this.homepageButton.active = !container.getHomepage().toString().isEmpty();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(new Selectable() {
                @Override
                public SelectionType getType() {
                    return SelectionType.HOVERED;
                }

                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    builder.put(NarrationPart.TITLE, Text.literal(container.getName()));
                }
            });
        }

        @Override
        public List<? extends Element> children() {
            return List.of(issuesButton, homepageButton, name);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int resetButtonPos = ModsListWidget.this.getScrollbarX() - this.issuesButton.getWidth() - 8;
            int j = y - 2;
            int startPos = getWidth() / 2 - maxKeyNameLength;

            if (startPos < 10) {
                startPos = 10;
            }

            name.setPosition(startPos + (ModObserver.HAS_MODMENU ? 25 : 0), y + entryHeight / 2 - 9 / 2);
            name.render(context, mouseX, mouseY, tickProgress);

            if (ModObserver.HAS_MODMENU) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, getIconTexture(), startPos, y, 0, 0, 20, 20, 20, 20);
            }

            this.issuesButton.setPosition(resetButtonPos, j);
            issuesButton.render(context, mouseX, mouseY, tickProgress);

            int editButtonPos = resetButtonPos - 8 - this.homepageButton.getWidth();
            homepageButton.setPosition(editButtonPos, j);
            homepageButton.render(context, mouseX, mouseY, tickProgress);
        }

        public Identifier getIconTexture() {
            if (ModObserver.HAS_MODMENU) {
                if (this.iconLocation == null) {
                    this.iconLocation = Identifier.of(ModObserver.MOD_ID, container.getModid() + "_icon");
                    NativeImageBackedTexture icon = ModMenuCompat.getIconImage(container.getModid());
                    icon.setFilter(false, false);
                    MinecraftClient.getInstance().getTextureManager().registerTexture(this.iconLocation, icon);
                }

                return iconLocation;
            }

            return null;
        }
    }


    public abstract static class Entry extends ElementListWidget.Entry<ModsListWidget.Entry> {
    }
}
