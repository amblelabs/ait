package dev.amble.ait.client.screens;

import com.google.common.collect.Lists;
import dev.amble.ait.AITMod;
import dev.loqor.client.WorldGeometryRenderer;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

public class SaveLoadInteriorScreen extends ConsoleScreen {
    private static final Identifier BACKGROUND = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/security_menu.png");
    private final List<ButtonWidget> buttons = Lists.newArrayList();
    private final int bgHeight = 138;
    private final int bgWidth = 216;
    private int left, top;
    private int choicesCount = 0;
    private final Screen parent;
    private final int APPLY_BAR_BUTTON_WIDTH = 53;
    private final int APPLY_BAR_BUTTON_HEIGHT = 12;
    private final BlockPos console;

    // Rendering fields
    private float rotationX = 30.0f;
    private float rotationY = 0.0f;
    private float zoom = 4.0f;
    private boolean isDragging = false;
    private boolean isPanning = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    // World renderer
    private WorldGeometryRenderer worldRenderer;

    public SaveLoadInteriorScreen(ClientTardis tardis, BlockPos console, Screen parent) {
        super(Text.translatable("screen." + AITMod.MOD_ID + ".loadsaveinterior.title"), tardis, console);
        this.console = console;
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

        // Initialize world renderer
        worldRenderer = new WorldGeometryRenderer(25);

        // Set up orthographic projection
        float aspect = (float) this.width / (float) this.height;
        worldRenderer.setOrthographicProjection(aspect, 50.0f, 1.0f, 5000.0f);

        worldRenderer.markDirty();

        super.init();
    }

    private void createButtons() {
        choicesCount = 0;
        this.buttons.clear();

        this.addButton(new AITPressableTextWidget((int) (left + (bgWidth * 0.139f)), (int) (top + (bgHeight * 0.839f)),
                APPLY_BAR_BUTTON_WIDTH, APPLY_BAR_BUTTON_HEIGHT, Text.empty(), button -> {
            sendSaveInteriorPacket();
        }, this.textRenderer));
    }

    public void backToInteriorSettings() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    public void sendSaveInteriorPacket() {
        if (!(MinecraftClient.getInstance().world.getBlockEntity(this.console) instanceof ConsoleBlockEntity consoleBlockEntity))
            return;
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        button.active = true;
        this.buttons.add((ButtonWidget) button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos playerPos = client.player.getBlockPos();

        // Build view matrix
        MatrixStack matrices = new MatrixStack();
        matrices.translate(0, 0, -2500);
        matrices.scale(zoom, zoom, zoom);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));
        matrices.translate(offsetX / zoom, -offsetY / zoom, 0);

        // Render the world!
        worldRenderer.render(client.world, playerPos, matrices, delta, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        } else if (button == 2) {
            isPanning = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            return true;
        } else if (button == 2) {
            isPanning = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            rotationY += (float) deltaX * 0.5f;
            rotationX += (float) deltaY * 0.5f;
            rotationX = Math.max(-90, Math.min(90, rotationX));
            return true;
        } else if (isPanning) {
            offsetX += (float) deltaX;
            offsetY += (float) deltaY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        zoom += (float) amount * 0.5f;
        zoom = Math.max(1.0f, Math.min(100.0f, zoom));
        return true;
    }

    @Override
    public void close() {
        if (worldRenderer != null) {
            worldRenderer.close();
        }
        super.close();
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(BACKGROUND, left, top, 0, 0, bgWidth, bgHeight);
    }

    public static class AITPressableTextWidget extends ButtonWidget {
        private final TextRenderer textRenderer;
        private final Text text;

        public AITPressableTextWidget(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress,
                                      TextRenderer textRenderer) {
            super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.textRenderer = textRenderer;
            this.text = text;
        }

        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            Text text = this.text;
            context.drawTextWithShadow(this.textRenderer, text, this.getX(), this.getY(),
                    16777215 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}