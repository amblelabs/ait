package dev.amble.ait.core.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.data.Exclude;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * A durable subsystem whose mutable state belongs to one physical subsystem
 * core at a time.
 */
public abstract class CoreBoundDurableSubSystem extends DurableSubSystem implements CoreInstallableSubSystem {

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private UUID coreInstallationId;

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private BlockPos coreInstallationPos;

    protected CoreBoundDurableSubSystem(IdLike id) {
        super(id);
    }

    @Override
    public boolean canInstall(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        if (this.coreInstallationId == null)
            return true;

        if (this.ownsCore(core))
            return true;

        return !this.hasLiveBoundCore(core);
    }

    @Override
    public void onInstalled(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        this.bind(core);
    }

    @Override
    public void onCoreLoaded(GenericStructureSystemBlockEntity core) {
        if (core == null || !core.holdsSystem(this))
            return;

        UUID installationId = core.getOrCreateInstallationId();
        if (this.coreInstallationId == null
                || Objects.equals(this.coreInstallationId, installationId)
                || Objects.equals(this.coreInstallationPos, core.getPos())) {
            this.bind(core);
            return;
        }

        if (!this.hasLiveBoundCore(core))
            this.bind(core);
    }

    @Override
    public void onRemoved(GenericStructureSystemBlockEntity core, ItemStack stack) {
        if (core == null || this.coreInstallationId == null
                || !Objects.equals(this.coreInstallationId, core.getInstallationId().orElse(null)))
            return;

        this.clearCoreInstallation();
    }

    public boolean ownsCore(GenericStructureSystemBlockEntity core) {
        if (core == null || !core.holdsSystem(this))
            return false;

        if (this.coreInstallationId == null || Objects.equals(this.coreInstallationPos, core.getPos()))
            this.bind(core);

        boolean owns = Objects.equals(this.coreInstallationId, core.getInstallationId().orElse(null));
        if (owns && !Objects.equals(this.coreInstallationPos, core.getPos())) {
            this.coreInstallationPos = core.getPos().toImmutable();
            this.sync();
        }
        return owns;
    }

    public boolean isInstalledInCore() {
        return this.coreInstallationPos != null;
    }

    /**
     * Removes the exact installed item before an interior regeneration erases
     * its block entity, while retaining the structure materials restored by the
     * legacy subsystem flow.
     */
    public List<ItemStack> extractForInteriorChange() {
        if (!this.isInstalledInCore() || !this.isServer())
            return List.of();

        GenericStructureSystemBlockEntity holder = this.findHolder(true);
        if (holder == null || !holder.holdsSystem(this)) {
            this.clearCoreInstallation();
            return List.of();
        }

        ItemStack stack = holder.extractSystem();
        if (stack.isEmpty())
            return List.of();

        List<ItemStack> stacks = new ArrayList<>();
        if (this instanceof StructureHolder structureHolder && structureHolder.getStructure() != null
                && !structureHolder.getStructure().isEmpty())
            stacks.addAll(structureHolder.getStructure().toStacks());
        stacks.add(stack);
        stacks.add(AITBlocks.GENERIC_SUBSYSTEM.asItem().getDefaultStack());
        return stacks;
    }

    public void clearCoreInstallation() {
        if (this.coreInstallationId == null && this.coreInstallationPos == null)
            return;

        this.coreInstallationId = null;
        this.coreInstallationPos = null;
        this.sync();
    }

    private void bind(GenericStructureSystemBlockEntity core) {
        UUID installationId = core.getOrCreateInstallationId();
        BlockPos position = core.getPos().toImmutable();
        if (Objects.equals(this.coreInstallationId, installationId)
                && Objects.equals(this.coreInstallationPos, position))
            return;

        this.coreInstallationId = installationId;
        this.coreInstallationPos = position;
        this.sync();
    }

    @Nullable private GenericStructureSystemBlockEntity findHolder(boolean loadInterior) {
        if (this.coreInstallationPos == null || !(this.tardis() instanceof ServerTardis tardis))
            return null;

        if (!loadInterior && !tardis.hasWorld())
            return null;

        if (!loadInterior && !tardis.world().isChunkLoaded(this.coreInstallationPos))
            return null;

        BlockEntity blockEntity = tardis.world().getBlockEntity(this.coreInstallationPos);
        return blockEntity instanceof GenericStructureSystemBlockEntity generic ? generic : null;
    }

    private boolean hasLiveBoundCore(GenericStructureSystemBlockEntity context) {
        if (this.coreInstallationPos == null) {
            this.clearCoreInstallation();
            return false;
        }

        if (!(context.getWorld() instanceof ServerWorld world)
                || !world.isChunkLoaded(this.coreInstallationPos))
            return true;

        BlockEntity blockEntity = world.getBlockEntity(this.coreInstallationPos);
        if (!(blockEntity instanceof GenericStructureSystemBlockEntity owner) || !owner.holdsSystem(this)) {
            this.clearCoreInstallation();
            return false;
        }

        UUID ownerId = owner.getOrCreateInstallationId();
        if (!Objects.equals(this.coreInstallationId, ownerId))
            this.bind(owner);

        return true;
    }
}
