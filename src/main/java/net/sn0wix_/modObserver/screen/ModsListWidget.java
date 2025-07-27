package net.sn0wix_.modObserver.screen;

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
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.ModObserver;

import java.util.List;
import java.util.function.Supplier;

public class ModsListWidget extends ElementListWidget<ModsListWidget.Entry> {
    private int maxKeyNameLength;

    public ModsListWidget(ModsScreen parent, MinecraftClient client) {
        super(client, parent.width, parent.layout.getContentHeight(), parent.layout.getHeaderHeight(), 20);

        parent.detectedOn.forEach(container -> {
            int i = client.textRenderer.getWidth(Text.literal(container.getName()));

            if (i > this.maxKeyNameLength) {
                this.maxKeyNameLength = i;
            }

            this.addEntry(new ModEntry(container, parent));
        });
    }

    @Override
    public int getRowWidth() {
        return getWidth();
    }

    @Override
    protected int getScrollbarX() {
        return this.getX() + this.width / 2 - 340 / 2 + 340 + 10;
    }

    public class ModEntry extends Entry {
        private final ModsScreen.Container container;
        private final ButtonWidget issuesButton;
        private final ButtonWidget homepageButton;
        private final TextWidget name;

        public ModEntry(ModsScreen.Container container, ModsScreen parent) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            this.container = container;
            this.issuesButton = ButtonWidget.builder(Text.translatable("text." + ModObserver.MOD_ID + ".issue_tracker"),
                            ConfirmLinkScreen.opening(parent, container.getIssues(), false))
                    .size(textRenderer.getWidth(Text.translatable("text." + ModObserver.MOD_ID + ".issue_tracker")) + 8, 20).build();

            this.homepageButton = ButtonWidget.builder(Text.translatable("text." + ModObserver.MOD_ID + ".homepage"),
                            ConfirmLinkScreen.opening(parent, container.getHomepage(), false))
                    .size(textRenderer.getWidth(Text.translatable("text." + ModObserver.MOD_ID + ".homepage")) + 8, 20).build();

            this.name = new TextWidget(Text.literal(container.getName()), textRenderer);
            name.setTooltip(Tooltip.of(Text.literal(container.getModid())));

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
            this.issuesButton.setPosition(resetButtonPos, j);
            issuesButton.render(context, mouseX, mouseY, tickProgress);

            int editButtonPos = resetButtonPos - 8 - this.homepageButton.getWidth();
            homepageButton.setPosition(editButtonPos, j);
            homepageButton.render(context, mouseX, mouseY, tickProgress);

            name.setPosition(startPos, y + entryHeight / 2 - 9 / 2);
            name.render(context, mouseX, mouseY, tickProgress);

            //TODO fix icon loading
            /*if (container.hasIcon()) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, container.getIconIdentifier(),
                        startPos - 25, y, 0, 0, 0, 20, 20, 20, 20);
            }*/
        }
    }


    public abstract static class Entry extends ElementListWidget.Entry<ModsListWidget.Entry> {
    }
}
