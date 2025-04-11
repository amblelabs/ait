package dev.amble.ait.core.tardis.handler;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITStatusEffects;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

public class ShieldHandler extends KeyedTardisComponent implements TardisTickable {
    private static final BoolProperty IS_SHIELDED = new BoolProperty("is_shielded", false);
    private final BoolValue isShielded = IS_SHIELDED.create(this);
    public static final BoolProperty IS_VISUALLY_SHIELDED = new BoolProperty("is_visually_shielded", false);
    private final BoolValue isVisuallyShielded = IS_VISUALLY_SHIELDED.create(this);

    private int shieldAmbienceTicks = 0;

    public ShieldHandler() {
        super(Id.SHIELDS);
    }

    @Override
    public void onLoaded() {
        isShielded.of(this, IS_SHIELDED);
        isVisuallyShielded.of(this, IS_VISUALLY_SHIELDED);
    }

    public BoolValue shielded() {
        return isShielded;
    }

    public BoolValue visuallyShielded() {
        return isVisuallyShielded;
    }

    public void enable() {
        this.shielded().set(true);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, true, this.visuallyShielded().get());
    }

    public void disable() {
        this.shielded().set(false);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, false, this.visuallyShielded().get());
    }

    public void toggle() {
        if (this.shielded().get()) disable();
        else enable();
    }

    public void enableVisuals() {
        this.visuallyShielded().set(true);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, this.shielded().get(), true);
    }

    public void disableVisuals() {
        this.visuallyShielded().set(false);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, this.shielded().get(), false);
    }

    public void toggleVisuals() {
        if (this.visuallyShielded().get()) disableVisuals();
        else enableVisuals();
    }

    public void disableAll() {
        disableVisuals();
        disable();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!shielded().get() || !tardis.subsystems().shields().isEnabled() || tardis.subsystems().shields().isBroken())
            return;

        TravelHandler travel = tardis.travel();
        if (!tardis.fuel().hasPower()) {
            disableAll();
            return;
        }

        if (travel.getState() == TravelHandlerBase.State.FLIGHT)
            return;

        tardis.removeFuel(2 * travel.instability());

        CachedDirectedGlobalPos exterior = travel.position();
        World world = exterior.getWorld();
        BlockPos pos = exterior.getPos();
        Vec3d center = pos.toCenterPos();

        if (visuallyShielded().get()) {
            if (++shieldAmbienceTicks >= 44) {
                shieldAmbienceTicks = 0;
                tardis.getExterior().playSound(AITSounds.SHIELD_AMBIANCE, SoundCategory.BLOCKS, 2f, 0.7f);
            }
        }

        for (var entity : world.getOtherEntities(null, new Box(pos).expand(2.3))) {
            if (entity instanceof ServerPlayerEntity player && !canPush(player)) {
                if (entity.isSubmergedInWater()) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 15, 3, true, false, false));
                }
                if (world.getRegistryKey().equals(AITDimensions.SPACE)) {
                    player.addStatusEffect(new StatusEffectInstance(AITStatusEffects.OXYGENATED, 20, 1, true, false));
                }
                continue;
            }

            if (entity.squaredDistanceTo(center) > 8.7 * 8.7)
                continue;

            Vec3d repulsion = entity.getPos().subtract(center).normalize().multiply(0.65);
            entity.setVelocity(repulsion);
            entity.velocityDirty = true;
            entity.velocityModified = true;

            if (visuallyShielded().get() && entity instanceof ProjectileEntity projectile) {
                if (!world.isClient) {
                    world.playSound(null, pos, AITSounds.SHIELD_PUSH, SoundCategory.BLOCKS, 0.6f, 1.2f);
                }
            }
        }
    }

    private boolean canPush(ServerPlayerEntity entity) {
        return !(tardis.loyalty().get(entity).isOf(Loyalty.Type.COMPANION) || SecurityControl.hasMatchingKey(entity, this.tardis()));
    }
}
