package dev.amble.ait.client.screens;

import java.util.List;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.client.screens.widget.SwitcherManager;
import dev.amble.ait.core.blocks.AstralMapBlock;
import dev.amble.ait.core.util.WorldUtil;

public class AstralMapScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/astral_map_menu.png");
    int bgHeight = 138;
    int bgWidth = 216;
    int left, top;

    private IdentifierSwitcher switcher;
    private IdentifierSwitcher biomeSwitcher;

    private boolean waitingOnBiomes;
    private boolean waitingOnStructures;

    private enum View { ROOT, STRUCTURES, BIOMES }
    private View currentView = View.ROOT;

    public AstralMapScreen() {
        super(Text.translatable("screen." + AITMod.MOD_ID + ".astral_map"));
        this.client = MinecraftClient.getInstance();

        waitingOnStructures = (AstralMapBlock.structureIds == null || AstralMapBlock.structureIds.isEmpty());
        waitingOnBiomes = (AstralMapBlock.biomeIds == null || AstralMapBlock.biomeIds.isEmpty());
        if (!waitingOnStructures) {
            this.switcher = new IdentifierSwitcher(AstralMapBlock.structureIds, (id) -> {
                ClientPlayNetworking.send(AstralMapBlock.REQUEST_SEARCH, PacketByteBufs.create().writeIdentifier(id));
                this.close();
            });
        }

        if (!waitingOnBiomes) {
            this.biomeSwitcher = new IdentifierSwitcher(AstralMapBlock.biomeIds, (id) -> {
                ClientPlayNetworking.send(AstralMapBlock.REQUEST_SEARCH, PacketByteBufs.create().writeIdentifier(id));
                this.close();
            });
        }
    }

    public void reloadData() {
        boolean changed = false;

        if (waitingOnStructures && AstralMapBlock.structureIds != null && !AstralMapBlock.structureIds.isEmpty()) {
            this.switcher = new IdentifierSwitcher(AstralMapBlock.structureIds, (id) -> {
                ClientPlayNetworking.send(AstralMapBlock.REQUEST_SEARCH, PacketByteBufs.create().writeIdentifier(id));
                this.close();
            });
            waitingOnStructures = false;
            changed = true;
        }

        if (waitingOnBiomes && AstralMapBlock.biomeIds != null && !AstralMapBlock.biomeIds.isEmpty()) {
            this.biomeSwitcher = new IdentifierSwitcher(AstralMapBlock.biomeIds, (id) -> {
                ClientPlayNetworking.send(AstralMapBlock.REQUEST_SEARCH, PacketByteBufs.create().writeIdentifier(id));
                this.close();
            });
            waitingOnBiomes = false;
            changed = true;
        }

        if (changed) {
            this.clearAndInit();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.top = (this.height - this.bgHeight) / 2;
        this.left = (this.width - this.bgWidth) / 2;
        currentView = View.ROOT;
        super.init();

        this.addDrawableChild(new PressableTextWidget(
                (width / 2 - this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.structures.button")) / 2),
                (height / 2 + 9),
                this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.structures.button")),
                10,
                Text.translatable("screen.ait.astral_map.structures.button"),
                button -> structureFinderButton(),
                this.textRenderer
        ));

        this.addDrawableChild(new PressableTextWidget(
                (width / 2 - this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.biomes.button")) / 2),
                (height / 2 - 1),
                this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.biomes.button")),
                10,
                Text.translatable("screen.ait.astral_map.biomes.button"),
                button -> biomeFinderButton(),
                this.textRenderer
        ));
    }

    private void biomeFinderButton() {
        this.clearChildren();
        this.setFocused(null);
        currentView = View.BIOMES;

        if (!waitingOnBiomes && this.biomeSwitcher != null) {
            this.addDrawableChild(new PressableTextWidget((width / 2 - 46), (height / 2 - 0),
                    this.textRenderer.getWidth("<"), 10, Text.translatable("screen.ait.astral_map.switcher.left_arrow"), button -> this.biomeSwitcher.previous(), this.textRenderer));
            this.addDrawableChild(new PressableTextWidget((width / 2 + 40), (height / 2 - 1),
                    this.textRenderer.getWidth(">"), 10, Text.translatable("screen.ait.astral_map.switcher.right_arrow"), button -> this.biomeSwitcher.next(), this.textRenderer));

            this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.search.button")) / 2), (height / 2 + 12),
                    this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.search.button")), 10, Text.translatable("screen.ait.astral_map.search.button"), button -> this.biomeSwitcher.sync(null), this.textRenderer));
        }
    }

    private void structureFinderButton() {
        this.clearChildren();
        this.setFocused(null);
        currentView = View.STRUCTURES;

        Text text = Text.literal(this.switcher != null ? this.switcher.get().name().toUpperCase() : "LOADING");
        int w = this.textRenderer.getWidth(text);

        if (!waitingOnStructures && this.switcher != null) {
            this.addDrawableChild(new PressableTextWidget((width / 2 - 46), (height / 2 - 0),
                    this.textRenderer.getWidth("<"), 10, Text.translatable("screen.ait.astral_map.switcher.left_arrow"), button -> this.switcher.previous(), this.textRenderer));
            this.addDrawableChild(new PressableTextWidget((width / 2 + 46), (height / 2 - 0),
                    this.textRenderer.getWidth(">"), 10, Text.translatable("screen.ait.astral_map.switcher.right_arrow"), button -> this.switcher.next(), this.textRenderer));

            this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.search.button")) / 2), (height / 2 + 12),
                    this.textRenderer.getWidth(Text.translatable("screen.ait.astral_map.search.button")), 10, Text.translatable("screen.ait.astral_map.search.button"), button -> this.switcher.sync(null), this.textRenderer));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context);

        super.render(context, mouseX, mouseY, delta);

        if (currentView.equals(View.STRUCTURES)) {
            Text currentText = (!waitingOnStructures && this.switcher != null) ? Text.literal(this.switcher.get().name().toUpperCase()) : Text.translatable("screen.ait.astral_map.loading");
            context.drawText(this.textRenderer, currentText, (int) (left + (bgWidth * 0.5f)) - this.textRenderer.getWidth(currentText) / 2,
                    (int) (top + (bgHeight * 0.5)), 0xffffff, true);
        } else if (currentView.equals(View.BIOMES)) {
            Text currentText = (!waitingOnBiomes && this.biomeSwitcher != null) ? Text.literal(this.biomeSwitcher.get().name().toUpperCase()) : Text.translatable("screen.ait.astral_map.loading");
            context.drawText(this.textRenderer, currentText, (int) (left + (bgWidth * 0.5f)) - this.textRenderer.getWidth(currentText) / 2,
                    (int) (top + (bgHeight * 0.5)), 0xffffff, true);
        }
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
    }

    record IdentifierToName(Identifier id) implements Nameable {
        @Override
        public String name() {
            try {
                return WorldUtil.fakeTranslate(id.getPath());
            } catch (Exception e) {
                return id.toString();
            }
        }
    }

    static class IdentifierSwitcher extends SwitcherManager<IdentifierToName, Identifier> {
        public IdentifierSwitcher(List<Identifier> list, Consumer<Identifier> sync) {
            super((var) -> next(var, list), (var) -> prev(var, list), (var, arg) -> {sync.accept(var.id());}, new IdentifierToName(list.get(0)), "identifier");
        }

        private static IdentifierToName next(IdentifierToName id, List<Identifier> list) {
            int idx =  list.indexOf(id.id());
            idx = (idx + 1) % list.size();
            return new IdentifierToName(list.get(idx));
        }

        private static IdentifierToName prev(IdentifierToName id, List<Identifier> list) {
            int idx = list.indexOf(id.id());
            idx = (idx - 1 + list.size()) % list.size();
            return new IdentifierToName(list.get(idx));
        }
    }
}
