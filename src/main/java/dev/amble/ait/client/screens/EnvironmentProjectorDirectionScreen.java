package dev.amble.ait.client.screens;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.tardis.ClientTardis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EnvironmentProjectorDirectionScreen extends TardisScreen{
    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/environment_menu_direction.png");
    int bgHeight = 216;
    int bgWidth = 150;
    int left, top;

    public EnvironmentProjectorDirectionScreen(ClientTardis tardis) {
        super(Text.of("screen." + AITMod.MOD_ID + ".environment_projector_direction"), tardis);
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    protected void init() {
        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;

        super.init();

        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("SWITCH")) / 2), (height / 2 + 12),
                this.textRenderer.getWidth(Text.literal("SWITCH")), 10, Text.literal("SWITCH"), button -> AITMod.LOGGER.info("button"), this.textRenderer));

        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("Direction")) / 2 - 35), (height / 2 - 71),
                this.textRenderer.getWidth(Text.literal("Direction")), 10, Text.literal("Direction"), button -> new EnvironmentProjectorDirectionScreen(tardis()), this.textRenderer));
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
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
    }
}
