package dev.amble.ait.core.tardis.handler;

import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.api.TardisEvents;
import dev.amble.ait.api.TardisTickable;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;

public class OvergrownHandler extends KeyedTardisComponent implements TardisTickable {
    private static final BoolProperty IS_OVERGROWN_PROPERTY = new BoolProperty("is_overgrown", false);
    private final BoolValue overgrown = IS_OVERGROWN_PROPERTY.create(this);

    @Exclude
    public static final int MAXIMUM_TICKS = 600;

    public static String TEXTURE_PATH = "textures/blockentities/exteriors/";
    private static Random random;
    private int ticks; // same as usual

    static {
        TardisEvents.USE_DOOR.register((tardis, interior, world, player, pos) -> {
            if (!tardis.overgrown().isOvergrown() || player == null)
                return DoorHandler.InteractionResult.CONTINUE;

            // if holding an axe then break off the vegetation
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            if (stack.getItem() instanceof AxeItem) {
                player.swingHand(Hand.MAIN_HAND);
                tardis.overgrown().removeVegetation();
                stack.setDamage(stack.getDamage() - 1);

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

    public int getTicks() {
        return this.ticks;
    }

    private void setTicks(int ticks) {
        this.ticks = ticks;
    }

    private void addTick() {
        this.setTicks(this.getTicks() + 1);
    }

    private boolean hasReachedMaxTicks() {
        return this.getTicks() >= MAXIMUM_TICKS;
    }

    public boolean isOvergrown() {
        return overgrown.get();
    }

    public void setOvergrown(boolean var) {
        overgrown.set(var);
    }

    public void removeVegetation() {
        this.setOvergrown(false);
        this.setTicks(0);
    }

    @Environment(EnvType.CLIENT)
    public Identifier getOvergrownTexture() {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();


        return variant.texture().withSuffixedPath("_overgrown"); // todo - best to have a fallback somehow but icr how to check if texture exists
    }

    public static Random random() {
        if (random == null)
            random = new Random();

        return random;
    }

    @Override
    public void tick(MinecraftServer server) {
        if (tardis.isGrowth())
            return;

        if (this.isOvergrown() && (this.tardis.travel().getState() == TravelHandlerBase.State.FLIGHT
                || this.tardis.travel().getState() == TravelHandlerBase.State.MAT)) {
            this.setOvergrown(false);
            this.setTicks(0);
            return;
        }

        if (this.isOvergrown() || this.tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            return;

        // We know the tardis is landed so we can start ticking away
        if (this.hasReachedMaxTicks())
            return;

        if (random().nextFloat() < 0.025f)
            this.addTick();
    }
}
