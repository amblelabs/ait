package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.RepairData;
import dev.amble.ait.tardis.v2.data.TravelData;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.block.BlockState;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class TravelHandler implements THandler, TardisEvents {

    public void crash(Tardis tardis) {
        TravelData travel = tardis.resolve(TravelData.ID);

        if (travel.state().get() != TravelData.State.FLIGHT || travel.crashing().get())
            return;

        travel.crashing().set(true);

        int power = travel.speed().get() + travel.hammerUses().get() + 1;

        travel.antigravs().set(false);
        travel.hammerUses().set(0);
        travel.speed().set(0);

        if (tardis.sequence().hasActiveSequence())
            tardis.sequence().setActiveSequence(null, true);

        tardis.door().setLocked(true);
        this.forceRemat();

        int repairTicks = 1200 * power;
        tardis.crash().setRepairTicks(repairTicks);

        if (repairTicks > RepairHandler.UNSTABLE_TICK_START_THRESHOLD) {
            tardis.crash().setState(RepairHandler.State.TOXIC);
        } else {
            tardis.crash().setState(RepairHandler.State.UNSTABLE);
        }

        this.handle(TardisEvents.crash(tardis, power));
    }

    private static final ParticleEffect REPAIR_PARTICLE = new DustColorTransitionParticleEffect(
            new Vector3f(0.75f, 0.85f, 0.75f), new Vector3f(0.15f, 0.25f, 0.15f), 3);

    @Override
    public void event$repairTick(Tardis tardis, MinecraftServer server, RepairData repair) {
        if (repair.state().get() != RepairData.State.TOXIC)
            return;

        CachedDirectedGlobalPos exteriorPos = tardis.resolve(TravelData.ID).position().get();

        BlockPos pos = exteriorPos.getPos();
        Vec3d center = pos.toCenterPos();

        // maybe spawning a particle each tick is not a good idea
        exteriorPos.getWorld().spawnParticles(REPAIR_PARTICLE,
                center.x, pos.getY() + 0.1f, center.z, 1,
                0.05D, 0.75D, 0.05D, 0.01D);
    }

    @Override
    public void event$disablePower(Tardis tardis) {
        TravelData travel = tardis.resolve(TravelData.ID);

        if (travel.state().get() == TravelData.State.FLIGHT)
            this.crash(tardis);

        travel.antigravs().set(false);

        this.updateExteriorState(tardis, false);
    }

    @Override
    public void event$enablePower(Tardis tardis) {
        this.updateExteriorState(tardis, true);
    }

    @Override
    public void event$alarmToll(Tardis tardis, AlarmHandler.@Nullable Alarm alarm) {
        TravelData travel = tardis.resolve(TravelData.ID);

        boolean doorOpen = tardis.door().isOpen();

        float volume = doorOpen ? 1f : 0.3f;
        float pitch = doorOpen ? 1f : 0.2f;

        CachedDirectedGlobalPos pos = travel.position().get();

        pos.getWorld().playSound(null, pos.getPos(),
                AITSounds.CLOISTER, SoundCategory.AMBIENT, volume, pitch);
    }

    private void updateExteriorState(Tardis tardis, boolean power) {
        TravelData travel = tardis.resolve(TravelData.ID);

        if (travel.state().get() != TravelData.State.LANDED)
            return;

        CachedDirectedGlobalPos pos = travel.position().get();
        World world = pos.getWorld();

        if (world == null)
            return;

        BlockState state = world.getBlockState(pos.getPos());

        if (!(state.getBlock() instanceof ExteriorBlock))
            return;

        world.setBlockState(pos.getPos(),
                state.with(ExteriorBlock.LEVEL_4, power ? 4 : 0));
    }
}
