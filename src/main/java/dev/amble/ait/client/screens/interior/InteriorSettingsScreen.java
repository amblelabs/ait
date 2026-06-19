package dev.amble.ait.client.screens.interior;

import static dev.amble.ait.core.tardis.handler.InteriorChangingHandler.CHANGE_DESKTOP;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.api.tardis.TardisClientEvents;
import dev.amble.ait.client.models.exteriors.BedrockExteriorModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.screens.ConsoleScreen;
import dev.amble.ait.client.screens.SaveLoadInteriorScreen;
import dev.amble.ait.client.screens.SonicSettingsScreen;
import dev.amble.ait.client.screens.TardisSecurityScreen;
import dev.amble.ait.client.screens.widget.AnimationScrubberWidget;
import dev.amble.ait.client.screens.widget.IconButtonWidget;
import dev.amble.ait.client.screens.widget.SwitcherManager;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.vortex.reference.VortexReference;
import dev.amble.ait.data.hum.Hum;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.category.ClassicCategory;
import dev.amble.ait.data.schema.exterior.category.PoliceBoxCategory;
import dev.amble.ait.registry.impl.CategoryRegistry;
import dev.amble.ait.registry.impl.DesktopRegistry;

@Environment(EnvType.CLIENT)
public class InteriorSettingsScreen extends ConsoleScreen {
    private static final Identifier BACKGROUND = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/interior_settings.png");
    private static final Identifier ANIM_BACKGROUND = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/interior_settings_anim.png");
    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/interior_settings.png");
    private static final Identifier MISSING_PREVIEW = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/presets/missing_preview.png");
    private static final int PREVIEW_X_OFFSET = 151;
    private static final int PREVIEW_Y_OFFSET = 10;
    private static final int PREVIEW_SIZE = 95;
    private final List<ButtonWidget> buttons = Lists.newArrayList();
    int bgHeight = 166;
    int bgWidth = 256;
    int left, top;
    private int tickForSpin = 0;
    public int choicesCount = 0;
    private final Screen parent;
    private TardisDesktopSchema selectedDesktop;
    private SwitcherManager.ModeManager modeManager;
    private final int APPLY_BUTTON_WIDTH = 53;
    private final int APPLY_BUTTON_HEIGHT = 20;
    private final int APPLY_BAR_BUTTON_WIDTH = 53;
    private final int APPLY_BAR_BUTTON_HEIGHT = 12;
    private final int SMALL_ARROW_BUTTON_WIDTH = 20;
    private final int SMALL_ARROW_BUTTON_HEIGHT = 12;
    private final int BIG_ARROW_BUTTON_WIDTH = 20;
    private final int BIG_ARROW_BUTTON_HEIGHT = 20;
    private final int MAIN_SETTINGS_BUTTON_WIDTH = 20;
    private final int MAIN_SETTINGS_BUTTON_HEIGHT = 20;
    private BlockPos console;

    private AnimationScrubberWidget timeline;
    private IconButtonWidget playButton;
    private IconButtonWidget stopButton;
    private IconButtonWidget muteButton;
    private boolean previewMuted;
    private TardisAnimation previewBase;
    private TardisAnimation previewAnim;
    private Identifier previewAnimId;
    private int previewTicks;
    private int previewMax = 1;
    private SoundInstance previewSound;
    private boolean humSuppressed;

    public InteriorSettingsScreen(ClientTardis tardis, BlockPos console, Screen parent) {
        super(Text.translatable("screen." + AITMod.MOD_ID + ".interiorsettings.title"), tardis, console);
        this.parent = parent;
        this.console = console;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.modeManager = new SwitcherManager.ModeManager(this.tardis());
        this.selectedDesktop = tardis().getDesktop().getSchema();

        if (this.selectedDesktop == null)
            this.nextDesktop();

        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;
        this.createButtons();
        this.createPreviewWidgets();

        super.init();
    }

    private void createPreviewWidgets() {
        this.timeline = this.addDrawableChild(new AnimationScrubberWidget(
                this.left + 152, this.top + 78, 81, 5, this::scrubTo));
        this.timeline.visible = false;

        this.playButton = this.addDrawableChild(new IconButtonWidget(
                this.left + 238, this.top + 127, 6, IconButtonWidget.Icon.PLAY, this::playPreviewSound));
        this.stopButton = this.addDrawableChild(new IconButtonWidget(
                this.left + 238, this.top + 135, 6, IconButtonWidget.Icon.STOP, this::stopPreviewSound));

        this.muteButton = this.addDrawableChild(new IconButtonWidget(
                this.left + 235, this.top + 77, 7, IconButtonWidget.Icon.SOUND_ON, this::toggleMute));
        this.muteButton.visible = false;
    }

    private boolean isAnimMode() {
        return this.modeManager != null && this.modeManager.get().get() instanceof TardisAnimation;
    }

    private boolean isVortexMode() {
        return this.modeManager != null && this.modeManager.get().get() instanceof VortexReference;
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

        boolean showSonicButton = console != null && MinecraftClient.getInstance().world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlock
                && consoleBlock.getSonicScrewdriver() != null && !consoleBlock.getSonicScrewdriver().isEmpty();

        createTextButton(Text.translatable("screen.ait.sonic.button")
                .formatted(showSonicButton ? Formatting.WHITE : Formatting.GRAY), button -> {
                    if (showSonicButton)
                        toSonicScreen();
                });

        /*createTextButton(Text.translatable("screen.ait.loadsaveinterior.button")
                .formatted(Formatting.WHITE), button -> {
                toLoadSaveInteriorScreen();
        });*/

        this.createCompatButtons();
        TardisClientEvents.SETTINGS_SETUP.invoker().onSetup(this);

        // arrow - hum/misc screen - left
        this.addButton(new PressableTextWidget((width / 2 + 23), (height / 2 + 61),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> this.modeManager.get().previous(), this.textRenderer));

        // arrow - hum/misc screen - right
        this.addButton(new PressableTextWidget((width / 2 + 98), (height / 2 + 61),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> this.modeManager.get().next(), this.textRenderer));

        // apply (HUM)
        this.addButton(new PressableTextWidget((width / 2 + 44), (height / 2 + 61),
                APPLY_BAR_BUTTON_WIDTH, APPLY_BAR_BUTTON_HEIGHT, Text.empty(), button -> this.modeManager.get().sync(this.tardis()), this.textRenderer));

        // arrows (Interior)
        this.addButton(new PressableTextWidget((width / 2 + 23), (height / 2 + 3), BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT,
                Text.empty(), button -> {
                    previousDesktop();
                }, this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 98), (height / 2 + 3), BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT,
                Text.empty(), button -> {
                    nextDesktop();
                }, this.textRenderer));

        // apply (Interior)
        MutableText applyInteriorText = Text.translatable("screen.ait.monitor.apply");
        this.addDrawable(new TextWidget((width / 2 + 44), (height / 2 + 3),
                APPLY_BUTTON_WIDTH, APPLY_BUTTON_HEIGHT, applyInteriorText.formatted(Formatting.BOLD), this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 44), (height / 2 + 3),
                APPLY_BUTTON_WIDTH, APPLY_BUTTON_HEIGHT, Text.empty(), button -> applyDesktop(), this.textRenderer));

        // back to main monitor menu
        this.addButton(new PressableTextWidget((width / 2 - 13), (height / 2 + 52),
                MAIN_SETTINGS_BUTTON_WIDTH, MAIN_SETTINGS_BUTTON_HEIGHT,
                Text.empty(),
                button -> backToExteriorChangeScreen(), this.textRenderer));


        // arrows (HUM) mode selector
        this.addButton(new PressableTextWidget((width / 2 + 77), (height / 2 + 30),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> this.modeManager.previous(), this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 98), (height / 2 + 30),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> this.modeManager.next(), this.textRenderer));
    }

    private void toSonicScreen() {
        MinecraftClient.getInstance().setScreen(new SonicSettingsScreen(this.tardis(), this.console, this));
    }

    private void toLoadSaveInteriorScreen() {
        MinecraftClient.getInstance().setScreen(new SaveLoadInteriorScreen(this.tardis(), this.console, this));
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
        int rangeProgress = progress % 19;
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

        // arrow buttons (hum/misc screen)
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 93, 166,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 93, 178,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 113, 166,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 113, 178,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);

        // apply bar button (hum/misc screen)
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 133, 166,
                    APPLY_BAR_BUTTON_WIDTH, APPLY_BAR_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 133, 178,
                    APPLY_BAR_BUTTON_WIDTH, APPLY_BAR_BUTTON_HEIGHT);

        // arrow buttons (interior)
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 0, 166,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 0, 186,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 20, 166,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 20, 186,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);

        // apply button (interior)
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 40, 166,
                    APPLY_BUTTON_WIDTH, APPLY_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 40, 186,
                    APPLY_BUTTON_WIDTH, APPLY_BUTTON_HEIGHT);

        // back to main monitor menu button
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 186, 166,
                    MAIN_SETTINGS_BUTTON_WIDTH, MAIN_SETTINGS_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 186, 186,
                    MAIN_SETTINGS_BUTTON_WIDTH, MAIN_SETTINGS_BUTTON_HEIGHT);

        // arrow buttons (hum/misc screen) - mode selector
        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 93, 166,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 93, 178,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);

        buttonIndex++;
        if (!this.buttons.get(buttonIndex).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 113, 166,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(buttonIndex).getX(), this.buttons.get(buttonIndex).getY(), 113, 178,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);


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
                uvOffset = 76;
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

        boolean anim = this.isAnimMode();
        boolean hasSound = this.currentPreviewSound() != null;

        if (this.timeline != null)
            this.timeline.visible = anim;
        if (this.playButton != null)
            this.playButton.visible = hasSound && !anim;
        if (this.stopButton != null)
            this.stopButton.visible = hasSound && !anim;
        if (this.muteButton != null) {
            this.muteButton.visible = anim;
            this.muteButton.setIcon(this.previewMuted
                    ? IconButtonWidget.Icon.SOUND_OFF : IconButtonWidget.Icon.SOUND_ON);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(this.isAnimMode() ? ANIM_BACKGROUND : BACKGROUND, left, top, 0, 0, bgWidth, bgHeight);
    }

    private void renderDesktop(DrawContext context) {
        if (this.isAnimMode()) {
            this.renderAnimationPreview(context);
            return;
        }

        if (this.isVortexMode()) {
            this.renderVortexPreview(context);
            return;
        }

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
                left + PREVIEW_X_OFFSET, top + PREVIEW_Y_OFFSET, PREVIEW_SIZE, PREVIEW_SIZE, 0, 0,
                this.selectedDesktop.previewTexture().width * 2,
                this.selectedDesktop.previewTexture().height * 2, this.selectedDesktop.previewTexture().width * 2,
                this.selectedDesktop.previewTexture().height * 2);

        context.getMatrices().pop();
    }

    private void renderVortexPreview(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        Object current = this.modeManager.get().get();

        if (client.player == null || !(current instanceof VortexReference ref))
            return;

        int boxX = this.left + PREVIEW_X_OFFSET;
        int boxY = this.top + PREVIEW_Y_OFFSET;

        context.draw();

        Window window = client.getWindow();
        double scale = window.getScaleFactor();
        int fbX = (int) (boxX * scale);
        int fbY = (int) (window.getFramebufferHeight() - (boxY + PREVIEW_SIZE) * scale);
        int fbSize = (int) (PREVIEW_SIZE * scale);

        Matrix4f prevProjection = RenderSystem.getProjectionMatrix();
        VertexSorter prevSorter = RenderSystem.getVertexSorting();

        context.enableScissor(boxX, boxY, boxX + PREVIEW_SIZE, boxY + PREVIEW_SIZE);
        RenderSystem.viewport(fbX, fbY, fbSize, fbSize);
        RenderSystem.setProjectionMatrix(
                new Matrix4f().perspective((float) Math.toRadians(70.0), 1f, 0.05f, 4000f), VertexSorter.BY_DISTANCE);

        MatrixStack modelView = RenderSystem.getModelViewStack();
        modelView.push();
        modelView.loadIdentity();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        float spin = (client.player.age + client.getTickDelta()) / 100f * 360f;

        MatrixStack vortexStack = new MatrixStack();
        vortexStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(spin));
        vortexStack.translate(0, 0, 500);
        ref.toRender().render(vortexStack);

        modelView.pop();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setProjectionMatrix(prevProjection, prevSorter);
        RenderSystem.viewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.disableScissor();
    }

    private void renderAnimationPreview(DrawContext context) {
        if (this.tardis() == null || this.tardis().getExterior() == null)
            return;

        ClientExteriorVariantSchema variant = this.tardis().getExterior().getVariant().getClient();

        if (variant == null)
            return;

        ExteriorModel model = variant.model();

        if (model == null)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        float delta = client.getTickDelta();

        float alpha = 1f;
        Vector3f animPosition = new Vector3f();
        Vector3f animRotation = new Vector3f();
        Vector3f animScale = new Vector3f(1f, 1f, 1f);

        if (this.previewAnim != null) {
            alpha = MathHelper.clamp(this.previewAnim.getAlpha(delta), 0f, 1f);
            animPosition = this.previewAnim.getPosition(delta);
            animRotation = this.previewAnim.getRotation(delta);
            animScale = this.previewAnim.getScale(delta);
        }

        ExteriorCategorySchema category = this.tardis().getExterior().getCategory();
        boolean isPoliceBox = category.equals(CategoryRegistry.getInstance().get(PoliceBoxCategory.REFERENCE))
                || category.equals(CategoryRegistry.getInstance().get(ClassicCategory.REFERENCE));

        float baseScale = isPoliceBox ? 10f : 17f;
        int centerX = this.left + 198;
        int centerY = this.top + (isPoliceBox ? 59 : 48);
        float spin = (client.player == null ? 0 : client.player.age + delta) * 3f;

        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(centerX, centerY, 100f);
        stack.scale(-baseScale, baseScale, baseScale);

        if (model instanceof BedrockExteriorModel)
            stack.translate(0, 1.25f, 0);

        stack.translate(animPosition.x(), animPosition.y(), animPosition.z());
        stack.scale(animScale.x(), animScale.y(), animScale.z());
        stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(spin));
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(animRotation.z()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(animRotation.y()));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(animRotation.x()));

        model.render(stack,
                context.getVertexConsumers().getBuffer(AITRenderLayers.getEntityTranslucentCull(variant.texture())),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, alpha);

        stack.pop();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.humSuppressed && (this.previewSound == null
                || !MinecraftClient.getInstance().getSoundManager().isPlaying(this.previewSound)))
            this.setHumSuppressed(false);

        if (this.tardis() == null)
            return;

        Object current = this.modeManager == null ? null : this.modeManager.get().get();

        if (!(current instanceof TardisAnimation selected)) {
            if (this.previewBase != null)
                this.stopPreviewSound();

            this.previewBase = null;
            this.previewAnim = null;
            this.previewAnimId = null;
            return;
        }

        if (this.previewAnim == null || !selected.id().equals(this.previewAnimId)) {
            this.buildPreview(selected);
            this.playAnimationSound(selected);
        }

        if (this.timeline == null || !this.timeline.isDragging()) {
            if (this.previewAnim.isAged()) {
                this.buildPreview(selected);
                this.playAnimationSound(selected);
            } else {
                this.previewAnim.tick(MinecraftClient.getInstance());
                this.previewTicks = Math.min(this.previewTicks + 1, this.previewMax);
            }

            if (this.timeline != null)
                this.timeline.setProgress(this.previewMax <= 0 ? 0f : (float) this.previewTicks / this.previewMax);
        }
    }

    private void toggleMute() {
        this.previewMuted = !this.previewMuted;

        if (this.previewMuted) {
            this.stopPreviewSound();
        } else if (this.previewBase != null) {
            this.buildPreview(this.previewBase);
            this.playAnimationSound(this.previewBase);
        }
    }

    private void playAnimationSound(TardisAnimation anim) {
        this.stopPreviewSound();

        if (this.previewMuted)
            return;

        SoundEvent sfx = anim.getSound();

        if (sfx == null)
            return;

        this.previewSound = PositionedSoundInstance.master(sfx, 1f, 1f);
        MinecraftClient.getInstance().getSoundManager().play(this.previewSound);
    }

    private void buildPreview(TardisAnimation selected) {
        this.previewBase = selected;
        this.previewAnimId = selected.id();
        this.previewAnim = selected.instantiate();
        this.previewMax = Math.max(1, selected.getMaxDuration());
        this.previewTicks = 0;
    }

    private void scrubTo(float progress) {
        if (this.previewBase == null)
            return;

        int target = MathHelper.clamp(Math.round(progress * this.previewMax), 0, this.previewMax);
        TardisAnimation fresh = this.previewBase.instantiate();
        MinecraftClient client = MinecraftClient.getInstance();

        for (int i = 0; i < target; i++)
            fresh.tick(client);

        this.previewAnim = fresh;
        this.previewTicks = target;
    }

    private static SoundEvent soundOf(Object current) {
        if (current instanceof Hum hum)
            return hum.sound();
        if (current instanceof FlightSound flight)
            return flight.sound();
        if (current instanceof TardisAnimation anim)
            return anim.getSound();

        return null;
    }

    private SoundEvent currentPreviewSound() {
        return this.modeManager == null ? null : soundOf(this.modeManager.get().get());
    }

    private void playPreviewSound() {
        if (this.modeManager == null)
            return;

        Object current = this.modeManager.get().get();
        SoundEvent sfx = soundOf(current);

        if (sfx == null)
            return;

        this.stopPreviewSound();

        if (current instanceof Hum)
            this.setHumSuppressed(true);

        this.previewSound = PositionedSoundInstance.master(sfx, 1f, 1f);
        MinecraftClient.getInstance().getSoundManager().play(this.previewSound);
    }

    private void stopPreviewSound() {
        this.setHumSuppressed(false);

        if (this.previewSound == null)
            return;

        MinecraftClient.getInstance().getSoundManager().stop(this.previewSound);
        this.previewSound = null;
    }

    private void setHumSuppressed(boolean suppressed) {
        if (this.humSuppressed == suppressed)
            return;

        this.humSuppressed = suppressed;
        ClientSoundManager.getHum().setSuppressed(suppressed);
    }

    @Override
    public void removed() {
        this.stopPreviewSound();
        super.removed();
    }

    private void renderCurrentMode(DrawContext context) {
        Nameable current = this.modeManager.get().get();

        Text modeText = Text.literal(this.modeManager.get().name().toUpperCase());
        context.drawText(this.textRenderer, modeText,
                (width / 2 + 50) - this.textRenderer.getWidth(modeText) / 2,
                height / 2 + 32, 0xffffff, true);
        String name = current.name();
        String currentString = Text.translatable(name).getString().toUpperCase();
        context.drawText(this.textRenderer, currentString, (int) (left + (bgWidth * 0.78f)) - this.textRenderer.getWidth(currentString) / 2,
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

        int idx = current == null ? -1 : list.indexOf(current);
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

        int idx = current == null ? -1 : list.indexOf(current);
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
