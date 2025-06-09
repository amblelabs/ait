package dev.amble.ait.client.screens;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.handler.permissions.PermissionHandler;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.Value;
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
    int bgHeight = 138;
    int bgWidth = 216;
    int left, top;
    int choicesCount = 0;
    private final Screen parent;
    private TextFieldWidget landingCodeInput;

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
        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
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

        createTextButton(Text.translatable("screen.ait.interiorsettings.back"),
                (button -> backToExteriorChangeScreen()));
        createTextButton(Text.translatable("screen.ait.security.leave_behind"), (button -> toggleLeaveBehind()));
        createTextButton(Text.translatable("screen.ait.security.hostile_alarms"), (button -> toggleHostileAlarms()));
        createTextButton(Text.translatable("screen.ait.security.minimum_loyalty"), (button -> changeMinimumLoyalty()));
        createTextButton(Text.translatable("screen.ait.security.receive_distress_calls"), (button -> receiveDistressCalls()));

        this.landingCodeInput = new TextFieldWidget(this.textRenderer, (int) (left + (bgWidth * 0.06f)), this.top + 85, 120, this.textRenderer.fontHeight + 4,
                Text.translatable("message.ait.landing_code"));
        this.addButton(new PressableTextWidget((width / 2 + 40), (height / 2 + 18),
                this.textRenderer.getWidth("✓"), 20, Text.literal("✓").formatted(Formatting.BOLD), button -> {
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

    private void receiveDistressCalls() {
        Value<Boolean> receiveCalls = this.tardis().<StatsHandler>handler(TardisComponent.Id.STATS).receiveCalls();
        receiveCalls.set(!receiveCalls.get());
    }

    private void toggleLeaveBehind() {
        this.tardis().travel().leaveBehind().flatMap(value -> !value);
    }

    private void changeMinimumLoyalty() {
        ClientTardis tardis = this.tardis();
        PermissionHandler.p19Loyalty(tardis, getMinimumLoyalty(tardis).next());
    }

    private static Loyalty.Type getMinimumLoyalty(ClientTardis tardis) {
        return tardis.<PermissionHandler>handler(TardisComponent.Id.PERMISSIONS).p19Loyalty().get();
    }

    private void toggleHostileAlarms() {
        this.tardis().alarm().hostilePresence().flatMap(value -> !value);
    }

    private void updateLandingCode() {
        String input = this.landingCodeInput.getText();

        this.tardis().landingPad().code().set(input);
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        button.active = true; // this whole method is unnecessary bc it defaults to true ( ?? )
    }

    // this might be useful, so remember this exists and use it later on
    private void createTextButton(Text text, ButtonWidget.PressAction onPress) {
        this.addButton(new PressableTextWidget((int) (left + (bgWidth * 0.06f)),
                (int) (top + (bgHeight * (0.1f * (choicesCount + 1)))), this.textRenderer.getWidth(text), 10, text,
                onPress, this.textRenderer));

        choicesCount++;
    }

    public void backToExteriorChangeScreen() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context);

        ClientTardis tardis = this.tardis();

        Text onText = Text.translatable("screen.ait.monitor.on");
        Text offText = Text.translatable("screen.ait.monitor.off");


        context.drawText(this.textRenderer,
                Text.empty().append(": ").append(tardis.travel().leaveBehind().get() ? onText : offText),
                (int) (left + (bgWidth * 0.46f)), (int) (top + (bgHeight * (0.1f * 2))), 0xffA500, false);


        context.drawText(this.textRenderer,
                Text.empty().append(": ").append(tardis.alarm().hostilePresence().get() ? onText : offText),
                (int) (left + (bgWidth * 0.48f)), (int) (top + (bgHeight * (0.1f * 3))), 0xffA500, false);


        context.drawText(this.textRenderer,
                Text.literal(": " + getMinimumLoyalty(tardis)),
                (int) (left + (bgWidth * 0.51f)), (int) (top + (bgHeight * (0.1f * 4))), 0xffA500, false);


        context.drawText(this.textRenderer,
                Text.translatable("message.ait.date_created"),
                (int) (left + (bgWidth * 0.06f)),
                (int) (top + (bgHeight * (0.1f * 7.5))), 0xadcaf7, false);


        context.drawText(this.textRenderer,
                Text.literal(tardis.stats().getCreationString()),
                (int) (left + (bgWidth * 0.06f)),
                (int) (top + (bgHeight * (0.1f * 8.5))), 0xadcaf7, false);


        context.drawText(this.textRenderer,
                Text.empty().append(": ").append(this.tardis().<StatsHandler>handler(TardisComponent.Id.STATS).receiveCalls().get() ? onText : offText),
                (int) (left + (bgWidth * 0.7f)), (int) (top + (bgHeight * (0.1f * 5))), 0xffA500, false);

        this.landingCodeInput.render(context, mouseX, mouseY, delta);
        this.landingCodeInput.setEditableColor(this.landingCodeInput.isSelected() || !this.landingCodeInput.getText().isBlank() ? 0xffffff: 0x545454);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
    }
}
