package dev.amble.ait.core.entities;


import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.entities.base.DummyAmbientEntity;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DalekShipEntity extends DummyAmbientEntity implements ISpaceImmune {
    private int interactAmount = 0;
    private int ambianceTimer = 0;

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
            if (this.ambianceTimer-- <= 0) {
                playSound(
                        AITSounds.DALEK_AMBIANCE,
                        0.6f,
                        1.0f
                );
                this.ambianceTimer = 40;
            }
        }
    }
}
