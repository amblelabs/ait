package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class ToyotaSpinningRotorBlockEntity extends InteriorLinkableBlockEntity {
    SoundHandler sound;
    public ToyotaSpinningRotorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.TOYOTA_SPINNING_ROTOR_ENTITY_TYPE, pos, state);
        this.sound = SoundHandler.create(
                new PositionedLoopingSound(AITSounds.TICKING_LOOP, SoundCategory.BLOCKS, pos),
                new PositionedLoopingSound(AITSounds.TICKING_START, SoundCategory.BLOCKS, pos),
                new PositionedLoopingSound(AITSounds.TICKING_STOP, SoundCategory.BLOCKS, pos));
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

    public void tick(World world, BlockPos pos, BlockState blockState, ToyotaSpinningRotorBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld)) {
            if (!blockEntity.isLinked()) return;
            Tardis tardis = blockEntity.tardis().get();

            TravelHandlerBase.State state = tardis.travel().getState();

            this.age++;

            ANIM_STATE.startIfNotRunning(this.getAge());

            switch (state) {
                case DEMAT -> {
                    this.sound.stopSound(this.sound.findSoundByEvent(AITSounds.TICKING_STOP));
                    this.sound.stopSound(this.sound.findSoundByEvent(AITSounds.TICKING_LOOP));
                    if (!this.sound.isPlaying(AITSounds.TICKING_START)) {
                        this.sound.startSound(this.sound.findSoundByEvent(AITSounds.TICKING_START));
                    }
                }
                case FLIGHT -> {
                    this.sound.stopSound(this.sound.findSoundByEvent(AITSounds.TICKING_START));
                    this.sound.stopSound(this.sound.findSoundByEvent(AITSounds.TICKING_STOP));
                    if (!this.sound.isPlaying(AITSounds.TICKING_LOOP)) {
                        this.sound.startSound(this.sound.findSoundByEvent(AITSounds.TICKING_LOOP));
                    }
                }
                case MAT -> {
                    this.sound.stopSound(this.sound.findSoundByEvent(AITSounds.TICKING_LOOP));
                    this.sound.stopSound(this.sound.findSoundByEvent(AITSounds.TICKING_START));
                    if (!this.sound.isPlaying(AITSounds.TICKING_STOP)) {
                        this.sound.startSound(this.sound.findSoundByEvent(AITSounds.TICKING_STOP));
                    }
                }
                default -> this.sound.stopSounds();
            }
        }
    }
}