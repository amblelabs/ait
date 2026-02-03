package dev.amble.ait.client.screens.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class WorldListWidget extends EntryListWidget<WorldListWidget.WorldEntry> {
    public interface SelectionHandler {
        void onSelect(RegistryKey<World> key);
    }

    private final SelectionHandler select;
    private WorldEntry entry;

    public WorldListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, int left, SelectionHandler onSelect) {
        super(client, width, height, top, bottom, itemHeight);
        this.select = onSelect;
        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
        this.setLeftPos(left);
    }

    public void addWorld(RegistryKey<World> key, Text label) {
        super.addEntry(new WorldEntry(this, key, label));
    }

    public WorldEntry getSelected() {
        return this.entry;
    }

    @Override
    public int getRowWidth() {
        return this.width - 15;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.left + this.width - 6;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        WorldEntry selected = this.getSelected();
        if (selected != null) {
            builder.put(NarrationPart.TITLE, Text.literal("world: ").copy().append(selected.label));
        } else {
            builder.put(NarrationPart.TITLE, Text.literal("dimension skys"));
        }
    }

    public static class WorldEntry extends EntryListWidget.Entry<WorldEntry> {
        private final WorldListWidget parent;
        private final RegistryKey<World> key;
        final Text label;

        public WorldEntry(WorldListWidget parent, RegistryKey<World> key, Text label) {
            this.parent = parent;
            this.key = key;
            this.label = label;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            int color = hovered ? 0xFFFFA0 : 0xFFFFFF;
            context.drawText(MinecraftClient.getInstance().textRenderer, this.label, x + 4, y + (entryHeight - 9) / 2, color, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                this.parent.entry = this;
                this.parent.select.onSelect(this.key);
                return true;
            }
            return false;
        }

        public void appendNarrations(NarrationMessageBuilder builder) {
            builder.put(NarrationPart.TITLE, this.label);
        }
    }
}
