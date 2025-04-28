package dev.amble.ait.core.entities;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.entities.base.DummyAmbientEntity;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;

public class DalekShipEntity extends DummyAmbientEntity implements ISpaceImmune {
    private int interactAmount = 0;
    private int ambientSoundCooldown = 0;
    private int currentSoundIndex = 0;

    private static final SoundEvent[] RIFT_SOUNDS = {
            AITSounds.DALEK_AMBIANCE,
            AITSounds.DALEK_AMBIANCE,
            AITSounds.DALEK_AMBIANCE,
    };

    private static final int[] RIFT_DURATIONS = {
            15 * 20,
            13 * 20,
            14 * 20
    };

    public DalekShipEntity(EntityType<?> type, World world) {
        super(AITEntityTypes.DALEK_SHIP_ENTITY_TYPE, world);
    }

    @Override
    public final ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient()) return ActionResult.SUCCESS;

        interactAmount += 1;

        if (interactAmount >= 3) {

            // run animation

            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            if (ambientSoundCooldown > 0) {
                ambientSoundCooldown--;
            } else {
                this.getWorld().playSound(null, this.getBlockPos(), RIFT_SOUNDS[currentSoundIndex], SoundCategory.AMBIENT, 1.0f, 1.0f);
                ambientSoundCooldown = RIFT_DURATIONS[currentSoundIndex];
                currentSoundIndex = (currentSoundIndex + 1) % RIFT_SOUNDS.length;
            }
        }
    }
}
