package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DirectionControl extends Control {

    public DirectionControl() {
        super(AITMod.id("direction"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
            boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        TravelHandler travel = tardis.travel();
        CachedDirectedGlobalPos dest = travel.destination();

        byte rotation = dest.getRotation();
        rotation = (byte) (leftClick ? getPreviousGeneralizedRotation(rotation) : getNextGeneralizedRotation(rotation));

        rotation = wrap(rotation, ExteriorBlock.MAX_ROTATION_INDEX);

        travel.forceDestination(dest.rotation(rotation));
        messagePlayer(player, rotation);
        return Result.SUCCESS;
    }

    // ↖ ↑ ↗
    // ← · →
    // ↙ ↓ ↘

    private void messagePlayer(ServerPlayerEntity player, int rotation) {
        String arrow = rotationForArrow(rotation);
        player.sendMessage(
                Text.literal("Rotation Direction: " + rotationToDirection(rotation).substring(0, 1).toUpperCase()
                        + rotationToDirection(rotation).substring(1) + " | " + arrow),
                true); // fixme translatable is preferred
    }

    public static String rotationToDirection(int currentRot) {
        return WorldUtil.rot2Text(currentRot).getString();
    }

    public static int getNextGeneralizedRotation(int rotation) {
        return (rotation + 2) % 16;
    }

    public static int getPreviousGeneralizedRotation(int rotation) {
        return (rotation - 2) % 16;
    }

    public static byte getGeneralizedRotation(int rotation) {
        if (rotation % 2 != 0 && rotation < 15)
            return (byte) (rotation + 1);

        if (rotation == 15)
            return 0;

        return (byte) rotation;
    }

    public static String rotationForArrow(int currentRot) {
        return switch (currentRot) {
            case 1, 2, 3 -> "↗";
            case 4 -> "→";
            case 5, 6, 7 -> "↘";
            case 8 -> "↓";
            case 9, 10, 11 -> "↙";
            case 12 -> "←";
            case 13, 14, 15 -> "↖";
            default -> "↑";
        };
    }

    public static byte wrap(byte value, byte max) {
        return (byte) ((value % max + max) % max);
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.DIRECTION;
    }
}
