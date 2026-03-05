package dev.amble.ait.data.preset;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.sounds.flight.FlightSoundRegistry;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.handler.ExtraHandler;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.LoyaltyHandler;
import dev.amble.ait.core.tardis.handler.ServerHumHandler;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.handler.SubSystemHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.manager.TardisBuilder;
import dev.amble.ait.core.tardis.vortex.reference.VortexReference;
import dev.amble.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.hum.Hum;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.HumRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

/**
 * Handles the server-side logic for creating a TARDIS with a preset.
 */
public class TardisPresetHandler {
    public static final Identifier CONFIRM_PRESET = AITMod.id("confirm_preset");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(CONFIRM_PRESET,
                (server, player, handler, buf, responseSender) -> {
                    Identifier presetId = buf.readIdentifier();
                    BlockPos placePos = buf.readBlockPos();
                    int facingHorizontal = buf.readInt();

                    server.execute(() -> createTardisWithPreset(player, presetId, placePos, facingHorizontal));
                });
    }

    private static void createTardisWithPreset(ServerPlayerEntity player, Identifier presetId, 
                                                BlockPos placePos, int facingHorizontal) {
        if (!(player.getWorld() instanceof ServerWorld serverWorld))
            return;

        // Check if player has the creative tardis item
        if (!player.getMainHandStack().isOf(AITItems.TARDIS_ITEM) && 
            !player.getOffHandStack().isOf(AITItems.TARDIS_ITEM)) {
            return;
        }

        // Get the preset
        TardisPreset preset = TardisPresetRegistry.getInstance().get(presetId);
        if (preset == null) {
            preset = TardisPresetRegistry.HARTNELL;
        }

        // Validate position
        BlockPos actualPos = serverWorld.getBlockState(placePos).isReplaceable() ? placePos : placePos.up();

        Direction facing = Direction.fromHorizontal(facingHorizontal);
        int rotation = DirectionControl.getGeneralizedRotation(
                RotationPropertyHelper.fromYaw(facing.asRotation()));

        CachedDirectedGlobalPos pos = CachedDirectedGlobalPos.create(serverWorld, actualPos, (byte) rotation);

        // Get fallback values from Hartnell preset (using registry fallback to handle loading order)
        TardisPreset fallback = TardisPresetRegistry.getInstance().fallback();
        if (fallback == null) {
            fallback = preset; // Use the selected preset as fallback if hartnell isn't loaded
        }

        final TardisPreset resolvedFallback = fallback;

        // Resolve exterior
        ExteriorVariantSchema exterior = preset.exterior()
                .map(id -> ExteriorVariantRegistry.getInstance().get(id))
                .orElseGet(() -> resolvedFallback.exterior()
                        .map(id -> ExteriorVariantRegistry.getInstance().get(id))
                        .orElseGet(() -> ExteriorVariantRegistry.getInstance().getRandom()));

        // Resolve desktop
        TardisDesktopSchema desktop = preset.desktop()
                .map(id -> DesktopRegistry.getInstance().get(id))
                .orElseGet(() -> resolvedFallback.desktop()
                        .map(id -> DesktopRegistry.getInstance().get(id))
                        .orElseGet(() -> DesktopRegistry.getInstance().getRandom()));

        // Resolve hum
        final Hum hum = preset.hum()
                .map(id -> HumRegistry.getInstance().get(id))
                .orElseGet(() -> resolvedFallback.hum()
                        .map(id -> HumRegistry.getInstance().get(id))
                        .orElse(HumRegistry.CORAL));

        // Resolve vortex
        final VortexReference vortex = preset.vortex()
                .map(id -> VortexReferenceRegistry.getInstance().get(id))
                .orElseGet(() -> resolvedFallback.vortex()
                        .map(id -> VortexReferenceRegistry.getInstance().get(id))
                        .orElse(VortexReferenceRegistry.TOYOTA));

        // Resolve flight sound
        final FlightSound flightSound = preset.flightSound()
                .map(id -> FlightSoundRegistry.getInstance().get(id))
                .orElseGet(() -> resolvedFallback.flightSound()
                        .map(id -> FlightSoundRegistry.getInstance().get(id))
                        .orElse(FlightSoundRegistry.DEFAULT));

        // Resolve takeoff animation ID
        final Identifier takeoffAnimId = preset.takeoffSound()
                .orElseGet(() -> resolvedFallback.takeoffSound()
                        .orElse(TardisAnimationRegistry.DEFAULT_DEMAT));

        // Resolve landing animation ID
        final Identifier landingAnimId = preset.landingSound()
                .orElseGet(() -> resolvedFallback.landingSound()
                        .orElse(TardisAnimationRegistry.DEFAULT_MAT));

        // Resolve console variant ID for later use (applied when console links)
        final Identifier consoleVariantId = preset.console()
                .orElseGet(() -> resolvedFallback.console().orElse(null));

        // Build the TARDIS
        TardisBuilder builder = new TardisBuilder().at(pos)
                .owner(player)
                .exterior(exterior)
                .desktop(desktop)
                .<FuelHandler>with(TardisComponent.Id.FUEL, fuel -> {
                    fuel.setCurrentFuel(fuel.getMaxFuel());
                    fuel.enablePower();
                })
                .with(TardisComponent.Id.SUBSYSTEM, SubSystemHandler::repairAll)
                .<LoyaltyHandler>with(TardisComponent.Id.LOYALTY, loyalty -> {
                    loyalty.setMessageEnabled(false);
                    loyalty.set(player, new Loyalty(Loyalty.Type.OWNER));
                    loyalty.setMessageEnabled(true);
                })
                .<ServerHumHandler>with(TardisComponent.Id.HUM, humHandler -> humHandler.set(hum))
                .<StatsHandler>with(TardisComponent.Id.STATS, stats -> {
                    stats.setVortexEffects(vortex);
                    stats.setFlightEffects(flightSound);
                });

        ServerTardis created = ServerTardisManager.getInstance().create(builder);

        if (created == null) {
            player.sendMessage(Text.translatable("message.ait.max_tardises"), true);
            return;
        }

        // Set pending console variant after TARDIS is created (handlers now exist)
        if (consoleVariantId != null) {
            created.extra().setPendingConsoleVariant(consoleVariantId);
        }

        // Set animations
        created.travel().setAnimationFor(TravelHandlerBase.State.DEMAT, takeoffAnimId);
        created.travel().setAnimationFor(TravelHandlerBase.State.MAT, landingAnimId);

        player.sendMessage(Text.translatable("message.ait.unlocked_all", 
                Text.translatable("message.ait.all_types").formatted(Formatting.GREEN))
                .formatted(Formatting.WHITE), false);

        // Consume the item
        if (player.getMainHandStack().isOf(AITItems.TARDIS_ITEM)) {
            player.getMainHandStack().decrement(1);
        } else if (player.getOffHandStack().isOf(AITItems.TARDIS_ITEM)) {
            player.getOffHandStack().decrement(1);
        }

        player.getItemCooldownManager().set(AITItems.TARDIS_ITEM, 20);
    }
}
