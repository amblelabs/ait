package dev.amble.ait.client.screens.interior;

import static dev.amble.ait.core.tardis.handler.InteriorChangingHandler.CHANGE_DESKTOP;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.api.TardisClientEvents;
import dev.amble.ait.client.screens.ConsoleScreen;
import dev.amble.ait.client.screens.SonicSettingsScreen;
import dev.amble.ait.client.screens.TardisSecurityScreen;
import dev.amble.ait.client.screens.widget.SwitcherManager;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;

@Environment(EnvType.CLIENT)
public class InteriorSettingsScreen extends ConsoleScreen {
    private static final Identifier BACKGROUND = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/interior_settings.png");
    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/interior_settings.png");
    private static final Identifier MISSING_PREVIEW = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/presets/missing_preview.png");
    private final List<ButtonWidget> buttons = Lists.newArrayList();
    int bgHeight = 166;
    int bgWidth = 256;
    int left, top;
    private int tickForSpin = 0;
    public int choicesCount = 0;
    private final Screen parent;
    private TardisDesktopSchema selectedDesktop;
    private SwitcherManager.ModeManager modeManager;

    public InteriorSettingsScreen(ClientTardis tardis, BlockPos console, Screen parent) {
        super(Text.translatable("screen.ait.interiorsettings.title"), tardis, console);

        this.parent = parent;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.modeManager = new SwitcherManager.ModeManager(this.tardis());
        this.selectedDesktop = tardis().getDesktop().getSchema();
        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;
        this.createButtons();

        super.init();
    }

    private void sendCachePacket() {
        if (this.console == null)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.tardis().getUuid());
        buf.writeBlockPos(this.console);

        ClientPlayNetworking.send(TardisDesktop.CACHE_CONSOLE, buf);
        this.close();
    }

    private void createCompatButtons() { }

    private void createButtons() {
        choicesCount = 0;
        this.buttons.clear();

        createTextButton(Text.translatable("screen.ait.interiorsettings.cacheconsole")
                .formatted(this.console != null ? Formatting.WHITE : Formatting.GRAY), button -> sendCachePacket());
        createTextButton(Text.translatable("screen.ait.security.button"), (button -> toSecurityScreen()));
        createTextButton(Text.translatable("screen.ait.sonic.button")
                .formatted(tardis().sonic().getConsoleSonic() != null ? Formatting.WHITE : Formatting.GRAY), button -> {
                    if (tardis().sonic().getConsoleSonic() != null)
                        toSonicScreen();
                });

        this.createCompatButtons();
        TardisClientEvents.SETTINGS_SETUP.invoker().onSetup(this);

        this.addButton(new PressableTextWidget((width / 2 + 30), (height / 2 + 64),
                this.textRenderer.getWidth("<"), 10, Text.literal(""), button -> this.modeManager.get().previous(), this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 105), (height / 2 + 64),
                this.textRenderer.getWidth(">"), 10, Text.literal(""), button -> this.modeManager.get().next(), this.textRenderer));
        Text applyInteriorText = Text.translatable("screen.ait.monitor.apply");
        this.addButton(new PressableTextWidget((width / 2 + 55), (height / 2 + 64),
                this.textRenderer.getWidth(applyInteriorText), 10, Text.literal(""), button -> this.modeManager.get().sync(this.tardis()), this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 30), (height / 2 + 8), this.textRenderer.getWidth("<"), 10,
                Text.literal(""), button -> {
                    previousDesktop();
                }, this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 105), (height / 2 + 8), this.textRenderer.getWidth(">"), 10,
                Text.literal(""), button -> {
                    nextDesktop();
                }, this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 55), (height / 2 + 8),
                this.textRenderer.getWidth(applyInteriorText), 20, Text.translatable("screen.ait.monitor.apply").formatted(Formatting.BOLD), button -> applyDesktop(), this.textRenderer));
        Text exteriorSettingsText = Text.translatable("screen.ait.monitor.gear_icon");
        this.addButton(new PressableTextWidget((width / 2 - 6), (height / 2 + 57),
                this.textRenderer.getWidth(exteriorSettingsText), 10,
                Text.literal("").formatted(Formatting.BOLD).formatted(Formatting.WHITE),
                button -> backToExteriorChangeScreen(), this.textRenderer));


        this.addButton(new PressableTextWidget((width / 2 + 84), (height / 2 + 33),
                this.textRenderer.getWidth("<"), 10, Text.literal(""), button -> this.modeManager.previous(), this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 105), (height / 2 + 33),
                this.textRenderer.getWidth(">"), 10, Text.literal(""), button -> this.modeManager.next(), this.textRenderer));
    }

    private void toSonicScreen() {
        MinecraftClient.getInstance().setScreen(new SonicSettingsScreen(this.tardis(), this.console, this));
    }

    public <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        button.active = true; // this whole method is unnecessary bc it defaults to true ( ?? )
        this.buttons.add((ButtonWidget) button);
    }

    public PressableTextWidget createTextButton(Text text, ButtonWidget.PressAction onPress) {
        return this.createAnyButton(text, PressableTextWidget::new, onPress);
    }

    public <T extends ButtonWidget> T initAnyButton(Text text, ButtonCreator<T> creator,
            ButtonWidget.PressAction onPress) {
        return creator.create((int) (left + (bgWidth * 0.06f)), (int) (top + (bgHeight * (0.1f * (choicesCount + 1)))),
                this.textRenderer.getWidth(text), 10, text, onPress, this.textRenderer);
    }

    public <T extends ButtonWidget> T initAnyDynamicButton(Function<T, Text> text, DynamicButtonCreator<T> creator,
            ButtonWidget.PressAction onPress) {
        return creator.create((int) (left + (bgWidth * 0.06f)), (int) (top + (bgHeight * (0.1f * (choicesCount + 1)))),
                this.textRenderer.getWidth(Text.empty()), 10, text, onPress, this.textRenderer);
    }

    public <T extends ButtonWidget> T createAnyButton(Text text, ButtonCreator<T> creator,
            ButtonWidget.PressAction onPress) {
        T result = this.initAnyButton(text, creator, onPress);

        this.addButton(result);
        choicesCount++;

        return result;
    }

    public <T extends ButtonWidget> T createAnyDynamicButton(Function<T, Text> text, DynamicButtonCreator<T> creator,
            ButtonWidget.PressAction onPress) {
        T result = this.initAnyDynamicButton(text, creator, onPress);

        this.addButton(result);
        choicesCount++;

        return result;
    }

    public void backToExteriorChangeScreen() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    public void toSecurityScreen() {
        MinecraftClient.getInstance().setScreen(new TardisSecurityScreen(tardis(), this.console, this));
    }

    final int UV_BASE = 160;
    final int UV_INCREMENT = 19;

    int calculateUvOffsetForRange(int progress) {
        int rangeProgress = progress % 20;
        return (rangeProgress / 5) * UV_INCREMENT;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = (this.width - this.bgWidth) / 2;
        int j = ((this.height) - this.bgHeight) / 2;
        this.renderDesktop(context);
        this.drawBackground(context); // the grey backdrop
        context.getMatrices().push();
        int x = (left + 79);
        int y = (top + 59);
        context.getMatrices().translate(0, 0, 0f);
        context.getMatrices().pop();

        // TODO: this is a fucking nightmare
        int buttonIndex = DependencyChecker.hasGravity() ? 4 : 3;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 93, 166, 20,
                    12);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 93, 178, 20,
                    12);

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 113, 166, 20,
                    12);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 113, 178, 20,
                    12);

        // apply (HUM)
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 11, this.buttons.get(buttonIndex).getY() - 3, 133, 166, 53,
                    12);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 11, this.buttons.get(buttonIndex).getY() - 3, 133, 178, 53,
                    12);

        // arrow (Interior)
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 5, 0, 166, 20,
                    20);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 5, 0, 186, 20,
                    20);
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 5, 20, 166, 20,
                    20);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 5, 20, 186, 20,
                    20);

        // apply (Interior)

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 11, this.buttons.get(buttonIndex).getY() - 5, 40, 166, 53,
                    20);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 11, this.buttons.get(buttonIndex).getY() - 5, 40, 186, 53,
                    20);

        // back to main monitor menu

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 5, 186, 166, 53,
                    20);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 5, 186, 186, 53,
                    20);

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 93, 166, 20,
                    12);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 93, 178, 20,
                    12);

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 113, 166, 20,
                    12);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX() - 7, this.buttons.get(buttonIndex).getY() - 3, 113, 178, 20,
                    12);


        if (tardis() == null)
            return;

        // Fuel
        context.drawTexture(TEXTURE, i + 16, j + 144, 0,
                this.tardis().getFuel() > (FuelHandler.TARDIS_MAX_FUEL / 4) ? 225 : 234,
                (int) (85 * this.tardis().getFuel() / FuelHandler.TARDIS_MAX_FUEL), 9);


        // fuel markers @TODO come back and actually do the rest of it with the halves
        // and the red
        // parts
        // too

        // Flight Progress
        int progress = this.tardis().travel().getDurationAsPercentage();

        for (int index = 0; index < 5; index++) {
            int rangeStart = index * 19;
            int rangeEnd = (index + 1) * 19;

            int uvOffset;
            if (progress >= rangeStart && progress <= rangeEnd) {
                uvOffset = calculateUvOffsetForRange(progress);
            } else if (progress >= rangeEnd) {
                uvOffset = 57;
            } else {
                uvOffset = UV_BASE;
            }

            context.drawTexture(TEXTURE, i + 11 + (index * 19), j + 113,
                    this.tardis().travel().getState() == TravelHandlerBase.State.FLIGHT
                            ? progress >= 100 ? 76 : uvOffset
                            : UV_BASE,
                    206, 19, 19);
        }


        this.renderCurrentMode(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(BACKGROUND, left, top, 0, 0, bgWidth, bgHeight);
    }

    private void renderDesktop(DrawContext context) {
        if (this.selectedDesktop == null)
            return;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 15f);
        context.drawCenteredTextWithShadow(this.textRenderer, this.selectedDesktop.name(),
                (int) (left + (bgWidth * 0.77f)), (int) (top + (bgHeight * 0.080f)), 0xffffff);
        context.getMatrices().pop();

        context.getMatrices().push();
        context.drawTexture(
                doesTextureExist(this.selectedDesktop.previewTexture().texture())
                        ? this.selectedDesktop.previewTexture().texture()
                        : MISSING_PREVIEW,
                left + 151, top + 10, 95, 95, 0, 0, this.selectedDesktop.previewTexture().width * 2,
                this.selectedDesktop.previewTexture().height * 2, this.selectedDesktop.previewTexture().width * 2,
                this.selectedDesktop.previewTexture().height * 2);

        context.getMatrices().pop();
    }

    private void renderCurrentMode(DrawContext context) {
        Nameable current = this.modeManager.get().get();

        Text modeText = Text.literal(this.modeManager.get().name().toUpperCase());
        context.drawText(this.textRenderer, modeText,
                (width / 2 + 50) - this.textRenderer.getWidth(modeText) / 2,
                height / 2 + 32, 0xffffff, true);
        Text currentText = Text.literal(current .name().toUpperCase());
        context.drawText(this.textRenderer, currentText, (int) (left + (bgWidth * 0.78f)) - this.textRenderer.getWidth(currentText) / 2,
                (int) (top + (bgHeight * 0.792f)), 0xffffff, true);
    }

    private void applyDesktop() {
        if (this.selectedDesktop == null)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(tardis().getUuid());
        buf.writeIdentifier(this.selectedDesktop.id());

        ClientPlayNetworking.send(CHANGE_DESKTOP, buf);

        MinecraftClient.getInstance().setScreen(null);
    }

    private static TardisDesktopSchema nextDesktop(TardisDesktopSchema current) {
        List<TardisDesktopSchema> list = DesktopRegistry.getInstance().toList();

        int idx = list.indexOf(current);
        idx = (idx + 1) % list.size();
        return list.get(idx);
    }

    private void nextDesktop() {
        this.selectedDesktop = nextDesktop(this.selectedDesktop);

        if (!isCurrentUnlocked() || this.selectedDesktop == DesktopRegistry.DEFAULT_CAVE)
            nextDesktop(); // ooo incursion crash
    }

    private static TardisDesktopSchema previousDesktop(TardisDesktopSchema current) {
        List<TardisDesktopSchema> list = DesktopRegistry.getInstance().toList();

        int idx = list.indexOf(current);
        idx = (idx - 1 + list.size()) % list.size();
        return list.get(idx);
    }

    private void previousDesktop() {
        this.selectedDesktop = previousDesktop(this.selectedDesktop);

        if (!isCurrentUnlocked() || this.selectedDesktop == DesktopRegistry.DEFAULT_CAVE)
            previousDesktop(); // ooo incursion crash
    }

    public static boolean doesTextureExist(Identifier id) {
        return MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
    }

    private boolean isCurrentUnlocked() {
        return this.tardis().isUnlocked(this.selectedDesktop);
    }

    @FunctionalInterface
    public interface ButtonCreator<T extends ButtonWidget> {
        T create(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress,
                TextRenderer textRenderer);
    }

    @FunctionalInterface
    public interface DynamicButtonCreator<T extends ButtonWidget> {
        T create(int x, int y, int width, int height, Function<T, Text> text, ButtonWidget.PressAction onPress,
                TextRenderer textRenderer);
    }
}
