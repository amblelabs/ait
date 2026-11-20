package dev.amble.ait.client.screens.svc;

import java.util.ArrayList;
import java.util.List;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.comms.PhoneDirectory;
import dev.amble.ait.core.tardis.comms.RotaryPhones;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockPos;

public class DialScreen extends Screen {

    private static final Identifier TEXTURE = AITMod.id("textures/gui/phone.png");

    private static final int WIDTH = 216;
    private static final int HEIGHT = 138;

    private static final int FIELD_X = 10;
    private static final int FIELD_Y = 11;

    private static final int KEYS_X = 20;
    private static final int KEYS_Y = 37;

    private static final int PAPER_X = 114;
    private static final int PAPER_RIGHT = 188;
    private static final int PAPER_Y = 25;
    private static final int RECENTS_Y = 40;
    private static final int RECENT_HEIGHT = 11;
    private static final int MAX_RECENTS = 6;

    private static final int FIELD_TEXT = 0x202020;
    private static final int FIELD_HINT = 0x4A4A4A;
    private static final int FIELD_WAIT = 0x7A1F1F;
    private static final int INK = 0x3B2A1C;
    private static final int INK_FADED = 0x8A7458;

    private record Recent(String number, String label, boolean outgoing) {
    }

    private final BlockPos pos;
    private final String ownNumber;
    private final List<Recent> recents;
    private final PhoneKeypad keypad = new PhoneKeypad(() -> this.cooldown <= 0, this::dial);

    private int cooldown;

    private DialScreen(BlockPos pos, String ownNumber, int cooldown, List<Recent> recents) {
        super(Text.translatable("screen.ait.phone.title"));
        this.pos = pos;
        this.ownNumber = ownNumber;
        this.cooldown = cooldown;
        this.recents = recents;
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(RotaryPhones.COPY, (client, handler, buf, responseSender) -> {
            String number = buf.readString();
            client.execute(() -> client.keyboard.setClipboard(number));
        });

        ClientPlayNetworking.registerGlobalReceiver(RotaryPhones.OPEN, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String number = buf.readString();
            int cooldown = buf.readVarInt();
            int count = buf.readVarInt();
            List<Recent> recents = new ArrayList<>(count);

            for (int i = 0; i < count; i++)
                recents.add(new Recent(buf.readString(), buf.readString(), buf.readBoolean()));

            client.execute(() -> client.setScreen(new DialScreen(pos, number, cooldown, recents)));
        });
    }

    private int left() {
        return (this.width - WIDTH) / 2;
    }

    private int top() {
        return (this.height - HEIGHT) / 2;
    }

    @Override
    protected void init() {
        int left = this.left();
        int top = this.top();

        this.keypad.addKeys(TEXTURE, left + KEYS_X, top + KEYS_Y, this::addDrawableChild);

        int limit = Math.min(this.recents.size(), MAX_RECENTS);

        for (int i = 0; i < limit; i++) {
            Recent recent = this.recents.get(i);
            Text text = Text.literal((recent.outgoing() ? "> " : "< ") + PhoneDirectory.format(recent.number()));
            RecentRow row = this.addDrawableChild(new RecentRow(left + PAPER_X, top + RECENTS_Y + i * RECENT_HEIGHT,
                    PAPER_RIGHT - PAPER_X, text, pressed -> this.keypad.setDigits(recent.number()), () -> this.removeRecent(recent)));

            Text tooltip = Text.translatable(recent.outgoing() ? "screen.ait.phone.outgoing" : "screen.ait.phone.incoming");

            if (!recent.label().isBlank())
                tooltip = Text.literal(recent.label()).append("\n").append(tooltip);

            row.setTooltip(Tooltip.of(tooltip));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.cooldown > 0)
            this.cooldown--;

        this.keypad.tick();
    }

    private void removeRecent(Recent recent) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.pos);
        buf.writeString(recent.number());
        ClientPlayNetworking.send(RotaryPhones.REMOVE_RECENT, buf);

        this.recents.remove(recent);
        RotaryClicks.click(0.9f);
        this.clearAndInit();
    }

    private void dial(String number) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.pos);
        buf.writeString(number);
        ClientPlayNetworking.send(RotaryPhones.DIAL, buf);

        this.close();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.keypad.charTyped(chr) || super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.keypad.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int left = this.left();
        int top = this.top();

        context.drawTexture(TEXTURE, left, top, 0, 0, WIDTH, HEIGHT, 256, 256);
        this.renderField(context, left + FIELD_X, top + FIELD_Y);
        this.renderPaper(context, left, top);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderField(DrawContext context, int x, int y) {
        this.keypad.renderField(context, this.textRenderer, TEXTURE, x, y, this.cooldown <= 0, FIELD_TEXT, FIELD_HINT);

        if (this.cooldown <= 0)
            return;

        Text wait = Text.translatable("screen.ait.phone.wait", (this.cooldown + 19) / 20);
        context.drawText(this.textRenderer, wait, x + PhoneKeyButton.FIELD_WIDTH - PhoneKeyButton.FIELD_PADDING - this.textRenderer.getWidth(wait),
                PhoneKeypad.textY(this.textRenderer, y), FIELD_WAIT, false);
    }

    private void renderPaper(DrawContext context, int left, int top) {
        Text own = Text.translatable("screen.ait.phone.own_number", PhoneDirectory.format(this.ownNumber));
        context.drawText(this.textRenderer, own, left + PAPER_RIGHT - this.textRenderer.getWidth(own), top + PAPER_Y, INK, false);

        if (this.recents.isEmpty())
            context.drawText(this.textRenderer, Text.translatable("screen.ait.phone.no_recent"), left + PAPER_X,
                    top + RECENTS_Y, INK_FADED, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static final class RecentRow extends ButtonWidget {

        private static final String REMOVE = "X";
        private static final int REMOVE_WIDTH = 8;
        private static final int REMOVE_COLOR = 0xA02020;

        private final Runnable onRemove;

        private RecentRow(int x, int y, int width, Text message, PressAction action, Runnable onRemove) {
            super(x, y, width, RECENT_HEIGHT, message, action, DEFAULT_NARRATION_SUPPLIER);
            this.onRemove = onRemove;
        }

        private boolean overRemove(double mouseX) {
            return mouseX >= this.getX() + this.width - REMOVE_WIDTH;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (this.overRemove(mouseX)) {
                this.onRemove.run();
                return;
            }

            super.onClick(mouseX, mouseY);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            boolean hovered = this.isHovered();
            boolean removing = hovered && this.overRemove(mouseX);
            Text text = hovered && !removing ? this.getMessage().copy().formatted(Formatting.UNDERLINE) : this.getMessage();
            int textWidth = hovered ? this.width - REMOVE_WIDTH : this.width;

            context.drawText(font, Language.getInstance().reorder(font.trimToWidth(text, textWidth)), this.getX(), this.getY() + 1, INK, false);

            if (!hovered)
                return;

            Text remove = removing ? Text.literal(REMOVE).formatted(Formatting.BOLD) : Text.literal(REMOVE);
            context.drawText(font, remove, this.getX() + this.width - REMOVE_WIDTH + (REMOVE_WIDTH - font.getWidth(remove)) / 2,
                    this.getY() + 1, REMOVE_COLOR, false);
        }
    }
}
