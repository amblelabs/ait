/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package dev.amble.ait.core.screens;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blocks.AbstractRoundelBlock;
import dev.amble.ait.core.item.RoundelItem;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;

public class RoundelFabricatorScreenHandler
        extends ScreenHandler {
    private static final int NO_PATTERN = -1;
    private static final int INVENTORY_START = 4;
    private static final int INVENTORY_END = 31;
    private static final int HOTBAR_START = 31;
    private static final int HOTBAR_END = 40;
    private final ScreenHandlerContext context;
    final Property selectedPattern = Property.create();
    final Property colorProperty = Property.create();
    private List<RoundelPattern> roundelPatterns = List.of();
    Runnable inventoryChangeListener = () -> {};
    final Slot roundelSlot;
    final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot outputSlot;
    long lastTakeResultTime;
    private final Inventory input = new SimpleInventory(3){

        @Override
        public void markDirty() {
            super.markDirty();
            RoundelFabricatorScreenHandler.this.onContentChanged(this);
            RoundelFabricatorScreenHandler.this.inventoryChangeListener.run();
        }
    };
    private final Inventory output = new SimpleInventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            RoundelFabricatorScreenHandler.this.inventoryChangeListener.run();
        }
    };

    public RoundelFabricatorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    static {
        ServerPlayNetworking.registerGlobalReceiver(AITMod.id("update_roundel_color"), (server, player, handler, buf, responseSender) -> {
            int color = buf.readInt();
            ScreenHandler screenHandler = player.currentScreenHandler;
            if (screenHandler instanceof RoundelFabricatorScreenHandler roundelScreenHandler) {
                if (roundelScreenHandler.roundelSlot.hasStack() && roundelScreenHandler.dyeSlot.hasStack()) {
                    roundelScreenHandler.colorProperty.set(color);
                    if (!roundelScreenHandler.roundelPatterns.isEmpty() && roundelScreenHandler.selectedPattern != null && roundelScreenHandler.selectedPattern.get() != -1)
                        roundelScreenHandler.updateOutputSlot(roundelScreenHandler.roundelPatterns.get(roundelScreenHandler.selectedPattern.get()));
                }
            }
        });
    }

    public RoundelFabricatorScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(AITMod.ROUNDEL_FABRICATOR_HANDLER, syncId);
        int i;
        this.context = context;
        this.roundelSlot = this.addSlot(new Slot(this.input, 0, 13, 26){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof RoundelItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.input, 1, 33, 26){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.input, 2, 23, 45){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof AbstractRoundelBlock);
            }
        });
        this.outputSlot = this.addSlot(new Slot(this.output, 0, 143, 58){

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                RoundelFabricatorScreenHandler.this.roundelSlot.takeStack(1);
                RoundelFabricatorScreenHandler.this.dyeSlot.takeStack(1);
                RoundelFabricatorScreenHandler.this.patternSlot.takeStack(1);
                if (!RoundelFabricatorScreenHandler.this.roundelSlot.hasStack() || !RoundelFabricatorScreenHandler.this.dyeSlot.hasStack()) {
                    RoundelFabricatorScreenHandler.this.selectedPattern.set(-1);
                }
                context.run((world, pos) -> {
                    long l = world.getTime();
                    if (RoundelFabricatorScreenHandler.this.lastTakeResultTime != l) {
                        world.playSound(null, pos, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        RoundelFabricatorScreenHandler.this.lastTakeResultTime = l;
                    }
                });
                super.onTakeItem(player, stack);
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addProperty(this.selectedPattern);
        this.addProperty(this.colorProperty);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return RoundelFabricatorScreenHandler.canUse(this.context, player, AITBlocks.ROUNDEL_FABRICATOR);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.roundelPatterns.size()) {
            this.selectedPattern.set(id);
            this.updateOutputSlot(this.roundelPatterns.get(id));
            return true;
        }
        return false;
    }

    private List<RoundelPattern> getPatternsFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return RoundelPatterns.getInstance().toList();
        }
        if (stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof AbstractRoundelBlock)) {
            return List.of(RoundelPatterns.BASE);
        }
        return List.of();
    }

    private boolean isPatternIndexValid(int index) {
        return index >= 0 && index < this.roundelPatterns.size();
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        RoundelPattern registryEntry;
        ItemStack itemStack = this.roundelSlot.getStack();
        ItemStack itemStack2 = this.dyeSlot.getStack();
        ItemStack itemStack3 = this.patternSlot.getStack();
        if (itemStack.isEmpty() || itemStack2.isEmpty()) {
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
            this.roundelPatterns = List.of();
            this.selectedPattern.set(-1);
            return;
        }
        int i = this.selectedPattern.get();
        boolean bl = this.isPatternIndexValid(i);
        List<RoundelPattern> list = this.roundelPatterns;
        this.roundelPatterns = this.getPatternsFor(itemStack3);
        if (this.roundelPatterns.size() == 1) {
            this.selectedPattern.set(0);
            registryEntry = this.roundelPatterns.get(0);
        } else if (!bl) {
            this.selectedPattern.set(-1);
            registryEntry = null;
        } else {
            RoundelPattern registryEntry2 = list.get(i);
            int j = this.roundelPatterns.indexOf(registryEntry2);
            if (j != -1) {
                registryEntry = registryEntry2;
                this.selectedPattern.set(j);
            } else {
                registryEntry = null;
                this.selectedPattern.set(-1);
            }
        }
        if (registryEntry != null) {
            boolean bl2;
            NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
            boolean bl3 = bl2 = nbtCompound != null && nbtCompound.contains("Patterns", NbtElement.LIST_TYPE) &&
                    !itemStack.isEmpty() && nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).size() >= 6;
            if (bl2) {
                this.selectedPattern.set(-1);
                this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                this.updateOutputSlot(registryEntry);
            }
        } else {
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    public List<RoundelPattern> getRoundelPatterns() {
        return this.roundelPatterns;
    }

    public int getSelectedPattern() {
        return this.selectedPattern.get();
    }

    public void setInventoryChangeListener(Runnable inventoryChangeListener) {
        this.inventoryChangeListener = inventoryChangeListener;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == this.outputSlot.id) {
                if (!this.insertItem(itemStack2, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot == this.dyeSlot.id || slot == this.roundelSlot.id || slot == this.patternSlot.id ? !this.insertItem(itemStack2, 4, 40, false) : (itemStack2.getItem() instanceof RoundelItem ? !this.insertItem(itemStack2, this.roundelSlot.id, this.roundelSlot.id + 1, false) : (itemStack2.getItem() instanceof DyeItem ? !this.insertItem(itemStack2, this.dyeSlot.id, this.dyeSlot.id + 1, false) : (itemStack2.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof AbstractRoundelBlock) ? !this.insertItem(itemStack2, this.patternSlot.id, this.patternSlot.id + 1, false) : (slot >= 4 && slot < 31 ? !this.insertItem(itemStack2, 31, 40, false) : slot >= 31 && slot < 40 && !this.insertItem(itemStack2, 4, 31, false)))))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    private void updateOutputSlot(RoundelPattern pattern) {
        ItemStack itemStack = this.roundelSlot.getStack();
        ItemStack itemStack2 = this.dyeSlot.getStack();
        ItemStack patternStack = this.patternSlot.getStack();
        ItemStack itemStack3 = ItemStack.EMPTY;
        if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            NbtList nbtList;
            itemStack3 = itemStack.copyWithCount(1);
            DyeColor dyeColor = ((DyeItem)itemStack2.getItem()).getColor();
            NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack3);
            if (nbtCompound != null && nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)) {
                nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
            } else {
                nbtList = new NbtList();
                if (nbtCompound == null) {
                    nbtCompound = new NbtCompound();
                }
                nbtCompound.put("Patterns", nbtList);
            }
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound2.putString("Pattern", pattern.id().toString());
            nbtCompound2.putInt("Color", this.colorProperty.get() == 0 ? ColorHelper.Argb.getArgb(255, (int) (255 * dyeColor.getColorComponents()[0]), (int) (255 * dyeColor.getColorComponents()[1]),
                    (int) (255 * dyeColor.getColorComponents()[2])) : this.colorProperty.get());
            nbtCompound2.putBoolean("Emissive", true);
            if (this.patternSlot.getStack().getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof AbstractRoundelBlock)) {
                nbtCompound.put("DynamicTex", NbtHelper.fromBlockState(blockItem.getBlock().getDefaultState()));
            }
            if (pattern.equals(RoundelPatterns.BASE))
                nbtList.add(0, nbtCompound2);
            else
                nbtList.add(nbtCompound2);
            BlockItem.setBlockEntityNbt(itemStack3, AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE, nbtCompound);
        }
        if (!ItemStack.areEqual(itemStack3, this.outputSlot.getStack())) {
            this.outputSlot.setStackNoCallbacks(itemStack3);
        }
    }

    public Slot getRoundelSlot() {
        return this.roundelSlot;
    }

    public Slot getDyeSlot() {
        return this.dyeSlot;
    }

    public Slot getPatternSlot() {
        return this.patternSlot;
    }

    public Slot getOutputSlot() {
        return this.outputSlot;
    }
}
