package dev.amble.ait.core.tardis.animation;

import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.joml.Math;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.effects.ZeitonHighEffect;
import dev.amble.ait.core.sounds.travel.TravelSound;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.CloakHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.NetworkUtil;

public abstract class ExteriorAnimation {

    public static final Identifier UPDATE = AITMod.id("update_setup_anim");
    public static final double MAX_CLOAK_DISTANCE = 5d;

    protected ExteriorBlockEntity exterior;
    protected int timeLeft, maxTime, startTime;
    protected float alpha = 1;

    private boolean isDone = false;

    public ExteriorAnimation(ExteriorBlockEntity exterior) {
        this.exterior = exterior;
    }

    public float getAlpha() {
        if (this.exterior.tardis().isEmpty())
            return 1f;

        if (this.exterior.tardis().get().travel().getState() == TravelHandlerBase.State.LANDED) {
            if (!isHigh() && this.exterior.tardis().get().<CloakHandler>handler(TardisComponent.Id.CLOAK).cloaked().get()) {
                return 0.105f;
            }
            return 1.0f;
        }

        return this.alpha;
    }

    private static boolean isHigh() {
        if (ServerLifecycleHooks.isServer()) return false;

        return amIHigh();
    }
    @Environment(EnvType.CLIENT)
    private static boolean amIHigh() {
        return MinecraftClient.getInstance().player != null && ZeitonHighEffect.isHigh(MinecraftClient.getInstance().player);
    }

    public static boolean isNearTardis(PlayerEntity player, Tardis tardis, double radius) {
        return radius >= distanceFromTardis(player, tardis);
    }

    public static double distanceFromTardis(PlayerEntity player, Tardis tardis) {
        BlockPos pPos = player.getBlockPos();
        BlockPos tPos = tardis.travel().position().getPos();
        return Math.sqrt(tPos.getSquaredDistance(pPos));
    }

    public abstract void tick(Tardis tardis);

    public boolean reset() {
        if (this.isDone)
            return false;

        this.isDone = true;
        return true;
    }

    public boolean setupAnimation(TravelHandlerBase.State state) {
        if (exterior.tardis().isEmpty()) {
            AITMod.LOGGER.error("Tardis for exterior {} was null!", exterior);
            this.alpha = 0f; // just make me vanish.
            return false;
        }

        Tardis tardis = exterior.tardis().get();

        if (tardis.getExterior().getCategory() == null) {
            AITMod.LOGGER.error("Exterior category {} was null!", exterior);
            this.alpha = 0f; // just make me vanish.
            return false;
        }

        this.alpha = switch (state) {
            case DEMAT, LANDED -> 1f;
            case MAT -> 0f;

            default -> {
                AITMod.LOGGER.error("Can't get alpha for a TARDIS in FLIGHT state! Using default!");
                yield 0;
            }
        };

        this.tellClientsToSetup(state);
        TravelSound sound = tardis.stats().getTravelEffects().get(state);

        if (sound == null)
            return false;

        this.timeLeft = sound.timeLeft();
        this.maxTime = sound.maxTime();
        this.startTime = sound.startTime();

        return true;
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.clamp(0.0F, 1.0F, alpha);
    }

    public int getStartTime() {
        return startTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }
    public long getRunningTime() {
        return MathHelper.lfloor((1f - ((double) getTimeLeft() / getStartTime())) * 1000.0f / 20.0f);
    }

    public void tellClientsToSetup(TravelHandlerBase.State state) {
        if (exterior.getWorld() == null)
            return; // happens when tardis spawns above world limit, so thats nice

        if (exterior.getWorld().isClient())
            return;

        if (!exterior.isLinked())
            return;

        for (ServerPlayerEntity player : NetworkUtil.getLinkedPlayers(exterior.tardis().get().asServer())) {
            tellClientToSetup(state, player);
        }
    }

    public void tellClientToSetup(TravelHandlerBase.State state, ServerPlayerEntity player) {
        if (exterior.getWorld().isClient() || exterior.tardis().isEmpty())
            return;

        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(state.ordinal());
        data.writeUuid(exterior.tardis().getId());

        ServerPlayNetworking.send(player, UPDATE, data);
    }
}
