package dev.amble.ait.client.screens;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.screens.widget.SwitcherManager;
import dev.amble.ait.core.blocks.AstralMapBlock;

public class AstralMapScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/security_menu.png");
    int bgHeight = 138;
    int bgWidth = 216;
    int left, top;
    final SwitcherManager.IdentifierSwitcher switcher;

    public AstralMapScreen() {
        super(Text.translatable("screen." + AITMod.MOD_ID + ".astral_map"));

        this.client = MinecraftClient.getInstance();

        this.switcher = new SwitcherManager.IdentifierSwitcher(AstralMapBlock.structureIds, (id) -> {
            ClientPlayNetworking.send(AstralMapBlock.REQUEST_SEARCH, PacketByteBufs.create().writeIdentifier(id));
            this.close();
        });
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;

        super.init();

        this.addDrawableChild(new PressableTextWidget((width / 2 - 30), (height / 2 + 12),
                this.textRenderer.getWidth("<"), 10, Text.literal("<"), button -> this.switcher.previous(), this.textRenderer));
        this.addDrawableChild(new PressableTextWidget((width / 2 + 25), (height / 2 + 12),
                this.textRenderer.getWidth(">"), 10, Text.literal(">"), button -> this.switcher.next(), this.textRenderer));
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("SEARCH")) / 2), (height / 2 + 12),
                this.textRenderer.getWidth(Text.literal("SEARCH")), 10, Text.literal("SEARCH"), button -> this.switcher.sync(null), this.textRenderer));
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

        Text currentText = Text.literal(switcher.get().name().toUpperCase());
        context.drawText(this.textRenderer, currentText, (int) (left + (bgWidth * 0.5f)) - this.textRenderer.getWidth(currentText) / 2,
                (int) (top + (bgHeight * 0.5)), 0xffffff, true);
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
    }

}