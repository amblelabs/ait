package dev.amble.ait.core.tardis.handler;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.api.TardisEvents;
import dev.amble.ait.api.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;

public class OvergrownHandler extends KeyedTardisComponent implements TardisTickable {
    private static final BoolProperty IS_OVERGROWN_PROPERTY = new BoolProperty("is_overgrown", false);
    private final BoolValue overgrown = IS_OVERGROWN_PROPERTY.create(this);
    @Exclude
    private static final int TIME_TO_OVERGROW = 24000;
    private int ticks = 24000;
    private boolean ticking = false;
    private int soundCooldown = 0;

    static {
        TardisEvents.USE_DOOR.register((tardis, interior, world, player, pos) -> {
            if (!tardis.overgrown().overgrown().get() || player == null)
                return DoorHandler.InteractionResult.CONTINUE;

            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            if (stack.getItem() instanceof ShearsItem) {
                player.swingHand(Hand.MAIN_HAND);
                tardis.overgrown().removeVegetation();
                stack.damage(1, player, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));

                TardisCriterions.VEGETATION.trigger(player);
                return DoorHandler.InteractionResult.BANG;
            }

            return DoorHandler.InteractionResult.KNOCK;
        });
    }

    public OvergrownHandler() {
        super(Id.OVERGROWN);
    }

    @Override
    public void onLoaded() {
        overgrown.of(this, IS_OVERGROWN_PROPERTY);
    }

    public BoolValue overgrown() {
        return overgrown;
    }

    public void removeVegetation() {
        overgrown.set(false);
        this.ticks = 0;
        this.ticking = false;
        this.soundCooldown = 0;
    }

    @Environment(EnvType.CLIENT)
    public Identifier getOvergrownTexture() {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();
        Identifier baseTexture = variant.texture();

        // FIXME what the fuck
        if (baseTexture.getPath().contains("police_box")) {
            return new Identifier("ait", "textures/blockentities/exteriors/police_box/overgrown.png");
        }

        return baseTexture.withSuffixedPath("_overgrown");
    }

    @Override
    public void tick(MinecraftServer server) {

        if (server.getTicks() % 20 != 0) return;

        Tardis tardis = this.tardis();

        if (!tardis.fuel().hasPower()) {
            if (tardis.isGrowth() || overgrown.get()) {
                this.ticking = false;
                playMoodySounds(tardis);
                return;
            }

            if (tardis.travel().getState() != TravelHandlerBase.State.LANDED) {
                this.ticking = false;
                return;
            }

            if (tardis.travel().getState() == TravelHandlerBase.State.FLIGHT) {
                overgrown.set(false);
                this.ticking = false;
                this.ticks = 0;
                return;
            }

            if (!this.ticking) {
                this.ticking = true;
                this.ticks = 0;
            }

            if (++this.ticks >= TIME_TO_OVERGROW) {
                overgrown.set(true);
                this.ticking = false;
            }
        } else {
            this.ticking = false;
        }
    }

    private void playMoodySounds(Tardis tardis) {
        if (!overgrown.get() || tardis == null)
            return;

        if (soundCooldown > 0) {
            soundCooldown--;
            return;
        }

        //idk why i did this tbh
        if (AITMod.RANDOM.nextFloat() < 0.005f) {
            SoundEvent moodySound = AITSounds.MOODY;
            tardis.getExterior().playSound(moodySound, SoundCategory.AMBIENT, 0.15f, 1.0f);
            soundCooldown = 400;
        }
    }
}
