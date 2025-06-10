package dev.amble.ait.core.tardis.handler;

import java.util.Objects;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

public class SiegeHandler extends KeyedTardisComponent implements TardisTickable {

    public static final Identifier DEFAULT_TEXTURRE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/siege_mode.png");
    public static final Identifier BRICK_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/siege_mode_brick.png");
    public static final Identifier COMPANION_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/companion_cube.png");
    public static final Identifier APERTURE_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/weighted_cube.png");

    private static final Property<UUID> HELD_KEY = new Property<>(Property.UUID, "siege_held_uuid");
    private static final Property<Identifier> TEXTURE = new Property<>(Property.IDENTIFIER, "texture", DEFAULT_TEXTURRE);

    private static final BoolProperty ACTIVE = new BoolProperty("siege_mode", false);

    private final Value<UUID> heldKey = HELD_KEY.create(this);
    private final BoolValue active = ACTIVE.create(this);
    private final Value<Identifier> texture = TEXTURE.create(this);

    private int siegeTime;

    public SiegeHandler() {
        super(Id.SIEGE);
    }

    static {
        TardisEvents.DEMAT.register(tardis -> tardis.siege().isActive() ? TardisEvents.Interaction.FAIL : TardisEvents.Interaction.PASS);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            ServerTardisManager.getInstance().forEach(tardis -> {
                if (!tardis.siege().isActive())
                    return;

                if (!Objects.equals(tardis.siege().getHeldPlayerUUID(), player.getUuid()))
                    return;

                for (ItemStack itemStack : player.getInventory().main) {
                    if (itemStack.isOf(AITItems.SIEGE_ITEM)) {
                        if (tardis.getUuid().equals(SiegeTardisItem.getTardisIdStatic(itemStack))) {
                            player.getInventory().setStack(player.getInventory().getSlotWithStack(itemStack), Items.AIR.getDefaultStack());
                        }
                    }
                }
                SiegeTardisItem.placeTardis(tardis, SiegeTardisItem.fromEntity(player));
            });
        });
    }

    @Override
    public void onLoaded() {
        active.of(this, ACTIVE);
        heldKey.of(this, HELD_KEY);
        texture.of(this, TEXTURE);

        // fix old data using new UUID(0, 0) instead of null.
        UUID held = this.getHeldPlayerUUID();

        if (held != null && held.getMostSignificantBits() == 0 && held.getLeastSignificantBits() == 0)
            this.setSiegeBeingHeld(null);
    }

    public boolean isActive() {
        return active.get();
    }

    public boolean isSiegeBeingHeld() {
        return this.isActive() && heldKey.get() != null;
    }

    public UUID getHeldPlayerUUID() {
        return heldKey.get();
    }

    public void setSiegeBeingHeld(UUID playerId) {
        if (playerId != null) {
            this.tardis.door().closeDoors();
            this.tardis.door().setLocked(true);
            this.tardis.alarm().enable();
        }

        this.heldKey.set(playerId);
    }

    public void setActive(boolean siege) {
        if (this.tardis.getFuel() <= (0.01 * FuelHandler.TARDIS_MAX_FUEL))
            return; // The required amount of fuel to enable/disable siege mode

        SoundEvent sound;

        if (siege) {
            sound = AITSounds.SIEGE_ENABLE;
            this.tardis.door().closeDoors();
            this.tardis.door().setLocked(true);
            this.tardis.door().setDeadlocked(true);

            this.tardis.fuel().disablePower();

            TardisUtil.giveEffectToInteriorPlayers(this.tardis.asServer(),
                    new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, false, false));
        } else {
            sound = AITSounds.SIEGE_DISABLE;
            this.tardis.door().setDeadlocked(false);
            this.tardis.door().setLocked(false);

            this.tardis.alarm().disable();

            if (this.tardis.getExterior().findExteriorBlock().isEmpty())
                this.tardis.travel().placeExterior(false);

            this.siegeTime = 0;
        }

        tardis.getDesktop().playSoundAtEveryConsole(sound, SoundCategory.BLOCKS, 3f, 1f);

        this.tardis.removeFuel(0.01 * FuelHandler.TARDIS_MAX_FUEL * this.tardis.travel().instability());
        this.active.set(siege);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!this.active.get())
            return;

        this.siegeTime += 1;

        if (server.getTicks() % 10 == 0)
            return;

        boolean freeze = this.siegeTime > 60 * 20 && !this.isSiegeBeingHeld()
                && !this.tardis.subsystems().lifeSupport().isEnabled();

        this.tardis.asServer().world().getPlayers().forEach(player -> {
            if (!player.isAlive() || !player.canFreeze())
                return;

            if (freeze) {
                this.freeze(player);
            } else {
                this.unfreeze(player);
            }
        });
    }

    private void freeze(ServerPlayerEntity player) {
        int m = player.getFrozenTicks();
        if (m < 0) player.setFrozenTicks(5);
        player.setFrozenTicks(Math.min(player.getMinFreezeDamageTicks(), m + 5));
    }

    private void unfreeze(ServerPlayerEntity player) {
        if (player.getFrozenTicks() > player.getMinFreezeDamageTicks())
            player.setFrozenTicks(0);
    }

    public Value<Identifier> texture() {
        return texture;
    }
}
