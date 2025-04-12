package dev.amble.ait.core.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.renderers.decoration.RoundelBlockEntityRenderer;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.item.RoundelItem;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;

@Environment(value=EnvType.CLIENT)
public class RoundelFabricatorScreen
        extends HandledScreen<RoundelFabricatorScreenHandler> {
    private static final Identifier TEXTURE = AITMod.id("textures/gui/roundel_fabricator.png");
    private static final int PATTERN_LIST_COLUMNS = 4;
    private static final int PATTERN_LIST_ROWS = 4;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int PATTERN_ENTRY_SIZE = 14;
    private static final int SCROLLBAR_AREA_HEIGHT = 56;
    private static final int PATTERN_LIST_OFFSET_X = 60;
    private static final int PATTERN_LIST_OFFSET_Y = 13;
    @Nullable private List<Pair<RoundelPattern, DyeColor>> roundelPatterns;
    private ItemStack roundel = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int visibleTopRow;

    public RoundelFabricatorScreen(RoundelFabricatorScreenHandler screenHandler, PlayerInventory inventory, Text title) {
        super(screenHandler, inventory, title);
        screenHandler.setInventoryChangeListener(this::onInventoryChanged);
        this.titleY -= 2;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
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
            context.getMatrices().push();
            context.getMatrices().translate(i + 127, j - 8, 0);
            context.getMatrices().scale(24.0f, -24.0f, 1);
            context.getMatrices().translate(0.5f, 0.5f, 0.5f);
            context.getMatrices().multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(0));
            context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(0));
            float f = 1f;
            context.getMatrices().scale(f, -f, -f);
            List<Pair<RoundelPattern, DyeColor>> reversedPatterns = new ArrayList<>(this.roundelPatterns);
            Collections.reverse(reversedPatterns);
            RoundelBlockEntityRenderer.renderBlock(RoundelBlockEntityRenderer.getTexturedModelData().createModel(), context.getMatrices(),
                    context.getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV, reversedPatterns, false);

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

    private void drawRoundel(DrawContext context, RoundelPattern pattern, int x, int y) {
        NbtCompound nbtCompound = new NbtCompound();
        NbtList nbtList = new RoundelPattern.Patterns().add(RoundelPatterns.BASE, DyeColor.GRAY).add(pattern, DyeColor.WHITE).toNbt();
        nbtCompound.put("Patterns", nbtList);
        ItemStack itemStack = new ItemStack(AITBlocks.ROUNDEL.asItem());
        BlockItem.setBlockEntityNbt(itemStack, AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE, nbtCompound);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate((float)x - 2.35f, y + 1, 0.0f);
        matrixStack.scale(6.0f, -6.0f, 1.0f);
        matrixStack.translate(0.5f, 0.5f, 0.0f);
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        float f = 1f;
        matrixStack.scale(f, -f, -f);
        List<Pair<RoundelPattern, DyeColor>> list = RoundelBlockEntity.getPatternsFromNbt(DyeColor.GRAY, RoundelBlockEntity.getPatternListNbt(itemStack));
        RoundelBlockEntityRenderer.renderBlock(RoundelBlockEntityRenderer.getTexturedModelData().createModel(), matrixStack,
                context.getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV, list, false);
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
