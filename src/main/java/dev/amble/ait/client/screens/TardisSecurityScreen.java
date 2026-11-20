package dev.amble.ait.client.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.screens.widget.SwitcherManager;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.compat.svc.CommsHandler;
import dev.amble.ait.core.tardis.handler.LandingPadHandler;
import dev.amble.ait.core.tardis.handler.ServerAlarmHandler;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.handler.permissions.PermissionHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.Loyalty;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class TardisSecurityScreen extends ConsoleScreen {
    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/security_menu.png");

    private static final int PANEL_WIDTH = 216;
    private static final int PANEL_HEIGHT = 138;
    private static final int BORDER = 9;

    private static final int PADDING = 14;
    private static final int ROW_START = 14;
    private static final int ROW_SPACING = 14;
    private static final int VALUE_COLOR = 0xffA500;
    private static final int INFO_COLOR = 0xadcaf7;

    int bgHeight = 180;
    int bgWidth = 256;
    int left, top;
    int choicesCount = 0;
    private final Screen parent;
    private final List<Row> rows = new ArrayList<>();
    private TextFieldWidget landingCodeInput;

    private record Row(Text label, int y, Supplier<Text> value) {
    }

    public TardisSecurityScreen(ClientTardis tardis, BlockPos console, Screen parent) {
        super(Text.translatable("screen.ait.security.title"), tardis, console);
        this.parent = parent;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.top = (this.height - this.bgHeight) / 2;
        this.left = (this.width - this.bgWidth) / 2;
        this.createButtons();

        super.init();
    }

    @Override
    public boolean canCloseWithKey() {
        return !this.landingCodeInput.isActive();
    }

    private void createButtons() {
        choicesCount = 0;
        this.rows.clear();

        createTextButton(Text.translatable("screen.ait.interiorsettings.back"),
                (button -> backToExteriorChangeScreen()), null);
        createTextButton(Text.translatable("screen.ait.security.leave_behind"), (button -> toggleLeaveBehind()),
                () -> onOff(this.tardis().travel().leaveBehind().get()));
        createTextButton(Text.translatable("screen.ait.security.hostile_alarms"), (button -> toggleHostileAlarms()),
                () -> onOff(this.tardis().alarm().hostilePresence().get()));
        createTextButton(Text.translatable("screen.ait.security.minimum_loyalty"), (button -> changeMinimumLoyalty()),
                () -> getMinimumLoyalty(this.tardis()).text());
        createTextButton(Text.translatable("screen.ait.security.receive_distress_calls"), (button -> receiveDistressCalls()),
                () -> onOff(this.tardis().stats().receiveCalls().get()));

        if (DependencyChecker.hasVoiceChat())
            createTextButton(Text.translatable("screen.ait.security.call_tracing"), (button -> toggleCallTracing()),
                    () -> onOff(CommsHandler.of(this.tardis()).callTracing().get()));

        int codeY = this.rowY(choicesCount) + 6;

        this.landingCodeInput = new TextFieldWidget(this.textRenderer, left + PADDING, codeY, 160, this.textRenderer.fontHeight + 4,
                Text.translatable("message.ait.landing_code"));
        this.addButton(new PressableTextWidget(left + PADDING + 166, codeY,
                this.textRenderer.getWidth("✓"), this.textRenderer.fontHeight + 4, Text.literal("✓").formatted(Formatting.BOLD), button -> {
            updateLandingCode();
        }, this.textRenderer));

        this.landingCodeInput.setMaxLength(50);
        this.landingCodeInput.setDrawsBackground(true);
        this.landingCodeInput.setVisible(true);

        if(this.tardis().landingPad().code().get().isBlank())
            this.landingCodeInput.setPlaceholder(Text.translatable("message.ait.enter_landing_code"));
        else
            this.landingCodeInput.setText(this.tardis().landingPad().code().get());

        this.addSelectableChild(this.landingCodeInput);
    }

    private int rowY(int index) {
        return this.top + ROW_START + ROW_SPACING * index;
    }

    private static Text onOff(boolean value) {
        return Text.translatable(value ? "screen.ait.monitor.on" : "screen.ait.monitor.off");
    }

    private void receiveDistressCalls() {
        boolean bool = !this.tardis().stats().receiveCalls().get();
        this.tardis().stats().receiveCalls().set(bool);
        SwitcherManager.sync(this.tardis(), buf -> buf.writeBoolean(bool), StatsHandler.SHOULD_RECEIVE_CALLS);
    }

    private void toggleCallTracing() {
        CommsHandler comms = CommsHandler.of(this.tardis());
        boolean bool = !comms.callTracing().get();

        comms.callTracing().set(bool);
        SwitcherManager.sync(this.tardis(), buf -> buf.writeBoolean(bool), CommsHandler.CALL_TRACING);
    }

    private void toggleLeaveBehind() {
        boolean bool = !this.tardis().travel().leaveBehind().get();
        this.tardis().travel().leaveBehind().set(bool);
        SwitcherManager.sync(this.tardis(), buf -> buf.writeBoolean(bool), TravelHandlerBase.TOGGLE_LEAVE_BEHIND);
    }

    private void changeMinimumLoyalty() {
        ClientTardis tardis = this.tardis();
        PermissionHandler.p19Loyalty(tardis, getMinimumLoyalty(tardis).next());
    }

    private static Loyalty.Type getMinimumLoyalty(ClientTardis tardis) {
        return tardis.<PermissionHandler>handler(TardisComponent.Id.PERMISSIONS).p19Loyalty().get();
    }

    private void toggleHostileAlarms() {
        boolean bool = !this.tardis().alarm().hostilePresence().get();
        this.tardis().alarm().hostilePresence().set(bool);
        SwitcherManager.sync(this.tardis(), buf -> buf.writeBoolean(bool), ServerAlarmHandler.TOGGLE_HOSTILE_ALARMS);
    }

    private void updateLandingCode() {
        String input = this.landingCodeInput.getText();

        this.tardis().landingPad().code().set(input);
        SwitcherManager.sync(this.tardis(), buf -> buf.writeString(input), LandingPadHandler.LANDING_CODE);
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        button.active = true; // this whole method is unnecessary bc it defaults to true ( ?? )
    }

    private void createTextButton(Text text, ButtonWidget.PressAction onPress, Supplier<Text> value) {
        int y = this.rowY(choicesCount);

        this.addButton(new PressableTextWidget(left + PADDING, y, this.textRenderer.getWidth(text), 10, text,
                onPress, this.textRenderer));

        if (value != null)
            this.rows.add(new Row(text, y, value));

        choicesCount++;
    }

    public void backToExteriorChangeScreen() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context);

        for (Row row : this.rows) {
            int x = left + PADDING + this.textRenderer.getWidth(row.label());
            context.drawText(this.textRenderer, Text.empty().append(": ").append(row.value().get()), x, row.y(), VALUE_COLOR, false);
        }

        int infoY = top + bgHeight - BORDER - 26;

        context.drawText(this.textRenderer, Text.translatable("message.ait.date_created"),
                left + PADDING, infoY, INFO_COLOR, false);
        context.drawText(this.textRenderer, Text.literal(this.tardis().stats().getCreationString()),
                left + PADDING, infoY + 11, INFO_COLOR, false);

        this.landingCodeInput.render(context, mouseX, mouseY, delta);
        this.landingCodeInput.setEditableColor(this.landingCodeInput.isSelected() || !this.landingCodeInput.getText().isBlank() ? 0xffffff: 0x545454);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context) {
        int innerSourceWidth = PANEL_WIDTH - BORDER * 2;
        int innerSourceHeight = PANEL_HEIGHT - BORDER * 2;
        int innerWidth = bgWidth - BORDER * 2;
        int innerHeight = bgHeight - BORDER * 2;
        int right = left + bgWidth - BORDER;
        int bottom = top + bgHeight - BORDER;

        context.drawTexture(TEXTURE, left, top, 0, 0, BORDER, BORDER, 256, 256);
        context.drawTexture(TEXTURE, right, top, PANEL_WIDTH - BORDER, 0, BORDER, BORDER, 256, 256);
        context.drawTexture(TEXTURE, left, bottom, 0, PANEL_HEIGHT - BORDER, BORDER, BORDER, 256, 256);
        context.drawTexture(TEXTURE, right, bottom, PANEL_WIDTH - BORDER, PANEL_HEIGHT - BORDER, BORDER, BORDER, 256, 256);

        context.drawTexture(TEXTURE, left + BORDER, top, innerWidth, BORDER, BORDER, 0, innerSourceWidth, BORDER, 256, 256);
        context.drawTexture(TEXTURE, left + BORDER, bottom, innerWidth, BORDER, BORDER, PANEL_HEIGHT - BORDER, innerSourceWidth, BORDER, 256, 256);
        context.drawTexture(TEXTURE, left, top + BORDER, BORDER, innerHeight, 0, BORDER, BORDER, innerSourceHeight, 256, 256);
        context.drawTexture(TEXTURE, right, top + BORDER, BORDER, innerHeight, PANEL_WIDTH - BORDER, BORDER, BORDER, innerSourceHeight, 256, 256);

        for (int offset = 0; offset < innerHeight; offset += innerSourceHeight) {
            int height = Math.min(innerSourceHeight, innerHeight - offset);

            context.drawTexture(TEXTURE, left + BORDER, top + BORDER + offset, innerWidth, height,
                    BORDER, BORDER, innerSourceWidth, height, 256, 256);
        }
    }
}
