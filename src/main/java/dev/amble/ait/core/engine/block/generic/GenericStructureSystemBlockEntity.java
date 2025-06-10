package dev.amble.ait.core.engine.block.generic;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.engine.block.multi.StructureSystemBlockEntity;
import dev.amble.ait.core.engine.item.SubSystemItem;
import dev.amble.ait.core.util.StackUtil;

/**
 * a mutable version of the structure system block entity
 * it can have its id changed
 * usually set by the SubSystemItem
 * @see SubSystemItem
 * @author duzo
 */
public class GenericStructureSystemBlockEntity extends StructureSystemBlockEntity {
    private ItemStack idSource;

    protected GenericStructureSystemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, null);
    }
    public GenericStructureSystemBlockEntity(BlockPos pos, BlockState state) {
        this(AITBlockEntityTypes.GENERIC_SUBSYSTEM_BLOCK_TYPE, pos, state);
    }

    @Override
    public ActionResult useOn(BlockState state, World world, boolean sneaking, PlayerEntity player, ItemStack hand) {
        if (hand.isEmpty()) {
            if (this.system() != null && this.idSource != null) {
                if (this.system() instanceof DurableSubSystem durable && (durable.isBroken() || durable.durability() < 1250)) {
                    player.sendMessage(Text.translatable("tardis.message.engine.system_is_weakened"), true);
                    return ActionResult.SUCCESS;
                }
                StackUtil.spawn(world, pos, this.idSource.copyAndEmpty());
                if (this.tardis().isPresent() && this.id() != null) {
                    system().setEnabled(false);
                }
                world.playSound(null, this.getPos(), AITSounds.WAYPOINT_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 0.1f);
                this.markDirty();
                this.id = null;
                return ActionResult.SUCCESS;
            }
        }

        if (world.isClient())
            return ActionResult.SUCCESS;

        if (hand.getItem() instanceof SubSystemItem link) {
            if (this.system() != null && this.idSource != null) {
                if (tardis() != null) {
                    system().setEnabled(false);
                }
                StackUtil.spawn(world, pos, this.idSource.copyAndEmpty());
            }
            this.setId(link.id());
            this.idSource = hand.copy();
            this.idSource.setCount(1);
            hand.decrement(1);
            world.playSound(null, this.getPos(), AITSounds.WAYPOINT_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }

        return super.useOn(state, world, sneaking, player, hand);
    }

    private void setId(SubSystem.IdLike id) {
        this.id = id;
        this.onChangeId();
    }

    protected StructureHolder getHolder() {
        if (!(this.system() instanceof StructureHolder holder)) return null;

        return holder;
    }

    protected void onChangeId() {
        this.processStructure();
        this.markDirty();
        this.sync();
    }

    public boolean hasSystem() {
        return this.id() != null;
    }

    @Override
    protected MultiBlockStructure getStructure() {
        StructureHolder holder = this.getHolder();
        if (holder == null) return null;

        return holder.getStructure();
    }

    @Override
    public boolean isStructureComplete(World world, BlockPos pos) {
        if (this.getStructure() == null) return false;

        return super.isStructureComplete(world, pos);
    }

    @Override
    protected boolean shouldRefresh(ServerWorld world, BlockPos pos) {
        if (this.getStructure() == null) return false;

        return super.shouldRefresh(world, pos);
    }

    @Override
    public void onLoseFluid() {
        if (this.system() == null) return;

        super.onLoseFluid();
    }

    @Override
    public void onBroken(World world, BlockPos pos) {
        super.onBroken(world, pos);

        if (world.isClient() || this.idSource == null) return;
        StackUtil.spawn(world, pos, this.idSource.copyAndEmpty());
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (this.idSource != null) {
            nbt.put("SourceStack", this.idSource.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("SourceStack")) {
            this.idSource = ItemStack.fromNbt(nbt.getCompound("SourceStack"));
        }
    }

    /**
     * @return the source stack that was used to set the id
     */
    public Optional<ItemStack> getSourceStack() {
        return Optional.ofNullable(this.idSource);
    }
}
