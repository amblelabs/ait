package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.AnimationState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.tardis.Tardis;

public class ToyotaSpinningRotorBlockEntity extends InteriorLinkableBlockEntity implements BlockEntityTicker<ToyotaSpinningRotorBlockEntity> {
    public ToyotaSpinningRotorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.TOYOTA_SPINNING_ROTOR_ENTITY_TYPE, pos, state);
    }
    public final AnimationState ANIM_STATE = new AnimationState();

    public int age;

    public int getAge() {
        return age;
    }

    @Override
    public void onLinked() {
        if (this.tardis().isEmpty())
            return;

        Tardis tardis = this.tardis().get();

        if (tardis instanceof ClientTardis)
            return;

        tardis.getDesktop().getConsolePos().add(this.pos);
        tardis.asServer().markDirty(tardis.getDesktop());
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, ToyotaSpinningRotorBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) {
            this.age++;

            ANIM_STATE.startIfNotRunning(this.age);
            return;
        }
    }
}