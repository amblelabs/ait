package dev.amble.ait.core.screens;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import me.shedaniel.clothconfig2.gui.widget.ColorDisplayWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.item.RoundelItem;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;
import dev.amble.ait.core.roundels.RoundelType;


@Environment(value=EnvType.CLIENT)
public class RoundelFabricatorScreen
        extends HandledScreen<RoundelFabricatorScreenHandler> implements ScreenHandlerListener {
    private static final Identifier TEXTURE = AITMod.id("textures/gui/roundel_fabricator.png");
    private static final int PATTERN_LIST_COLUMNS = 4;
    private static final int PATTERN_LIST_ROWS = 4;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int PATTERN_ENTRY_SIZE = 14;
    private static final int SCROLLBAR_AREA_HEIGHT = 56;
    private static final int PATTERN_LIST_OFFSET_X = 60;
    private static final int PATTERN_LIST_OFFSET_Y = 13;
    @Nullable private List<RoundelType> roundelPatterns;
    private static final Map<Formatting, TextColor> FORMATTING_TO_COLOR = Stream.of(Formatting.values()).filter(Formatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), formatting -> new TextColor(formatting.getColorValue(), formatting.getName())));
    private static final Map<String, TextColor> BY_NAME = FORMATTING_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap(TextColor::getName, Function.identity()));
    private ItemStack roundel = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int visibleTopRow;
    private final List<TexturedButtonWidget> buttons = Lists.newArrayList();
    private TextFieldWidget hexColorField;
    private ColorDisplayWidget displayWidget;
    public RoundelFabricatorScreen(RoundelFabricatorScreenHandler screenHandler, PlayerInventory inventory, Text title) {
        super(screenHandler, inventory, title);
        screenHandler.setInventoryChangeListener(this::onInventoryChanged);
        this.titleY -= 2;
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add((TexturedButtonWidget) button);
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.hexColorField = new TextFieldWidget(this.textRenderer, i - 99, j + 150, 103, 12, Text.literal("#00ff00ff"));
        this.hexColorField.setFocusUnlocked(false);
        this.hexColorField.setEditableColor(-1);
        this.hexColorField.setUneditableColor(-1);
        this.hexColorField.setDrawsBackground(false);
        this.hexColorField.setMaxLength(14);
        this.hexColorField.setChangedListener(this::onInputHexCode);
        this.hexColorField.setText("");
        this.addSelectableChild(this.hexColorField);
        this.setInitialFocus(this.hexColorField);
        this.hexColorField.setEditable(false);
        this.addButton(new TexturedButtonWidget(this.x - 103, this.y + 2, 102, 102, -50, 0, AITMod.id("textures/environment/saturn_ring.png"), button -> {
            this.onPress(true);
        }));
        this.displayWidget = new ColorDisplayWidget(new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
                25, 25, 50, 50, Text.literal("HI IM TEXT")),
                15, 65, 27, 0xffffffff);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.hexColorField.getText();
        this.init(client, width, height);
        this.hexColorField.setText(string);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.player.closeHandledScreen();
        }
        if (this.hexColorField.keyPressed(keyCode, scanCode, modifiers) || this.hexColorField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onInputHexCode(String name) {
        if (TextColor.parse(name) != null) {
            this.insertMouseColorHere = RoundelFabricatorScreen.parse(name);
            this.onPress(false);
            int xPos = -90;
            int yPos = 20;
            int centerX = xPos + this.x + this.backgroundWidth / 2;
            int centerY = yPos + this.y + this.backgroundHeight / 2;
            this.cursorVec = getPositionForColor(this.insertMouseColorHere, centerX - 50, -centerY + 50, 50);
        }
    }

    @Nullable public static int parse(String name) {
        if (name.startsWith("#")) {
            try {
                String p = name.substring(1);
                int i = Integer.parseInt(p/*p.length() < 15 ? p + "FF" : p*/, 16);
                int r = ColorHelper.Argb.getRed(i);
                int g = ColorHelper.Argb.getGreen(i);
                int b = ColorHelper.Argb.getBlue(i);
                return ColorHelper.Argb.getArgb(255, r, g, b);
            } catch (NumberFormatException numberFormatException) {
                return 0;
            }
        }
        int i = BY_NAME.get(name).getRgb();
        int r = ColorHelper.Argb.getRed(i);
        int g = ColorHelper.Argb.getGreen(i);
        int b = ColorHelper.Argb.getBlue(i);
        return ColorHelper.Argb.getArgb(255, r, g, b);
    }

    public void onPress(boolean moveCursor) {
        this.shouldMoveCursor = moveCursor;
        Scheduler.get().runTaskLater(() -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(insertMouseColorHere);
            ClientPlayNetworking.send(AITMod.id("update_roundel_color"), buf);
        }, TimeUnit.TICKS, 1);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderForeground(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private int getRows() {
        return MathHelper.ceilDiv(this.handler.getRoundelPatterns().size(), 4);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        this.renderBackground(context);
        int i = this.x;
        int j = this.y;
        this.drawColorWheel(context, delta, mouseX, mouseY);
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        Slot slot = this.handler.getRoundelSlot();
        Slot slot2 = this.handler.getDyeSlot();
        Slot slot3 = this.handler.getPatternSlot();
        Slot slot4 = this.handler.getOutputSlot();
        if (!slot.hasStack()) {
            context.drawTexture(TEXTURE, i + slot.x, j + slot.y, this.backgroundWidth, 0, 16, 16);
        }
        if (!slot2.hasStack()) {
            context.drawTexture(TEXTURE, i + slot2.x, j + slot2.y, this.backgroundWidth + 16, 0, 16, 16);
        }
        if (!slot3.hasStack()) {
            context.drawTexture(TEXTURE, i + slot3.x, j + slot3.y, this.backgroundWidth + 32, 0, 16, 16);
        }
        int k = (int)(41.0f * this.scrollPosition);
        context.drawTexture(TEXTURE, i + 119, j + 13 + k, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
        DiffuseLighting.disableGuiDepthLighting();
        if (this.roundelPatterns != null && !this.hasTooManyPatterns) {
            MatrixStack matrixOfTheStack = context.getMatrices();
            matrixOfTheStack.push();
            matrixOfTheStack.translate(i + 163, j + 4, 0);
            matrixOfTheStack.scale(-24, 24, 24);
            matrixOfTheStack.translate(0.5f, 0.5f, 0.5f);
            matrixOfTheStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(0));
            matrixOfTheStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0));
            float f = 1f;
            matrixOfTheStack.scale(f, f, f);
            ModelPart modelPart = RoundelFabricatorScreen.getTexturedModelData().createModel();
            for (int p = 0; p < 17 && p < this.roundelPatterns.size(); ++p) {
                RoundelType pair = this.roundelPatterns.get(p);
                float r = ColorHelper.Argb.getRed(pair.color()) / 255f;
                float g = ColorHelper.Argb.getGreen(pair.color()) / 255f;
                float b = ColorHelper.Argb.getBlue(pair.color()) / 255f;
                if (pair.pattern().equals(RoundelPatterns.BASE)) {
                    matrixOfTheStack.push();
                    matrixOfTheStack.translate(0, 0, -0.01f);
                    modelPart.render(matrixOfTheStack, context.getVertexConsumers().getBuffer(RenderLayer.getEntityCutoutNoCull(pair.pattern().texture())),
                            0xf000f0, OverlayTexture.DEFAULT_UV, r, g, b, 1.0f);
                    matrixOfTheStack.pop();
                    continue;
                }

                VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(pair.pattern().texture()));

                modelPart.render(matrixOfTheStack, vertexConsumer, 0xf000f0, OverlayTexture.DEFAULT_UV, r, g, b, 1.0f);
            }

            context.getMatrices().pop();
            context.draw();
        } else if (this.hasTooManyPatterns) {
            context.drawTexture(TEXTURE, i + slot4.x - 2, j + slot4.y - 2, this.backgroundWidth, 17, 17, 16);
        }
        if (this.canApplyDyePattern) {
            int l = i + 60;
            int m = j + 13;
            List<RoundelPattern> list = this.handler.getRoundelPatterns();
            block0: for (int n = 0; n < 4; ++n) {
                for (int o = 0; o < 4; ++o) {
                    boolean bl;
                    int p = n + this.visibleTopRow;
                    int q = p * 4 + o;
                    if (q >= list.size()) break block0;
                    int r = l + o * 14;
                    int s = m + n * 14;
                    boolean bl2 = bl = mouseX >= r && mouseY >= s && mouseX < r + 14 && mouseY < s + 14;
                    int t = q == this.handler.getSelectedPattern() ? this.backgroundHeight + 14 : (bl ? this.backgroundHeight + 28 : this.backgroundHeight);
                    context.drawTexture(TEXTURE, r, s, 0, t, 14, 14);
                    this.drawRoundel(context, list.get(q), r, s);
                }
            }
        }
        DiffuseLighting.enableGuiDepthLighting();
    }

    protected void renderForeground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.hexColorField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        System.out.print(slotId);
        if (slotId == this.handler.getDyeSlot().id) {
            this.hexColorField.setText(stack.isEmpty() ? "" : stack.getName().getString());
            this.hexColorField.setEditable(!stack.isEmpty());
            this.setFocused(this.hexColorField);
        }
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }

    private static Identifier colorWheelTexture;
    private static boolean colorWheelInitialized = false;
    private boolean shouldMoveCursor;
    private int insertMouseColorHere = 0xFFFFFFFF;
    private Vector2i cursorVec = new Vector2i((int) 3.400E+2, (int) -2.220E+2);

    private void drawColorWheel(DrawContext context, float delta, int mouseX, int mouseY) {

        if (!colorWheelInitialized) {
            colorWheelTexture = generateColorWheelTexture();
            colorWheelInitialized = true;
        }

        int xPos = -90;
        int yPos= 20;

        int centerX = xPos + this.x + this.backgroundWidth / 2;
        int centerY = yPos + this.y + this.backgroundHeight / 2;
        int radius = 50; // Adjust the radius as needed

        ColorWheel colorWheel = new ColorWheel(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        int circumference = radius * 2;
        //System.out.println("X: " + (this.x - mouseX) + ", Y: " + (this.y - mouseY) + " | " + "centerX: " + centerX + ", centerY: " + (-centerY));
        if (isCursorInsideCircle((this.x - mouseX), (this.y - mouseY) , 50, -50, 56) && shouldMoveCursor) {
            try {
                Robot robot = new Robot();
                Point mouseLocation;
                Color pixelColor;
                mouseLocation = MouseInfo.getPointerInfo().getLocation();
                pixelColor = robot.getPixelColor(mouseLocation.x, mouseLocation.y);
                //System.out.print(mouseLocation.x + "||" + mouseLocation.y);
                //if (pixelColor.getRGB() != 0x0ff303f5b) {
                    insertMouseColorHere = pixelColor.getRGB();
                //}
                this.cursorVec = getPositionForColor(insertMouseColorHere, centerX - 50, -centerY + 50, 50);

                shouldMoveCursor = false;
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        // Draw the precomputed color wheel texture
        MatrixStack stack = context.getMatrices();
        {
            stack.push();
            stack.translate(-2, -3, 30);
            context.drawText(this.textRenderer, Text.literal("+"),
                    this.cursorVec.x(), -this.cursorVec.y(), Color.WHITE.getRGB(), false);
            stack.pop();
        }
        stack.push();

        stack.translate(colorWheel.x(), colorWheel.y(), 1);
        context.fill(RenderLayer.getGui(), -radius - 3, -radius - 3, radius + 5, radius + 63, -1, 0xff000000);
        context.fill(RenderLayer.getGui(), -radius - 2, -radius - 2, radius + 5, radius + 62, -1, 0x0ff303f5b);
        context.fill(RenderLayer.getGui(), -48, 95, 42, 106, -1, Color.GRAY.getRGB());

        this.displayWidget.setColor(this.insertMouseColorHere);
        stack.push();
        stack.translate(0, 0, 1);
        int r = ColorHelper.Argb.getRed(insertMouseColorHere);
        int g = ColorHelper.Argb.getGreen(insertMouseColorHere);
        int b = ColorHelper.Argb.getBlue(insertMouseColorHere);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        context.drawText(this.textRenderer, Text.literal("R: " + ColorHelper.Argb.getRed(insertMouseColorHere)),
                -48, 65, Color.WHITE.getRGB(), false);
        context.drawText(this.textRenderer,  Text.literal("G: " +
                ColorHelper.Argb.getGreen(insertMouseColorHere)),-40, 73,Color.WHITE.getRGB(), false);
        context.drawText(this.textRenderer,  Text.literal(
                "B: " + ColorHelper.Argb.getBlue(insertMouseColorHere)),-32, 81, Color.WHITE.getRGB(), false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        stack.pop();
        displayWidget.render(context, mouseX, mouseY, delta);
        stack.multiply(RotationAxis.POSITIVE_Z
                .rotationDegrees(180f));
        stack.scale(0.4f, 0.4f, 1.0f); // Scale down the texture

        // TODO: work on brightness. - Loqor
        /*float brightness = MathHelper.clamp((float) Math.sqrt(mouseX * mouseX + mouseY * mouseY) % 256 / 255.0f, 0.0f, 1.0f);

        RenderSystem.setShaderColor(brightness, brightness, brightness, 1);*/
        context.drawTexture(colorWheelTexture, -253 / 2, -253 / 2, 3, 3, 253, 253);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        stack.pop();
    }

    public boolean isCursorInsideCircle(int cursorX, int cursorY, int centerX, int centerY, int radius) {
        int deltaX = cursorX - centerX;
        int deltaY = cursorY - centerY;
        return (deltaX * deltaX + deltaY * deltaY) <= (radius * radius);
    }

    public Vector2i getPositionForColor(int color, int centerX, int centerY, int radius) {
        float[] hsb = Color.RGBtoHSB(
                ColorHelper.Argb.getRed(color),
                ColorHelper.Argb.getGreen(color),
                ColorHelper.Argb.getBlue(color),
                null
        );

        float angle = (float) (-hsb[0] * 2 * Math.PI - Math.toRadians(120)); // Add 120 degrees offset
        float distance = Math.min(hsb[1] * radius, radius); // Clamp distance to the radius

        int x = centerX + (int) (Math.cos(angle) * distance);
        int y = centerY - (int) (Math.sin(angle) * distance); // Invert y-axis to correct flipping

        return new Vector2i(x, y);
    }

    public record ColorWheel(int x, int y, int toX, int toY) {
        public int width() {
            return x + toX;
        }

        public int height() {
            return y + toY;
        }
    }

    private Identifier generateColorWheelTexture() {
        int radius = 50; // Adjust the radius as needed
        int diameter = radius * 2;
        NativeImage image = new NativeImage(diameter, diameter, true);

        for (int y = -radius; y < radius; y++) {
            for (int x = -radius; x < radius; x++) {
                double distance = Math.sqrt(x * x + y * y);
                if (distance <= radius) {
                    double angle = Math.atan2(y, x) + Math.PI; // Angle in radians
                    float hue = (float) (angle / (2 * Math.PI));
                    float saturation = (float) (distance / radius);
                    int color = Color.HSBtoRGB(hue, saturation, 1.0f); // Full brightness
                    image.setColor(x + radius, y + radius, color | 0xFF000000); // Add alpha
                } else {
                    image.setColor(x + radius, y + radius, 0); // Transparent
                }
            }
        }

        Identifier textureId = AITMod.id("textures/gui/color_wheel.png");
        MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, new NativeImageBackedTexture(image));
        return textureId;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData cube = modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    private void drawRoundel(DrawContext context, RoundelPattern pattern, int x, int y) {
        NbtCompound nbtCompound = new NbtCompound();
        NbtList nbtList = new RoundelPattern.Patterns().add(RoundelPatterns.BASE, DyeColor.BLACK.getSignColor()).add(pattern, DyeColor.WHITE.getSignColor()).toNbt();
        nbtCompound.put("Patterns", nbtList);
        ItemStack itemStack = new ItemStack(AITBlocks.ROUNDEL.asItem());
        BlockItem.setBlockEntityNbt(itemStack, AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE, nbtCompound);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate((float)x + 0.75f, y + 10, 0.0f);
        matrixStack.scale(6.0f, -6.0f, 1.0f);
        matrixStack.translate(0.5f, 0.5f, 0.0f);
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        float f = 1f;
        matrixStack.scale(f, -f, -f);
        List<RoundelType> list = RoundelBlockEntity.getPatternsFromNbt(DyeColor.GRAY.getSignColor(), RoundelBlockEntity.getPatternListNbt(itemStack));
        ModelPart modelPart = RoundelFabricatorScreen.getTexturedModelData().createModel();
        modelPart.render(matrixStack, context.getVertexConsumers().getBuffer(RenderLayer.getEntityCutoutNoCull(list.get(0).pattern().texture())), 0xf000f0, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1.0f);
        for (int i = 0; i < 17 && i < list.size(); ++i) {
            RoundelType pair = list.get(i);
            float[] fs = new float[]{ColorHelper.Argb.getRed(pair.color()) / 255f, ColorHelper.Argb.getGreen(pair.color()) / 255f, ColorHelper.Argb.getBlue(pair.color()) / 255f};
            if (pair.pattern().equals(RoundelPatterns.BASE)) {
                modelPart.render(matrixStack, context.getVertexConsumers().getBuffer(RenderLayer.getEntityCutoutNoCull(pair.pattern().texture())),
                        0xf000f0, OverlayTexture.DEFAULT_UV, fs[0], fs[1], fs[2], 1.0f);
                continue;
            }

            VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getEntityNoOutline(pair.pattern().texture()));

            modelPart.render(matrixStack, vertexConsumer, 0xf000f0, OverlayTexture.DEFAULT_UV, fs[0], fs[1], fs[2], 1.0f);
        }
        matrixStack.pop();
        context.draw();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrollbarClicked = false;
        if (this.canApplyDyePattern) {
            int i = this.x + 60;
            int j = this.y + 13;
            for (int k = 0; k < 4; ++k) {
                for (int l = 0; l < 4; ++l) {
                    double d = mouseX - (double)(i + l * 14);
                    double e = mouseY - (double)(j + k * 14);
                    int m = k + this.visibleTopRow;
                    int n = m * 4 + l;
                    if (!(d >= 0.0) || !(e >= 0.0) || !(d < 14.0) || !(e < 14.0) || !this.handler.onButtonClick(this.client.player, n)) continue;
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                    this.client.interactionManager.clickButton(this.handler.syncId, n);
                    return true;
                }
            }
            i = this.x + 119;
            j = this.y + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 56)) {
                this.scrollbarClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = this.getRows() - 4;
        if (this.scrollbarClicked && this.canApplyDyePattern && i > 0) {
            int j = this.y + 13;
            int k = j + 56;
            this.scrollPosition = ((float)mouseY - (float)j - 7.5f) / ((float)(k - j) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.visibleTopRow = Math.max((int)((double)(this.scrollPosition * (float)i) + 0.5), 0);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = this.getRows() - 4;
        if (this.canApplyDyePattern && i > 0) {
            float f = (float)amount / (float)i;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0f, 1.0f);
            this.visibleTopRow = Math.max((int)(this.scrollPosition * (float)i + 0.5f), 0);
        }
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    private void onInventoryChanged() {
        this.hexColorField.setEditable(!this.dye.isEmpty());
        ItemStack itemStack = this.handler.getOutputSlot().getStack();
        this.roundelPatterns = itemStack.isEmpty() ? null : RoundelBlockEntity.getPatternsFromNbt(((RoundelItem)itemStack.getItem()).getColor(), RoundelBlockEntity.getPatternListNbt(itemStack));
        ItemStack itemStack2 = this.handler.getRoundelSlot().getStack();
        ItemStack itemStack3 = this.handler.getDyeSlot().getStack();
        ItemStack itemStack4 = this.handler.getPatternSlot().getStack();
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack2);
        boolean bl = this.hasTooManyPatterns = nbtCompound != null && nbtCompound.contains("Patterns", NbtElement.LIST_TYPE) && !itemStack2.isEmpty() && nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).size() >= 6;
        if (this.hasTooManyPatterns) {
            this.roundelPatterns = null;
        }
        if (!(ItemStack.areEqual(itemStack2, this.roundel) && ItemStack.areEqual(itemStack3, this.dye) && ItemStack.areEqual(itemStack4, this.pattern))) {
            boolean bl2 = this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && !this.hasTooManyPatterns && !this.handler.getRoundelPatterns().isEmpty();
        }
        if (this.visibleTopRow >= this.getRows()) {
            this.visibleTopRow = 0;
            this.scrollPosition = 0.0f;
        }
        this.roundel = itemStack2.copy();
        this.dye = itemStack3.copy();
        this.pattern = itemStack4.copy();
    }
}
