package dev.amble.ait.client.screens;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import dev.amble.ait.registry.impl.SonicRegistry;

public class SonicSettingsScreen extends ConsoleScreen {
    private static final Identifier BACKGROUND = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/sonic_selection.png");
    private final List<ButtonWidget> buttons = Lists.newArrayList();
    int bgHeight = 130;
    int bgWidth = 216;
    int left, top;
    int choicesCount = 0;
    private final Screen parent;
    private int selectedSonic;
    private final int APPLY_BAR_BUTTON_WIDTH = 53;
    private final int APPLY_BAR_BUTTON_HEIGHT = 12;
    private final int SMALL_ARROW_BUTTON_WIDTH = 20;
    private final int SMALL_ARROW_BUTTON_HEIGHT = 12;
    private BlockPos console;

    public SonicSettingsScreen(ClientTardis tardis, BlockPos console, Screen parent) {
        super(Text.translatable("screen." + AITMod.MOD_ID + ".sonicsettings.title"), tardis, console);
        this.console = console;
        this.parent = parent;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        if (!(MinecraftClient.getInstance().world.getBlockEntity(this.console) instanceof ConsoleBlockEntity consoleBlockEntity)) return;
        SonicSchema schema = SonicItem.schema(consoleBlockEntity.getSonicScrewdriver()/*tardis().sonic().getConsoleSonic()*/);

        this.selectedSonic = SonicRegistry.getInstance().toList().indexOf(schema);
        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;
        this.createButtons();

        super.init();
    }

    private void createButtons() {
        choicesCount = 0;
        this.buttons.clear();

        // apply bar button
        this.addButton(new AITPressableTextWidget((int) (left + (bgWidth * 0.139f)), (int) (top + (bgHeight * 0.839f)),
                APPLY_BAR_BUTTON_WIDTH, APPLY_BAR_BUTTON_HEIGHT, Text.empty(), button -> {
                    sendSonicChangePacket();
                }, this.textRenderer));

        // back button
        Text back = Text.translatable("screen.ait.sonicsettings.back");
        this.addButton(new PressableTextWidget((width / 2 - 102), (height / 2 - 59), this.textRenderer.getWidth(back),
                10, back, button -> backToInteriorSettings(), this.textRenderer));

        // arrow buttons
        this.addButton(new AITPressableTextWidget((int) (left + (bgWidth * 0.042f)), (int) (top + (bgHeight * 0.839f)),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> this.getLastSelectedSonic(),
                this.textRenderer));
        this.addButton(new AITPressableTextWidget((int) (left + (bgWidth * 0.389f)), (int) (top + (bgHeight * 0.839f)),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> this.getNextSelectedSonic(),
                this.textRenderer));
    }

    public void backToInteriorSettings() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    public void sendSonicChangePacket() {
        if (!(MinecraftClient.getInstance().world.getBlockEntity(this.console) instanceof ConsoleBlockEntity consoleBlockEntity)) return;
        if (consoleBlockEntity.getSonicScrewdriver()/*this.tardis().sonic().getConsoleSonic()*/ == null)
            return;

        SonicSchema schema = SonicRegistry.getInstance().toList().get(this.selectedSonic);

        if (!this.tardis().isUnlocked(schema))
            return;

        SonicItem.setSchema(consoleBlockEntity.getSonicScrewdriver()/*tardis().sonic().getConsoleSonic()*/, schema);
        System.out.println(this.console);
        ClientTardisUtil.changeSonicWithScreen(this.tardis().getUuid(), schema, this.console);
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        button.active = true; // this whole method is unnecessary bc it defaults to true ( ?? ) - does it
        // though?
        this.buttons.add((ButtonWidget) button);
    }

    private void createTextButton(Text text, ButtonWidget.PressAction onPress) {
        this.addButton(new PressableTextWidget((int) (left + (bgWidth * 0.06f)),
                (int) (top + (bgHeight * (0.1f * (choicesCount + 1)))), this.textRenderer.getWidth(text), 10, text,
                onPress, this.textRenderer));

        choicesCount++;
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
    }

    protected void drawSonicScrewdriver(DrawContext context, int x, int y, float scale) {
        if (this.tardis() == null)
            return;

        if (!(MinecraftClient.getInstance().world.getBlockEntity(this.console) instanceof ConsoleBlockEntity consoleBlockEntity)) return;

        if (consoleBlockEntity.getSonicScrewdriver()/*this.tardis().sonic().getConsoleSonic()*/ == null)
            return;

        ItemStack sonic = consoleBlockEntity.getSonicScrewdriver(); //this.tardis().sonic().getConsoleSonic();
        if (sonic.isEmpty()) return;
        NbtCompound nbt = sonic.getOrCreateNbt();

        if (this.tardis() != null) {
            MatrixStack stack = context.getMatrices();

            ItemStack sonicCopy = sonic.copy();
            SonicSchema schema = SonicRegistry.getInstance().toList().get(this.selectedSonic);

            SonicItem.setSchema(sonicCopy, schema);

            stack.push();
            stack.translate(50f, 50f, 1000f);
            context.drawCenteredTextWithShadow(this.textRenderer, (tardis().isUnlocked(schema)) ? "" : "\uD83D\uDD12",
                    x, y, 0xFFFFFF);
            stack.pop();

            stack.push();

            SonicSchema.Rendering rendering = schema.rendering();
            SonicSchema.Rendering.Offset positionOffset = rendering.getPositionOffset();
            SonicSchema.Rendering.Offset scaleOffset = rendering.getScaleOffset();

            stack.translate(x + positionOffset.x(), y + positionOffset.y(), positionOffset.z());
            stack.scale(scale + scaleOffset.x(), scale + scaleOffset.y(), scale + scaleOffset.z());

            boolean isSonicUnlocked = tardis().isUnlocked(schema);

            float base = isSonicUnlocked ? 1f : 0.1f;

            RenderSystem.setShaderColor(base, base, base, 1f);
            DiffuseLighting.disableGuiDepthLighting();
            context.drawItem(sonicCopy, 0, 0);
            DiffuseLighting.enableGuiDepthLighting();
            RenderSystem.setShaderColor(1, 1, 1, 1);

            stack.pop();

            stack.push();
            stack.translate(10, 0, 500f);
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("screen.ait.sonic_casing"), x + 140, y + 10,
                    0xFFFFFF);
            context.drawCenteredTextWithShadow(this.textRenderer, SonicItem.schema(sonicCopy).name(), x + 140,
                    y + 20, 0x00FFFF);
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("screen.ait.current_au"), x + 140, y + 40, 0xFFFFFF);
            context.drawCenteredTextWithShadow(this.textRenderer, nbt.getDouble(SonicItem.FUEL_KEY) + " AU", x + 140,
                    y + 50, 0x00FFFF);

            UUID tardis = LinkableItem.getTardisIdStatic(sonicCopy);

            if (tardis != null) {
                context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("screen.ait.linked_tardis"), x + 140, y + 70,
                        0xFFFFFF);
                context.drawCenteredTextWithShadow(this.textRenderer, tardis.toString().substring(0, 8), x + 140,
                        y + 80, 0x00FFFF);
            }

            stack.pop();
        }
    }

    public void getNextSelectedSonic() {
        this.selectedSonic = (this.selectedSonic + 1) % SonicRegistry.getInstance().size();
    }

    public void getLastSelectedSonic() {
        this.selectedSonic = (this.selectedSonic - 1 + SonicRegistry.getInstance().size()) % SonicRegistry.getInstance().size();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context);
        this.drawSonicScrewdriver(context, (width / 2 - 100), (height / 2 - 45), 6f);

        // apply bar button
        if (!this.buttons.get(0).isHovered())
            context.drawTexture(BACKGROUND, left + 30, top + 109, 40, 130, APPLY_BAR_BUTTON_WIDTH, APPLY_BAR_BUTTON_HEIGHT);
        else
            context.drawTexture(BACKGROUND, left + 30, top + 109, 40, 142, APPLY_BAR_BUTTON_WIDTH, APPLY_BAR_BUTTON_HEIGHT);

        // arrow buttons
        if (!this.buttons.get(2).isHovered())
            context.drawTexture(BACKGROUND, left + 9, top + 109, 0, 130, SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(BACKGROUND, left + 9, top + 109, 0, 142, SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);

        if (!this.buttons.get(3).isHovered())
            context.drawTexture(BACKGROUND, left + 84, top + 109, 20, 130, SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(BACKGROUND, left + 84, top + 109, 20, 142, SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(BACKGROUND, left, top, 0, 0, bgWidth, bgHeight);
        context.drawTexture(BACKGROUND, left + 9, top + 25, 0, 154, 95, 84);
    }

    public static class AITPressableTextWidget extends ButtonWidget {
        private final TextRenderer textRenderer;
        private final Text text;
        // private final Text hoverText;
        public AITPressableTextWidget(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress,
                TextRenderer textRenderer) {
            super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.textRenderer = textRenderer;
            this.text = text;
            // this.hoverText = Texts.setStyleIfAbsent(text.copy(),
            // Style.EMPTY.withUnderline(true));
        }

        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            Text text = /* this.isSelected() ? this.hoverText : */ this.text;
            context.drawTextWithShadow(this.textRenderer, text, this.getX(), this.getY(),
                    16777215 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
