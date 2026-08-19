package dev.amble.ait.core.tardis.handler;

import static dev.amble.ait.core.engine.SubSystem.Id.GRAVITATIONAL;

import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.impl.GravitationalCircuit;
import dev.amble.ait.core.entities.FallingTardisEntity;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

public class RealFlightHandler extends KeyedTardisComponent implements TardisTickable {

    private static final BoolProperty IS_FALLING = new BoolProperty("falling", false);
    private static final BoolProperty FLYING = new BoolProperty("flying", false);
    private static final BoolProperty SHOULD_FALL = new BoolProperty("should_fall", false);

    private final BoolValue falling = IS_FALLING.create(this);
    private final BoolValue flying = FLYING.create(this);
    private final BoolValue shouldFall = SHOULD_FALL.create(this);

    static {
        TardisEvents.DEMAT.register(tardis -> {
            tardis.flight().flying.set(false);
            return tardis.flight().falling().get() ? TardisEvents.Interaction.FAIL : TardisEvents.Interaction.PASS;
        });
    }

    public RealFlightHandler() {
        super(Id.FLIGHT);
    }

    @Override
    public void onLoaded() {
        falling.of(this, IS_FALLING);
        flying.of(this, FLYING);
        shouldFall.of(this, SHOULD_FALL);
    }

    public boolean isFlying() {
        return flying.get();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (this.falling.get())
            this.tardis.door().setLocked(true);
    }

    public void tickFlight(ServerPlayerEntity player, BlockPos pos) {
        tardis.travel().forcePosition(cached -> cached.pos(pos)
                .rotation(DirectionControl.getGeneralizedRotation(
                        RotationPropertyHelper.fromYaw(player.getYaw()))));
        if (player.age % 20 != 0) {
            GravitationalCircuit circuit = tardis.subsystems().get(GRAVITATIONAL);
            if (circuit.isEnabled()) {
                circuit.removeDurability(0.05f); // it takes wayyyy too much away from the gravitational circuit,
                // it should be a more negligible amount so it doesnt run out so quick
            }
        }
    }

    public void onLanding(ServerWorld world, BlockPos pos) {
        this.tardis.travel().forcePosition(cached -> cached.world(world.getRegistryKey()).pos(pos));

        this.falling.set(false);
        this.tardis.door().setLocked(this.tardis.door().previouslyLocked().get());
        this.tardis.door().setDeadlocked(false);

        world.playSound(null, pos, AITSounds.LAND_THUD, SoundCategory.BLOCKS);

        tardis.getDesktop().playSoundAtEveryConsole(AITSounds.LAND_THUD, SoundCategory.BLOCKS);
        TardisEvents.LANDED.invoker().onLanded(tardis);
    }

    public void onStartFalling(ServerWorld world, BlockState state, BlockPos pos) {
        this.falling.set(true);
        TardisEvents.START_FALLING.invoker().onStartFall(tardis);

        FallingTardisEntity.spawnFromBlock(world, pos, state);
    }

    public void enterFlight(ServerPlayerEntity player) {
        if (!AITMod.CONFIG.rwfEnabled) return;
        this.tardis.door().closeDoors();
        this.tardis().travel().autopilot(false);
        this.tardis.travel().handbrake(true);
        this.tardis().setRefueling(false);
        this.flying.set(true);

        FlightTardisEntity entity = FlightTardisEntity.createAndSpawn(
                player, this.tardis.asServer());

        TardisUtil.teleportOutside(tardis, player);

        Scheduler.get().runTaskLater(() -> {
            player.startRiding(entity);
        }, TaskStage.END_SERVER_TICK, TimeUnit.TICKS, 2);

        tardis.travel().finishDemat();
    }

    public void exitFlight(ServerPlayerEntity player) {
        this.flying.set(false);

        player.setInvisible(false);
        player.setInvulnerable(false);

        tardis.travel().forcePosition(cached -> cached.rotation(DirectionControl.getGeneralizedRotation(
                        RotationPropertyHelper.fromYaw(player.getYaw())))
                .world(player.getServerWorld()));
        tardis.travel().placeExterior(false);

        tardis.travel().finishRemat();
    }

    public BoolValue falling() {
        return falling;
    }

    public BoolValue flying() {
        return flying;
    }

    public BoolValue shouldFall() {
        return shouldFall;
    }
}
