package dev.amble.ait.client.screens;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.data.ClientLandingManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.landing.LandingPadRegion;

public class LandingPadScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/landing_marker_gui.png");
    private final BlockPos pos;
    private final LandingPadRegion landingRegion;
    int bgHeight = 137;
    int bgWidth = 191;
    int left, top;
    private TextFieldWidget landingCodeInput;

    public LandingPadScreen(BlockPos pos) {
        super(Text.literal("landing_pad"));

        this.client = MinecraftClient.getInstance();
        this.pos = pos;
        this.landingRegion = ClientLandingManager.getInstance().getRegion(new ChunkPos(pos));
    }

    @Override
    protected void init() {
        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;
        this.landingCodeInput = new TextFieldWidget(this.textRenderer, (int) (left + (bgWidth * 0.06f)), (this.height / 2) - 20, 120, this.textRenderer.fontHeight + 4,
                Text.translatable("message.ait.landing_code"));
        this.addButton(new PressableTextWidget((width / 2 + 40), (height / 2) - 20,
                this.textRenderer.getWidth("✓"), 20, Text.literal("✓").formatted(Formatting.BOLD), button -> {
            updateLandingCode();
        }, this.textRenderer));

        this.landingCodeInput.setMaxLength(50);
        this.landingCodeInput.setDrawsBackground(true);
        this.landingCodeInput.setVisible(true);

        if (this.landingRegion == null) {
            this.close();
            return;
        }

        if(this.landingRegion.getLandingCode().isBlank())
            this.landingCodeInput.setPlaceholder(Text.translatable("message.ait.enter_landing_code"));
        else
            this.landingCodeInput.setText(this.landingRegion.getLandingCode());

        this.addSelectableChild(this.landingCodeInput);
        super.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Check if a text field is focused to prevent closing
        if (this.landingCodeInput.isActive())
            return super.keyPressed(keyCode, scanCode, modifiers);

        // Close the screen when the inventory key is pressed
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void updateLandingCode() {
        PacketByteBuf buf = PacketByteBufs.create();
        if (this.pos == null) return;

        buf.writeBlockPos(this.pos);
        buf.writeString(this.landingCodeInput.getText());

        ClientPlayNetworking.send(TardisUtil.REGION_LANDING_CODE, buf);
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        button.active = true; // this whole method is unnecessary bc it defaults to true ( ?? )
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);

        this.landingCodeInput.render(context, mouseX, mouseY, delta);
        this.landingCodeInput.setEditableColor(this.landingCodeInput.isSelected() || !this.landingCodeInput.getText().isBlank() ? 0xffffff: 0x545454);

        super.render(context, mouseX, mouseY, delta);
    }
}
