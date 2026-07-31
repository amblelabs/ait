package dev.amble.ait.core.engine.block.generic;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.CoreBoundDurableSubSystem;
import dev.amble.ait.core.engine.CoreInstallableSubSystem;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.engine.block.multi.StructureSystemBlockEntity;
import dev.amble.ait.core.engine.item.SubSystemItem;
import dev.amble.ait.core.engine.registry.SubSystemRegistry;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.core.world.TardisServerWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * a mutable version of the structure system block entity
 * it can have its id changed
 * usually set by the SubSystemItem
 * @see SubSystemItem
 * @author duzo
 */
public class GenericStructureSystemBlockEntity extends StructureSystemBlockEntity {
    private static final String INSTALLATION_ID_KEY = "CoreInstallationId";

    private ItemStack idSource;
    private UUID installationId;
    private transient boolean lifecycleRegistered;

    protected GenericStructureSystemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, null);
    }
    public GenericStructureSystemBlockEntity(BlockPos pos, BlockState state) {
        this(AITBlockEntityTypes.GENERIC_SUBSYSTEM_BLOCK_TYPE, pos, state);
    }

    public ActionResult useOn(BlockState state, World world, boolean sneaking, PlayerEntity player, ItemStack hand) {
        if (!TardisServerWorld.isTardisDimension(world)) return ActionResult.CONSUME;
        if (hand.isEmpty()) {
            if (world.isClient())
                return this.idSource == null || this.idSource.isEmpty() ? ActionResult.PASS : ActionResult.SUCCESS;

            if (this.idSource != null && !this.idSource.isEmpty()) {
                SubSystem current = this.system();
                if (current instanceof DurableSubSystem durable
                        && (durable.isBroken() || durable.durability() < durable.maxDurability())) {
                    player.sendMessage(Text.translatable("tardis.message.engine.system_is_weakened"), true);
                    return ActionResult.SUCCESS;
                }
                StackUtil.spawn(world, pos, this.extractSystem());
                world.playSound(null, this.getPos(), AITSounds.WAYPOINT_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 0.1f);
                return ActionResult.SUCCESS;
            }
        }

        SubSystem.IdLike targetId = hand.getItem() instanceof SubSystemItem link
                ? link.id() : SubSystemRegistry.getInstance().get(hand);
        if (targetId == null)
            return ActionResult.PASS;
        if (world.isClient())
            return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity serverPlayer) || !this.isLinked())
            return ActionResult.CONSUME;

        SubSystem target = this.tardis().get().subsystems().get(targetId);
        if (target == null)
            return ActionResult.CONSUME;
        if (target instanceof DurableSubSystem && !(target instanceof CoreBoundDurableSubSystem))
            return ActionResult.SUCCESS;
        if (target instanceof CoreInstallableSubSystem installable
                && !installable.canInstall(this, serverPlayer, hand))
            return ActionResult.SUCCESS;

        if (this.idSource != null && !this.idSource.isEmpty())
            StackUtil.spawn(world, pos, this.extractSystem());

        if (target instanceof CoreInstallableSubSystem installable)
            installable.prepareInstall(this, serverPlayer, hand);

        this.idSource = hand.copy();
        this.idSource.setCount(1);
        target.readItemData(this.idSource);
        target.writeItemData(this.idSource);
        this.setId(targetId);
        hand.decrement(1);

        if (target instanceof CoreInstallableSubSystem installable) {
            installable.onInstalled(this, serverPlayer, this.idSource);
            this.lifecycleRegistered = true;
        }

        world.playSound(null, this.getPos(), AITSounds.WAYPOINT_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return ActionResult.SUCCESS;
    }

    private void setId(SubSystem.IdLike id) {
        this.installationId = null;
        this.lifecycleRegistered = false;
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
    public void tick(World world, BlockPos pos, BlockState state) {
        super.tick(world, pos, state);

        if (world.isClient() || this.lifecycleRegistered || this.idSource == null || this.idSource.isEmpty())
            return;

        SubSystem current = this.system();
        if (current instanceof CoreInstallableSubSystem installable) {
            installable.onCoreLoaded(this);
            this.lifecycleRegistered = true;
        }
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
        StackUtil.spawn(world, pos, this.extractSystem());
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (this.idSource != null) {
            nbt.put("SourceStack", this.idSource.writeNbt(new NbtCompound()));
        }
        if (this.installationId != null)
            nbt.putUuid(INSTALLATION_ID_KEY, this.installationId);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (!nbt.contains("SystemId"))
            this.id = null;

        this.idSource = nbt.contains("SourceStack")
                ? ItemStack.fromNbt(nbt.getCompound("SourceStack")) : null;
        this.installationId = nbt.containsUuid(INSTALLATION_ID_KEY)
                ? nbt.getUuid(INSTALLATION_ID_KEY) : null;
        this.lifecycleRegistered = false;
    }

    /**
     * @return the source stack that was used to set the id
     */
    public Optional<ItemStack> getSourceStack() {
        return Optional.ofNullable(this.idSource);
    }

    public UUID getOrCreateInstallationId() {
        if (this.installationId == null) {
            this.installationId = UUID.randomUUID();
            this.markDirty();
        }
        return this.installationId;
    }

    public Optional<UUID> getInstallationId() {
        return Optional.ofNullable(this.installationId);
    }

    public boolean holdsSystem(SubSystem system) {
        return system != null && this.id == system.getId() && this.idSource != null
                && !this.idSource.isEmpty() && this.idSource.isOf(system.asItem())
                && this.tardis() != null && system.tardis() != null
                && Objects.equals(system.tardis().getUuid(), this.tardis().getId());
    }

    /**
     * Removes the installed subsystem without spawning it. Callers can place the
     * returned exact stack elsewhere without duplicating its link data.
     */
    public ItemStack extractSystem() {
        if (this.idSource == null || this.idSource.isEmpty())
            return ItemStack.EMPTY;

        SubSystem current = this.id == null ? null : this.system();
        boolean ownsCurrent = !(current instanceof CoreBoundDurableSubSystem durable) || durable.ownsCore(this);
        ItemStack extracted = this.idSource.copyAndEmpty();
        this.idSource = null;
        if (current != null && ownsCurrent)
            current.writeItemData(extracted);
        boolean managedPower = false;
        if (current instanceof CoreInstallableSubSystem installable) {
            managedPower = installable.managesCorePowerState();
            installable.onRemoved(this, extracted);
        }
        if (ownsCurrent && !managedPower && current != null && current.isEnabled())
            current.setEnabled(false);
        this.installationId = null;
        this.lifecycleRegistered = false;
        this.id = null;
        this.markDirty();
        this.sync();
        return extracted;
    }
}
