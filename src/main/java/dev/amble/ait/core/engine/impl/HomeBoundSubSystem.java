package dev.amble.ait.core.engine.impl;

import java.util.List;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.engine.CoreInstallableSubSystem;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.data.Exclude;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Base for non-durable systems which may only remain installed while the TARDIS
 * is powered and stationary at its exact home. Individual implementations may
 * additionally require operational life support.
 */
public abstract class HomeBoundSubSystem extends SubSystem implements StructureHolder, CoreInstallableSubSystem {

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private Long holderPosition;

    protected HomeBoundSubSystem(IdLike id) {
        super(id);
    }

    @Override
    public MultiBlockStructure getStructure() {
        return MultiBlockStructure.EMPTY;
    }

    public boolean isInstalled() {
        return this.holderPosition != null;
    }

    public boolean isOperational() {
        return this.isServer() && this.isInstalled() && this.isEnabled() && this.meetsHomeConditions();
    }

    public boolean requiresLifeSupport() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled && !this.isInstalled())
            return;

        super.setEnabled(enabled);
    }

    @Override
    public boolean canInstall(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        return core != null && core.isPowered() && this.isServer()
                && core.system() != this.tardis().subsystems().lifeSupport() && this.meetsHomeConditions();
    }

    @Override
    public void prepareInstall(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        if (core == null)
            return;

        if (this.holderPosition != null && this.holderPosition != core.getPos().asLong())
            this.eject();

        this.holderPosition = core.getPos().asLong();
        this.sync();
    }

    @Override
    public void onInstalled(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        this.onInstallationChanged(true);
    }

    @Override
    public void onRemoved(GenericStructureSystemBlockEntity core, ItemStack stack) {
        if (core == null || this.holderPosition == null || this.holderPosition == core.getPos().asLong()) {
            this.holderPosition = null;
            this.onInstallationChanged(false);
            this.sync();
        }
    }

    public final void tickDormant(MinecraftServer server) {
        if (!this.isInstalled() || !this.isServer())
            return;

        if (this.hasInvalidLoadedHolder()) {
            this.clearMissingHolder();
            return;
        }

        if (!this.isEnabled() || !this.meetsHomeConditions()) {
            this.eject();
            return;
        }

        this.tickAtHome(server);
    }

    public final void validateNow() {
        if (!this.isInstalled() || !this.isServer())
            return;

        if (this.hasInvalidLoadedHolder()) {
            this.clearMissingHolder();
        } else if (!this.isEnabled() || !this.meetsHomeConditions()) {
            this.eject();
        }
    }

    public final void eject() {
        if (!this.isInstalled() || !this.isServer())
            return;

        GenericStructureSystemBlockEntity holder = this.findHolder(true);
        if (holder == null || !holder.holdsSystem(this)) {
            this.clearMissingHolder();
            return;
        }

        this.eject(holder);
    }

    /**
     * Removes the exact installed stack before an interior regeneration erases
     * its holder. This keeps these core-installed systems out of the legacy
     * {@link SubSystem#isReal()} restoration latch.
     */
    public final List<ItemStack> extractForInteriorChange() {
        if (!this.isInstalled() || !this.isServer())
            return List.of();

        GenericStructureSystemBlockEntity holder = this.findHolder(true);
        if (holder == null || !holder.holdsSystem(this)) {
            this.clearMissingHolder();
            return List.of();
        }

        ItemStack stack = holder.extractSystem();
        if (stack.isEmpty())
            return List.of();

        return List.of(stack, AITBlocks.GENERIC_SUBSYSTEM.asItem().getDefaultStack());
    }

    @Override
    public final List<ItemStack> toStacks() {
        return List.of();
    }

    protected void tickAtHome(MinecraftServer server) {
    }

    protected void onInstallationChanged(boolean installed) {
    }

    protected final ServerTardis serverTardis() {
        return this.tardis().asServer();
    }

    protected boolean meetsAdditionalConditions() {
        return true;
    }

    private boolean meetsHomeConditions() {
        if (!(this.tardis() instanceof ServerTardis tardis))
            return false;

        return TardisHomeUtil.isParkedAtExactHome(tardis)
                && tardis.fuel().hasPower()
                && (!this.requiresLifeSupport() || tardis.subsystems().lifeSupport().isUsable())
                && this.meetsAdditionalConditions();
    }

    @Nullable private GenericStructureSystemBlockEntity findHolder(boolean loadInterior) {
        if (this.holderPosition == null || !(this.tardis() instanceof ServerTardis tardis))
            return null;

        if (!loadInterior && !tardis.hasWorld())
            return null;

        BlockPos pos = BlockPos.fromLong(this.holderPosition);
        if (!loadInterior && !tardis.world().isChunkLoaded(pos))
            return null;

        BlockEntity blockEntity = tardis.world().getBlockEntity(pos);
        return blockEntity instanceof GenericStructureSystemBlockEntity generic ? generic : null;
    }

    private boolean hasInvalidLoadedHolder() {
        if (this.holderPosition == null || !(this.tardis() instanceof ServerTardis tardis)
                || !tardis.hasWorld())
            return false;

        BlockPos pos = BlockPos.fromLong(this.holderPosition);
        if (!tardis.world().isChunkLoaded(pos))
            return false;

        GenericStructureSystemBlockEntity holder = this.findHolder(false);
        return holder == null || !holder.holdsSystem(this);
    }

    private void eject(GenericStructureSystemBlockEntity holder) {
        ItemStack stack = holder.extractSystem();
        if (!stack.isEmpty())
            StackUtil.spawn(holder.getWorld(), holder.getPos().up(), stack,
                    HomeEntityCapture::excludeFromItemCapture);
    }

    private void clearMissingHolder() {
        this.holderPosition = null;
        if (this.isEnabled())
            this.setEnabled(false);
        this.onInstallationChanged(false);
        this.sync();
    }
}
