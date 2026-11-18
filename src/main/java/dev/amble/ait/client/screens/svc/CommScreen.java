package dev.amble.ait.client.screens.svc;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.screens.TardisScreen;
import dev.amble.ait.client.tardis.ClientTardis;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

public class CommScreen extends TardisScreen {

    public CommScreen(ClientTardis tardis) {
        super(Text.literal(""), tardis);
    }

    @Override
    protected void init() {
        this.addButton(new PressableTextWidget((width / 2 + 40), (height / 2) - 20,
                this.textRenderer.getWidth("Broadcast to interior"), 20, Text.literal("Broadcast to interior").formatted(Formatting.BOLD), button -> {
            sendPacket();
        }, this.textRenderer));

        super.init();
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        button.active = true; // this whole method is unnecessary bc it defaults to true ( ?? )
    }

    public void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.tardis().getUuid());

        ClientPlayNetworking.send(AITMod.TARDIS_BROADCAST, buf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = width / 2;
        int j = height / 2;
        String text = "Simple Voice Chat Wuz Here";
        int length = this.textRenderer.getWidth(text) / 2;
        int rectWidth = length / 2;
        int rectHeight = length / 2;
        super.render(context, mouseX, mouseY, delta);
        context.fill(i - rectWidth, j - rectHeight, i + rectWidth,j + rectHeight, ColorHelper.Argb.getArgb(255, 255, 150, 0));
        context.drawText(this.textRenderer, text, i - length, j,
                ColorHelper.Argb.getArgb(255, 255, 255, 255),true);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
