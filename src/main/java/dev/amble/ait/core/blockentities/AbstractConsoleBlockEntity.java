package dev.amble.ait.core.blockentities;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;

public abstract class AbstractConsoleBlockEntity extends InteriorLinkableBlockEntity implements Inventory,
        NamedScreenHandlerFactory,
        Nameable {

    private ContainerLock lock = ContainerLock.EMPTY;
    @Nullable private Text customName;

    public AbstractConsoleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lock = ContainerLock.fromNbt(nbt);
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        this.lock.writeNbt(nbt);
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getContainerName();
    }

    @Override
    public Text getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable public Text getCustomName() {
        return this.customName;
    }

    protected abstract Text getContainerName();

    public boolean checkUnlocked(PlayerEntity player) {
        return LockableContainerBlockEntity.checkUnlocked(player, this.lock, this.getDisplayName());
    }

    public static boolean checkUnlocked(PlayerEntity player, ContainerLock lock, Text containerName) {
        if (player.isSpectator() || lock.canOpen(player.getMainHandStack())) {
            return true;
        }
        player.sendMessage(Text.translatable("container.isLocked", containerName), true);
        player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return false;
    }

    @Override
    @Nullable public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            return this.createScreenHandler(i, playerInventory);
        }
        return null;
    }

    protected abstract ScreenHandler createScreenHandler(int var1, PlayerInventory var2);
}
