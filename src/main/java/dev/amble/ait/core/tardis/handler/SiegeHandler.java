package dev.amble.ait.core.tardis.handler;

import java.util.Objects;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.api.TardisEvents;
import dev.amble.ait.api.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.integer.IntProperty;
import dev.amble.ait.data.properties.integer.IntValue;

public class SiegeHandler extends KeyedTardisComponent implements TardisTickable {

    public static final Identifier DEFAULT_TEXTURRE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/siege_mode.png");
    public static final Identifier BRICK_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/siege_mode_brick.png");
    public static final Identifier COMPANION_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/companion_cube.png");
    public static final Identifier APERTURE_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/weighted_cube.png");

    private static final Property<UUID> HELD_KEY = new Property<>(Property.Type.UUID, "siege_held_uuid", new UUID(0, 0));
    private static final Property<Identifier> TEXTURE = new Property<>(Property.Type.IDENTIFIER, "texture", DEFAULT_TEXTURRE);

    private static final IntProperty SIEGE_TIME = new IntProperty("siege_time", 0);
    private static final BoolProperty ACTIVE = new BoolProperty("siege_mode", false);

    private final Value<UUID> heldKey = HELD_KEY.create(this);
    private final IntValue siegeTime = SIEGE_TIME.create(this);
    private final BoolValue active = ACTIVE.create(this);
    private final Value<Identifier> texture = TEXTURE.create(this);

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

                SiegeTardisItem.placeTardis(tardis, SiegeTardisItem.fromEntity(player));
            });
        });
    }

    @Override
    public void onLoaded() {
        active.of(this, ACTIVE);
        siegeTime.of(this, SIEGE_TIME);
        heldKey.of(this, HELD_KEY);
        texture.of(this, TEXTURE);
    }

    public boolean isActive() {
        return active.get();
    }

    public boolean isSiegeBeingHeld() {
        return heldKey.get() != null;
    }

    public UUID getHeldPlayerUUID() {
        if (!this.isSiegeBeingHeld())
            return null;

        return heldKey.get();
    }

    public void setSiegeBeingHeld(UUID playerId) {
        if (playerId != null)
            this.tardis.door().closeDoors();
            this.tardis.door().setLocked(true);
            this.tardis.alarm().enabled().set(true);

        this.heldKey.set(playerId);
    }

    public int getTimeInSiegeMode() {
        return this.siegeTime.get();
    }

    public void setActive(boolean siege) {
        if (this.tardis.getFuel() <= (0.01 * FuelHandler.TARDIS_MAX_FUEL))
            return; // The required amount of fuel to enable/disable siege mode

        SoundEvent sound;

        if (siege) {
            sound = AITSounds.SIEGE_ENABLE;
            this.tardis.door().closeDoors();
            this.tardis.door().setLocked(true);
            this.tardis.fuel().disablePower();

            TardisUtil.giveEffectToInteriorPlayers(this.tardis.asServer(),
                    new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, false, false));
        } else {
            sound = AITSounds.SIEGE_DISABLE;
            this.tardis.door().setDeadlocked(false);
            this.tardis.door().setLocked(false);
            this.tardis.alarm().enabled().set(false);

            if (this.tardis.getExterior().findExteriorBlock().isEmpty())
                this.tardis.travel().placeExterior(false);
        }

        for (BlockPos console : this.tardis.getDesktop().getConsolePos()) {
            TardisDesktop.playSoundAtConsole(tardis.asServer().getInteriorWorld(), console, sound, SoundCategory.BLOCKS, 3f, 1f);
        }

        this.tardis.removeFuel(0.01 * FuelHandler.TARDIS_MAX_FUEL * this.tardis.travel().instability());
        this.tardis().door().closeDoors();
        this.tardis.door().setLocked(true);
        this.active.set(siege);

    }

    @Override
    public void tick(MinecraftServer server) {
        if (!this.active.get())
            return;

        this.siegeTime.flatMap(i -> this.active.get() ? i + 1 : 0);

        if (server.getTicks() % 20 != 0)
            return;

        boolean isHeld = this.isSiegeBeingHeld();
        this.tardis.door().setDeadlocked(true);

        if (isHeld && this.tardis.getExterior().findExteriorBlock().isPresent())
            this.setSiegeBeingHeld(null);
        this.tardis.door().closeDoors();
        this.tardis.door().locked();
        boolean freeze = !isHeld && this.getTimeInSiegeMode() > 60 * 20 && !this.tardis.subsystems().lifeSupport().isEnabled();

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(this.tardis.asServer())) {
            if (!player.isAlive())
                continue;

            if (!freeze || player.canFreeze()) {
                this.unfreeze(player);
                continue;
            }

            this.freeze(player);
        }
    }

    private void freeze(ServerPlayerEntity player) {
        if (player.getFrozenTicks() < player.getMinFreezeDamageTicks())
            player.setFrozenTicks(player.getMinFreezeDamageTicks());

        player.setFrozenTicks(player.getFrozenTicks() + 2);
    }

    private void unfreeze(ServerPlayerEntity player) {
        if (player.getFrozenTicks() > player.getMinFreezeDamageTicks())
            player.setFrozenTicks(0);
    }

    public Value<Identifier> texture() {
        return texture;
    }
}
