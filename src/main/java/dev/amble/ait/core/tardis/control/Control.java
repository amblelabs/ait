package dev.amble.ait.core.tardis.control;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;

public class Control {

    public String id; // a name to represent the control

    public Control(String id) {
        this.setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        return false;
    }

    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
                             boolean leftClick) {
        return runServer(tardis, player, world, console);
    }

    public void addToControlSequence(Tardis tardis, ServerPlayerEntity player, BlockPos pos) {
        tardis.sequence().add(this, player, pos);

        if (AITMod.RANDOM.nextInt(0, 20) == 4) {
            tardis.loyalty().addLevel(player, 1);

            player.getServerWorld().spawnParticles(ParticleTypes.HEART, pos.toCenterPos().getX(),
                    pos.toCenterPos().getY() + 1, pos.toCenterPos().getZ(), 1, 0f, 1F, 0f, 5.0F);
        }
    }

    public SoundEvent getSound() {
        return AITSounds.XYZ;
    }

    public boolean requiresPower() {
        return true;
    }
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.ENGINE;
    }

    public void runAnimation(Tardis tardis, ServerPlayerEntity player, ServerWorld world) {
        // no animation
    }

    @Override
    public String toString() {
        return "Control{" + "id='" + id + '\'' + '}';
    }

    public long getDelayLength() {
        return 5;
    }

    public boolean shouldHaveDelay() {
        return true;
    }

    public boolean shouldHaveDelay(Tardis tardis) {
        return this.shouldHaveDelay();
    }

    public boolean ignoresSecurity() {
        return false;
    }

    public boolean canRun(Tardis tardis, ServerPlayerEntity user) {
        if ((this.requiresPower() && !tardis.fuel().hasPower()))
            return false;

        boolean security = tardis.stats().security().get();

        if (!this.ignoresSecurity() && security)
            return SecurityControl.hasMatchingKey(user, tardis);

        /*if (tardis.flight().isFlying())
            return false;*/

        SubSystem.IdLike dependent = this.requiredSubSystem();
        if (dependent != null) {
            return tardis.subsystems().get(dependent).isEnabled();
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || this.getClass() != o.getClass())
            return false;

        Control control = (Control) o;
        return this.id.equals(control.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
