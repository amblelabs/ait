package dev.amble.ait.client.screens.preset;

import java.util.List;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.renderers.VortexRender;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.vortex.reference.VortexReference;
import dev.amble.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import dev.amble.ait.data.hum.Hum;
import dev.amble.ait.data.preset.TardisPreset;
import dev.amble.ait.data.preset.TardisPresetHandler;
import dev.amble.ait.data.preset.TardisPresetRegistry;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.HumRegistry;
import dev.amble.ait.registry.impl.console.variant.ClientConsoleVariantRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

/**
 * Screen for selecting a TARDIS preset when placing a Creative TARDIS item.
 */
@Environment(EnvType.CLIENT)
public class CreativePresetScreen extends Screen {
    
    private static final Identifier BACKGROUND = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/monitor_gui.png");
    private static final Identifier MISSING_PREVIEW = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/presets/missing_preview.png");

    private final List<ButtonWidget> buttons = Lists.newArrayList();
    
    // Position data for placement
    private final BlockPos placePos;
    private final Direction playerFacing;
    
    // Screen dimensions
    private static final int BG_WIDTH = 256;
    private static final int BG_HEIGHT = 166;
    private int left, top;
    
    // Current preset selection
    private TardisPreset selectedPreset;
    private int presetIndex = 0;
    
    // Preview element cycling
    private PreviewElement currentElement = PreviewElement.EXTERIOR;
    private int elementCycleTimer = 0;
    private static final int ELEMENT_CYCLE_TICKS = 100; // 5 seconds per element
    
    // For spinning model preview
    private int tickForSpin = 0;
    
    // Sound preview
    private PositionedSoundInstance currentPreviewSound;
    
    // Button indices
    private static final int BTN_PREV_PRESET = 0;
    private static final int BTN_NEXT_PRESET = 1;
    private static final int BTN_PREV_ELEMENT = 2;
    private static final int BTN_NEXT_ELEMENT = 3;
    private static final int BTN_PLAY_SOUND = 4;
    private static final int BTN_CONFIRM = 5;
    private static final int BTN_CANCEL = 6;
    
    private enum PreviewElement {
        EXTERIOR("Exterior"),
        CONSOLE("Console"),
        DESKTOP("Desktop"),
        HUM("Hum"),
        TAKEOFF("Takeoff Sound"),
        FLIGHT("Flight Sound"),
        LANDING("Landing Sound"),
        VORTEX("Vortex");
        
        private final String displayName;
        
        PreviewElement(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public PreviewElement next() {
            PreviewElement[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }
        
        public PreviewElement previous() {
            PreviewElement[] values = values();
            return values[(this.ordinal() - 1 + values.length) % values.length];
        }
        
        public boolean isSoundElement() {
            return this == HUM || this == TAKEOFF || this == FLIGHT || this == LANDING;
        }
    }

    public CreativePresetScreen(BlockPos placePos, Direction playerFacing) {
        super(Text.translatable("screen.ait.preset_selection.title"));
        this.placePos = placePos;
        this.playerFacing = playerFacing;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        
        this.left = (this.width - BG_WIDTH) / 2;
        this.top = (this.height - BG_HEIGHT) / 2;
        
        // Initialize with first preset
        List<TardisPreset> presets = TardisPresetRegistry.getInstance().toList();
        if (!presets.isEmpty()) {
            this.selectedPreset = presets.get(0);
        } else {
            this.selectedPreset = TardisPresetRegistry.getInstance().fallback();
        }
        
        this.createButtons();
    }

    private void createButtons() {
        this.buttons.clear();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Button 0: Previous preset (big left arrow)
        this.addButton(new PressableTextWidget(centerX + 23, centerY + 10, 20, 20,
                Text.empty(), button -> previousPreset(), this.textRenderer));
        // Button 1: Next preset (big right arrow)
        this.addButton(new PressableTextWidget(centerX + 98, centerY + 10, 20, 20,
                Text.empty(), button -> nextPreset(), this.textRenderer));
        
        // Button 2: Previous element (small left arrow)
        this.addButton(new PressableTextWidget(centerX + 23, centerY + 54, 20, 12,
                Text.empty(), button -> previousElement(), this.textRenderer));
        // Button 3: Next element (small right arrow)
        this.addButton(new PressableTextWidget(centerX + 98, centerY + 54, 20, 12,
                Text.empty(), button -> nextElement(), this.textRenderer));
        
        // Button 4: Play sound button (for sound elements)
        this.addButton(new PressableTextWidget(centerX + 44, centerY + 54, 53, 12,
                Text.empty(), button -> playPreviewSound(), this.textRenderer));
        
        // Button 5: Confirm button
        this.addButton(new PressableTextWidget(centerX + 44, centerY + 10, 53, 20,
                Text.empty(), button -> confirmSelection(), this.textRenderer));
        
        // Button 6: Cancel/back button
        this.addButton(new PressableTextWidget(left + 9, top + 132, 20, 20,
                Text.empty(), button -> this.close(), this.textRenderer));
    }

    private void addButton(PressableTextWidget button) {
        this.addDrawableChild(button);
        this.buttons.add(button);
    }

    private void nextPreset() {
        List<TardisPreset> presets = TardisPresetRegistry.getInstance().toList();
        if (presets.isEmpty()) return;
        
        presetIndex = (presetIndex + 1) % presets.size();
        selectedPreset = presets.get(presetIndex);
        stopCurrentSound();
    }

    private void previousPreset() {
        List<TardisPreset> presets = TardisPresetRegistry.getInstance().toList();
        if (presets.isEmpty()) return;
        
        presetIndex = (presetIndex - 1 + presets.size()) % presets.size();
        selectedPreset = presets.get(presetIndex);
        stopCurrentSound();
    }

    private void nextElement() {
        stopCurrentSound();
        currentElement = currentElement.next();
        elementCycleTimer = 0;
    }

    private void previousElement() {
        stopCurrentSound();
        currentElement = currentElement.previous();
        elementCycleTimer = 0;
    }

    private void playPreviewSound() {
        stopCurrentSound();
        
        if (selectedPreset == null) return;
        if (!currentElement.isSoundElement()) return;
        
        SoundEvent sound = getSoundForCurrentElement();
        if (sound != null) {
            currentPreviewSound = PositionedSoundInstance.master(sound, 1.0f);
            MinecraftClient.getInstance().getSoundManager().play(currentPreviewSound);
        }
    }

    private SoundEvent getSoundForCurrentElement() {
        if (selectedPreset == null) return null;
        
        return switch (currentElement) {
            case HUM -> {
                Hum hum = getHumForPreset();
                yield hum != null ? hum.sound() : null;
            }
            case TAKEOFF -> {
                TardisAnimation anim = getTakeoffAnimationForPreset();
                yield anim != null ? anim.getSound() : null;
            }
            case FLIGHT -> null; // Flight sound is looping, handled differently
            case LANDING -> {
                TardisAnimation anim = getLandingAnimationForPreset();
                yield anim != null ? anim.getSound() : null;
            }
            default -> null;
        };
    }

    private void stopCurrentSound() {
        if (currentPreviewSound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(currentPreviewSound);
            currentPreviewSound = null;
        }
    }

    private void confirmSelection() {
        if (selectedPreset == null) return;
        
        stopCurrentSound();
        
        // Send packet to server with the selected preset
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(selectedPreset.id());
        buf.writeBlockPos(placePos);
        buf.writeInt(playerFacing.getHorizontal());
        
        ClientPlayNetworking.send(TardisPresetHandler.CONFIRM_PRESET, buf);
        this.close();
    }

    @Override
    public void close() {
        stopCurrentSound();
        super.close();
    }

    @Override
    public void tick() {
        super.tick();
        
        // Auto-cycle through elements
        elementCycleTimer++;
        if (elementCycleTimer >= ELEMENT_CYCLE_TICKS) {
            nextElement();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        tickForSpin++;
        
        this.renderBackground(context);
        drawBackground(context);
        drawPreview(context);
        drawPresetInfo(context);
        drawButtons(context);
        
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(BACKGROUND, left, top, 0, 0, BG_WIDTH, BG_HEIGHT);
    }

    private void drawPresetInfo(DrawContext context) {
        if (selectedPreset == null) return;
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(0, 0, 500f);
        
        // Left side info - Creative TARDIS title and selected preset
        context.drawText(this.textRenderer, Text.literal("Creative TARDIS"), left + 15, top + 50, 0xFFFFFF, true);
        context.drawText(this.textRenderer, Text.literal("Selected Preset:"), left + 15, top + 65, 0xAAAAAA, true);
        context.drawText(this.textRenderer, Text.literal(selectedPreset.name()).formatted(Formatting.BOLD, Formatting.AQUA), 
                left + 15, top + 78, 0xFFFFFF, true);
        
        // Preset name (right side header)
        context.drawCenteredTextWithShadow(this.textRenderer, 
                Text.literal(selectedPreset.name()).formatted(Formatting.BOLD), 
                centerX + 70, centerY - 68, 0xFFFFFF);
        
        // Preset count (moved up)
        List<TardisPreset> presets = TardisPresetRegistry.getInstance().toList();
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal((presetIndex + 1) + "/" + presets.size()).formatted(Formatting.BOLD),
                centerX + 70, centerY + 58, 0xFFFFFF);
        
        // Current element being previewed
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(currentElement.getDisplayName()),
                centerX + 70, centerY + 40, 0x5FAAFF);
        
        // Element value
        String elementValue = getElementValueDisplay();
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(elementValue),
                centerX + 70, centerY + 30, 0xAAAAAA);
        
        stack.pop();
    }

    private String getElementValueDisplay() {
        if (selectedPreset == null) return "None";
        
        return switch (currentElement) {
            case EXTERIOR -> {
                ExteriorVariantSchema ext = getExteriorForPreset();
                yield ext != null ? ext.name() : "Default";
            }
            case CONSOLE -> {
                ConsoleVariantSchema console = getConsoleForPreset();
                yield console != null ? console.id().getPath() : "Default";
            }
            case DESKTOP -> {
                TardisDesktopSchema desktop = getDesktopForPreset();
                yield desktop != null ? desktop.name() : "Default";
            }
            case HUM -> {
                Hum hum = getHumForPreset();
                yield hum != null ? hum.name() : "Default";
            }
            case TAKEOFF -> {
                TardisAnimation anim = getTakeoffAnimationForPreset();
                yield anim != null ? anim.name() : "Default";
            }
            case FLIGHT -> selectedPreset.flightSound().map(Identifier::getPath).orElse("Default");
            case LANDING -> {
                TardisAnimation anim = getLandingAnimationForPreset();
                yield anim != null ? anim.name() : "Default";
            }
            case VORTEX -> {
                VortexReference vortex = getVortexForPreset();
                yield vortex != null ? vortex.name() : "Default";
            }
        };
    }

    private void drawPreview(DrawContext context) {
        if (selectedPreset == null) return;
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Move previews up and reduce scale
        int previewY = centerY - 35;
        
        switch (currentElement) {
            case EXTERIOR -> drawExteriorPreview(context, centerX + 70, previewY, 14f);
            case CONSOLE -> drawConsolePreview(context, centerX + 70, previewY, 11f);
            case DESKTOP -> drawDesktopPreview(context, centerX + 70, previewY);
            case VORTEX -> drawVortexPreview(context, centerX + 70, previewY);
            default -> drawSoundIndicator(context, centerX + 70, previewY);
        }
    }

    private void drawExteriorPreview(DrawContext context, int x, int y, float scale) {
        ExteriorVariantSchema variant = getExteriorForPreset();
        if (variant == null) return;
        
        ClientExteriorVariantSchema clientVariant = ClientExteriorVariantRegistry.withParent(variant);
        if (clientVariant == null) return;
        
        ExteriorModel model = clientVariant.model();
        if (model == null) return;
        
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y + 15, 100f);
        stack.scale(-scale, scale, scale);
        stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(((float) tickForSpin / 1200L) * 360.0f));
        
        Identifier texture = clientVariant.texture();
        
        model.render(stack, context.getVertexConsumers().getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        
        stack.pop();
    }

    private void drawConsolePreview(DrawContext context, int x, int y, float scale) {
        ConsoleVariantSchema variant = getConsoleForPreset();
        if (variant == null) return;
        
        var clientVariant = ClientConsoleVariantRegistry.withParent(variant);
        if (clientVariant == null) return;
        
        ConsoleModel model = clientVariant.model();
        if (model == null) return;
        
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y + 22, 100f);
        stack.scale(-scale, scale, scale);
        stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(((float) tickForSpin / 1200L) * 360.0f));
        
        Identifier texture = clientVariant.texture();
        
        model.render(stack, context.getVertexConsumers().getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        
        stack.pop();
    }

    private void drawDesktopPreview(DrawContext context, int x, int y) {
        TardisDesktopSchema desktop = getDesktopForPreset();
        if (desktop == null) return;
        
        var preview = desktop.previewTexture();
        Identifier previewTexture = preview.texture();
        boolean exists = MinecraftClient.getInstance().getResourceManager().getResource(previewTexture).isPresent();
        
        // Smaller preview area
        int size = 70;
        
        context.drawTexture(
                exists ? previewTexture : MISSING_PREVIEW,
                x - size/2, y - size/2 + 15, size, size, 0, 0, 
                preview.width,
                preview.height, 
                preview.width,
                preview.height);
    }

    private void drawVortexPreview(DrawContext context, int x, int y) {
        VortexReference vortex = getVortexForPreset();
        if (vortex == null) return;
        
        MatrixStack stack = context.getMatrices();
        stack.push();
        
        // Use scissor to clip the vortex rendering
        int scissorX = x - 40;
        int scissorY = y - 30;
        int scissorW = 80;
        int scissorH = 70;
        
        // Convert to screen coordinates for scissor
        double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        int scissorLeft = (int) (scissorX * scaleFactor);
        int scissorBottom = (int) ((this.height - scissorY - scissorH) * scaleFactor);
        int scissorWidth = (int) (scissorW * scaleFactor);
        int scissorHeight = (int) (scissorH * scaleFactor);
        
        com.mojang.blaze3d.systems.RenderSystem.enableScissor(scissorLeft, scissorBottom, scissorWidth, scissorHeight);
        
        stack.translate(x, y + 10, 100f);
        stack.scale(0.8f, 0.8f, 0.8f);
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
        
        VortexRender.getInstance(vortex).render(stack);
        
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();
        
        stack.pop();
    }

    private void drawSoundIndicator(DrawContext context, int x, int y) {
        // Draw a speaker/sound icon or text indicating this is a sound preview
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("♪").formatted(Formatting.YELLOW),
                x, y, 0xFFFFFF);
        
        if (currentElement.isSoundElement()) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Click ▶ to preview").formatted(Formatting.GRAY),
                    x, y + 15, 0xAAAAAA);
        }
    }

    private void drawButtons(DrawContext context) {
        // Draw button textures (similar to MonitorScreen)
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Button 0: Big left arrow (previous preset)
        if (buttons.size() > BTN_PREV_PRESET) {
            ButtonWidget btn = buttons.get(BTN_PREV_PRESET);
            if (!btn.isHovered())
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 0, 166, 20, 20);
            else
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 0, 186, 20, 20);
        }
        
        // Button 1: Big right arrow (next preset)
        if (buttons.size() > BTN_NEXT_PRESET) {
            ButtonWidget btn = buttons.get(BTN_NEXT_PRESET);
            if (!btn.isHovered())
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 20, 166, 20, 20);
            else
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 20, 186, 20, 20);
        }
        
        // Button 2: Small left arrow (previous element)
        if (buttons.size() > BTN_PREV_ELEMENT) {
            ButtonWidget btn = buttons.get(BTN_PREV_ELEMENT);
            if (!btn.isHovered())
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 93, 166, 20, 12);
            else
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 93, 178, 20, 12);
        }
        
        // Button 3: Small right arrow (next element)
        if (buttons.size() > BTN_NEXT_ELEMENT) {
            ButtonWidget btn = buttons.get(BTN_NEXT_ELEMENT);
            if (!btn.isHovered())
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 113, 166, 20, 12);
            else
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 113, 178, 20, 12);
        }
        
        // Button 4: Play sound button (only show for sound elements)
        if (buttons.size() > BTN_PLAY_SOUND && currentElement.isSoundElement()) {
            ButtonWidget btn = buttons.get(BTN_PLAY_SOUND);
            if (!btn.isHovered())
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 133, 166, 53, 12);
            else
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 133, 178, 53, 12);
            
            // Draw play icon
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("▶ Play"),
                    btn.getX() + 26, btn.getY() + 2, 0xFFFFFF);
        }
        
        // Button 5: Confirm button
        if (buttons.size() > BTN_CONFIRM) {
            ButtonWidget btn = buttons.get(BTN_CONFIRM);
            if (!btn.isHovered())
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 40, 166, 53, 20);
            else
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 40, 186, 53, 20);
            
            // Draw "Confirm" text
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Confirm").formatted(Formatting.BOLD),
                    btn.getX() + 26, btn.getY() + 6, 0xFFFFFF);
        }
        
        // Button 6: Cancel button
        if (buttons.size() > BTN_CANCEL) {
            ButtonWidget btn = buttons.get(BTN_CANCEL);
            if (!btn.isHovered())
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 186, 166, 20, 20);
            else
                context.drawTexture(BACKGROUND, btn.getX(), btn.getY(), 186, 186, 20, 20);
        }
    }

    // Helper methods to get registry entries for preset
    private TardisPreset getFallbackPreset() {
        TardisPreset fallback = TardisPresetRegistry.getInstance().fallback();
        return fallback != null ? fallback : selectedPreset;
    }

    private ExteriorVariantSchema getExteriorForPreset() {
        if (selectedPreset == null) return null;
        TardisPreset fallback = getFallbackPreset();
        return selectedPreset.exterior()
                .map(id -> ExteriorVariantRegistry.getInstance().get(id))
                .orElseGet(() -> fallback != null ? fallback.exterior()
                        .map(id -> ExteriorVariantRegistry.getInstance().get(id))
                        .orElse(null) : null);
    }

    private ConsoleVariantSchema getConsoleForPreset() {
        if (selectedPreset == null) return null;
        TardisPreset fallback = getFallbackPreset();
        return selectedPreset.console()
                .map(id -> ConsoleVariantRegistry.getInstance().get(id))
                .orElseGet(() -> fallback != null ? fallback.console()
                        .map(id -> ConsoleVariantRegistry.getInstance().get(id))
                        .orElse(null) : null);
    }

    private TardisDesktopSchema getDesktopForPreset() {
        if (selectedPreset == null) return null;
        TardisPreset fallback = getFallbackPreset();
        return selectedPreset.desktop()
                .map(id -> DesktopRegistry.getInstance().get(id))
                .orElseGet(() -> fallback != null ? fallback.desktop()
                        .map(id -> DesktopRegistry.getInstance().get(id))
                        .orElse(null) : null);
    }

    private Hum getHumForPreset() {
        if (selectedPreset == null) return null;
        TardisPreset fallback = getFallbackPreset();
        return selectedPreset.hum()
                .map(id -> HumRegistry.getInstance().get(id))
                .orElseGet(() -> fallback != null ? fallback.hum()
                        .map(id -> HumRegistry.getInstance().get(id))
                        .orElse(HumRegistry.CORAL) : HumRegistry.CORAL);
    }

    private TardisAnimation getTakeoffAnimationForPreset() {
        if (selectedPreset == null) return null;
        return selectedPreset.takeoffSound()
                .map(id -> TardisAnimationRegistry.getInstance().get(id))
                .orElseGet(() -> TardisAnimationRegistry.getInstance().get(TardisAnimationRegistry.DEFAULT_DEMAT));
    }

    private TardisAnimation getLandingAnimationForPreset() {
        if (selectedPreset == null) return null;
        return selectedPreset.landingSound()
                .map(id -> TardisAnimationRegistry.getInstance().get(id))
                .orElseGet(() -> TardisAnimationRegistry.getInstance().get(TardisAnimationRegistry.DEFAULT_MAT));
    }

    private VortexReference getVortexForPreset() {
        if (selectedPreset == null) return null;
        return selectedPreset.vortex()
                .map(id -> VortexReferenceRegistry.getInstance().get(id))
                .orElse(VortexReferenceRegistry.TOYOTA);
    }
}
