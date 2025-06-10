package dev.amble.ait.core.tardis.control.sequences;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.registry.impl.SequenceRegistry;

public class SequenceHandler extends TardisComponent implements TardisTickable {
    @Exclude
    private RecentControls recent;

    private int ticks = 0;

    @Exclude
    private Sequence activeSequence;

    private static final Random random = Random.create();
    private UUID playerUUID;

    public SequenceHandler() {
        super(Id.SEQUENCE);
    }

    @Override
    protected void onInit(InitContext ctx) {
        recent = new RecentControls(tardis.getUuid());
        activeSequence = null;
    }

    public void setActivePlayer(ServerPlayerEntity player) {
        this.playerUUID = player.getUuid();
    }

    public ServerPlayerEntity getActivePlayer() {
        if (this.playerUUID == null)
            return null;

        ServerWorld world = this.tardis.asServer().world();

        if (world == null)
            return null;

        return (ServerPlayerEntity) world.getPlayerByUuid(this.playerUUID);
    }

    public void add(Control control, ServerPlayerEntity player, BlockPos console) {
        if (this.getActiveSequence() == null || recent == null)
            return;

        recent.add(control);
        ticks = 0;

        this.setActivePlayer(player);
        this.doesControlIndexMatch(control);
        this.compareToSequences(console);
    }

    public boolean doesControlIndexMatch(Control control) {
        if (recent == null || this.getActiveSequence() == null)
            return false;

        if (recent.indexOf(control) != this.getActiveSequence().getControls().indexOf(control)) {
            recent.remove(control);
            return false;
        }

        return true;
    }

    public boolean hasActiveSequence() {
        return this.activeSequence != null;
    }

    public void setActiveSequence(@Nullable Sequence sequence, boolean setTicksTo0) {
        if (setTicksTo0)
            this.ticks = 0;

        this.activeSequence = sequence;

        if (this.activeSequence == null)
            return;

        this.activeSequence.sendMessageToInteriorPlayers(TardisUtil.getPlayersInsideInterior(tardis.asServer()));
    }

    public void triggerRandomSequence(boolean setTicksTo0) {
        if (setTicksTo0)
            ticks = 0;

        int rand = random.nextBetween(0, SequenceRegistry.REGISTRY.size());
        Sequence sequence = SequenceRegistry.REGISTRY.get(rand);

        if (sequence == null)
            return;

        this.activeSequence = sequence;
        this.activeSequence.sendMessageToInteriorPlayers(TardisUtil.getPlayersInsideInterior(this.tardis.asServer()));

        this.tardis().getDesktop().playSoundAtEveryConsole(SoundEvents.BLOCK_BEACON_POWER_SELECT);
    }

    @Nullable public Sequence getActiveSequence() {
        return activeSequence;
    }

    private void compareToSequences(BlockPos console) {
        if (this.getActiveSequence() == null)
            return;

        if (this.recent == null)
            this.recent = new RecentControls(this.tardis().getUuid());

        if (this.getActiveSequence().isFinished(this.recent)) {
            recent.clear();
            this.getActiveSequence().execute(this.tardis(), this.getActivePlayer());

            this.doCompletedControlEffects(console);
            this.setActiveSequence(null, true);
        } else if (this.getActiveSequence().wasMissed(this.recent, ticks)) {
            recent.clear();
            this.getActiveSequence().executeMissed(this.tardis(), this.getActivePlayer());

            this.doMissedControlEffects(console);
            this.setActiveSequence(null, true);
        } else if (recent.size() >= this.getActiveSequence().getControls().size()) {
            recent.clear();
        }
    }

    private void doMissedControlEffects(@Nullable BlockPos console) {
        ServerWorld world = this.tardis.asServer().world();

        if (console == null) {
            this.tardis.getDesktop().getConsolePos().forEach(pos -> SequenceHandler.missedControlEffects(world, pos));
            return;
        }

        SequenceHandler.missedControlEffects(world, console);
    }

    public static void missedControlEffects(ServerWorld world, BlockPos pos) {
        TardisDesktop.playSoundAtConsole(world, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3f, 1f);
        Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0.0, 1.2f, 0.0);

        world.spawnParticles(ParticleTypes.SMALL_FLAME, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 20, 0.4F, 1F, 0.4F,
                5.0F);
        world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1, 0.4F, 1F, 0.4F,
                0.5F);
        world.spawnParticles(ParticleTypes.LAVA, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 7, 0.4F, 1F, 0.4F,
                0.5F);
        world.spawnParticles(ParticleTypes.FLASH, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 4, 0.4F, 1F, 0.4F, 5.0F);
        world.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 4f), vec3d.getX(), vec3d.getY(),
                vec3d.getZ(), 20, 0.0F, 1F, 0.0F, 2.0F);
    }

    private void doCompletedControlEffects(@Nullable BlockPos console) {
        ServerWorld world = this.tardis.asServer().world();

        if (console == null) {
            this.tardis.getDesktop().getConsolePos().forEach(pos -> SequenceHandler.completedControlEffects(world, pos));
            return;
        }

        SequenceHandler.completedControlEffects(world, console);
    }

    public static void completedControlEffects(ServerWorld world, BlockPos pos) {
        TardisDesktop.playSoundAtConsole(world, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 3f, 1f);
        Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0.0, 1.2f, 0.0);

        spawnControlParticles(world, vec3d);
        world.spawnParticles(ParticleTypes.HEART, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1, 0.4F, 1F, 0.4F, 0.5F);
    }

    public static void spawnControlParticles(ServerWorld world, Vec3d vec3d) {
        world.spawnParticles(ParticleTypes.GLOW, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 12, 0.4F, 1F, 0.4F, 5.0F);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 12, 0.4F, 1F, 0.4F,
                5.0F);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (this.getActiveSequence() == null)
            return;

        this.ticks++;
        if (this.ticks >= this.getActiveSequence().timeToFail()) {
            this.compareToSequences(null);

            this.recent.clear();
            this.ticks = 0;
        }
    }

    public boolean controlPartOfSequence(Control control) {
        if (this.getActiveSequence() == null)
            return false;

        return this.getActiveSequence().controlPartOfSequence(control);
    }
}
