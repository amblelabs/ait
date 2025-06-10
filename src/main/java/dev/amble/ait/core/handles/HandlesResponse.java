package dev.amble.ait.core.handles;

import java.util.List;

import dev.amble.lib.api.Identifiable;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.ServerTardis;

/**
 * For Handles the robot, this is used to handle responses.
 * @author james
 */
public interface HandlesResponse extends Identifiable {
    /**
     * Send a chat message to the target player.
     * Auto prefixed with <Handles> for the message.
     * @param target The player to send the message to.
     * @param message The message to send.
     */
    default void sendChat(ServerPlayerEntity target, Text message) {
        message = Text.literal("<Handles> ").append(message);
        target.sendMessage(message, false);
    }

    /**
     * @return The sound to play when the command fails.
     */
    default SoundEvent failureSound() {
        return AITSounds.HANDLES_DENIED;
    }

    /**
     * @return The sound to play when the command succeeds.
     */
    default SoundEvent successSound() {
        return AITSounds.HANDLES_AFFIRMATIVE;
    }

    default boolean success(HandlesSound source) {
        source.playSound(successSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);
        return true;
    }

    default boolean failure(HandlesSound source) {
        source.playSound(failureSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);
        return false;
    }

    /**
     * Run the responses.
     * @param player player who invoked this responses
     * @param source sound handler
     * @param tardis handles linked tardis
     * @return Whether the responses was successful.
     */
    boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis);

    /**
     * @param command keyword to search for
     * @return whether the word given is a command word for this responses
     */
    default boolean isCommand(String command) {
        return getCommandWords().contains(command.toLowerCase());
    }

    /**
     * @return Whether a non-secure person can run the command when P19 is on.
     */
    default boolean requiresSudo() {
        return true;
    }

    /**
     * @return The command words that this responses can handle.
     */
    List<String> getCommandWords();
}
