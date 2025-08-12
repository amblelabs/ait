package dev.amble.ait.core.blockentities.decoration;

import java.util.Objects;

import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.core.AITBlockEntityTypes;

public class AITJukeboxBlockEntity extends BlockEntity implements Clearable, SingleStackInventory {
    private static final int SECOND_PER_TICK = 20;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    private int ticksThisSecond;
    private long tickCount;
    private long recordStartTick;
    private boolean isPlaying;

    public AITJukeboxBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.JUKEBOX, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("RecordItem", NbtElement.COMPOUND_TYPE)) {
            this.inventory.set(0, ItemStack.fromNbt(nbt.getCompound("RecordItem")));
        }

        this.isPlaying = nbt.getBoolean("IsPlaying");
        this.recordStartTick = nbt.getLong("RecordStartTick");
        this.tickCount = nbt.getLong("TickCount");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.getStack().isEmpty()) {
            nbt.put("RecordItem", this.getStack().writeNbt(new NbtCompound()));
        }

        nbt.putBoolean("IsPlaying", this.isPlaying);
        nbt.putLong("RecordStartTick", this.recordStartTick);
        nbt.putLong("TickCount", this.tickCount);
    }

    public boolean isPlayingRecord() {
        return !this.getStack().isEmpty() && this.isPlaying;
    }

    private void updateState(@Nullable Entity entity, boolean hasRecord) {
        if (this.world.getBlockState(this.getPos()) == this.getCachedState()) {
            this.world.setBlockState(this.getPos(), this.getCachedState().with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(hasRecord)), Block.NOTIFY_LISTENERS);
            this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(entity, this.getCachedState()));
        }
    }

    @VisibleForTesting
    public void startPlaying() {
        this.recordStartTick = this.tickCount;
        this.isPlaying = true;
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.world.syncWorldEvent(null, WorldEvents.JUKEBOX_STARTS_PLAYING, this.getPos(), Item.getRawId(this.getStack().getItem()));
        this.markDirty();
    }

    private void stopPlaying() {
        this.isPlaying = false;
        this.world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.world.syncWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, this.getPos(), 0);
        this.markDirty();
    }

    private void tick(World world, BlockPos pos, BlockState state) {
        this.ticksThisSecond++;
        if (this.isPlayingRecord() && this.getStack().getItem() instanceof MusicDiscItem musicDiscItem) {
            if (this.isSongFinished(musicDiscItem)) {
                this.stopPlaying();
            } else if (this.hasSecondPassed()) {
                this.ticksThisSecond = 0;
                world.emitGameEvent(GameEvent.JUKEBOX_PLAY, pos, GameEvent.Emitter.of(state));
                this.spawnNoteParticle(world, pos);
            }
        }

        this.tickCount++;
    }

    private boolean isSongFinished(MusicDiscItem musicDisc) {
        return this.tickCount >= this.recordStartTick + (long)musicDisc.getSongLengthInTicks() + 20L;
    }

    private boolean hasSecondPassed() {
        return this.ticksThisSecond >= 20;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = (ItemStack)Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.updateState(null, false);
            this.stopPlaying();
        }

        return itemStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (stack.isIn(ItemTags.MUSIC_DISCS) && this.world != null) {
            this.inventory.set(slot, stack);
            this.updateState(null, true);
            this.startPlaying();
        }
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isIn(ItemTags.MUSIC_DISCS) && this.getStack(slot).isEmpty();
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return hopperInventory.containsAny(ItemStack::isEmpty);
    }

    private void spawnNoteParticle(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) {
            Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0.0, 1.2F, 0.0);
            float f = (float)world.getRandom().nextInt(4) / 24.0F;
            serverWorld.spawnParticles(ParticleTypes.NOTE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0, (double)f, 0.0, 0.0, 1.0);
        }
    }

    public void dropRecord() {
        if (this.world != null && !this.world.isClient) {
            BlockPos blockPos = this.getPos();
            ItemStack itemStack = this.getStack();
            if (!itemStack.isEmpty()) {
                this.removeStack();
                Vec3d vec3d = Vec3d.add(blockPos, 0.5, 1.01, 0.5).addRandom(this.world.random, 0.7F);
                ItemStack itemStack2 = itemStack.copy();
                ItemEntity itemEntity = new ItemEntity(this.world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack2);
                itemEntity.setToDefaultPickupDelay();
                this.world.spawnEntity(itemEntity);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, AITJukeboxBlockEntity blockEntity) {
        blockEntity.tick(world, pos, state);
    }

    @VisibleForTesting
    public void setDisc(ItemStack stack) {
        this.inventory.set(0, stack);
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.markDirty();
    }
}
