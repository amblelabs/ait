package dev.amble.ait.client.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class BlueprintFabricatorScreen extends Screen {

    private static final Identifier TEXTURE = AITMod.id("textures/gui/blueprinting_deck.png");
    int backgroundHeight = 166;
    int backgroundWidth = 176;

    public BlueprintFabricatorScreen() {
        super(Text.literal("Blueprint Fabricator")); // todo translatable
    }

    protected void drawBackground(DrawContext context) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = ((this.height) - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        drawBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }
}
