package dev.amble.ait.client.sounds.hum.interior;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import dev.amble.ait.client.sounds.PlayerFollowingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.CreakSound;
import dev.amble.ait.registry.impl.CreakRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientCreakHandler extends SoundHandler {

    private static final Random random = new Random();

    public static ClientCreakHandler create() {
        ClientCreakHandler handler = new ClientCreakHandler();

        handler.generateCreaks();
        return handler;
    }

    private void generateCreaks() {
        this.sounds = this.registryToList();
    }

    /**
     * Converts all the {@link CreakSound}'s in the {@link CreakRegistry} to
     * {@link LoopingSound} so they are usable
     *
     * @return A list of {@link LoopingSound} from the {@link CreakRegistry}
     */
    private List<SoundInstance> registryToList() {
        List<SoundInstance> list = new ArrayList<>();

        for (CreakSound sound : CreakRegistry.REGISTRY) {
            list.add(new PlayerFollowingLoopingSound(sound.sound(), SoundCategory.AMBIENT,
                    AITModClient.CONFIG.interiorHumVolume));
        }

        return list;
    }

    public BlockPos randomNearConsolePos(BlockPos consolePos) {
        return consolePos.add(random.nextInt(8) - 1, 0, random.nextInt(8) - 1);
    }

    public void playRandomCreak(ClientTardis current) {
        CreakSound chosen = CreakRegistry.getRandomCreak();

        if (current.siege().isActive() && chosen.equals(CreakRegistry.WHISPER)) {
            current.getDesktop().getConsolePos()
                    .forEach(console -> startIfNotPlaying(
                            new PositionedSoundInstance(chosen.sound(), SoundCategory.HOSTILE, 0.5f, 1.0f,
                                    net.minecraft.util.math.random.Random.create(), randomNearConsolePos(console))));

            return;
        } else if (chosen.equals(CreakRegistry.WHISPER)) {
            return;
        }

        PlayerFollowingSound following = new PlayerFollowingSound(chosen.sound(), SoundCategory.AMBIENT,
                AITModClient.CONFIG.interiorHumVolume);
        startIfNotPlaying(following);
    }

    public void tick(MinecraftClient client) {
        if (this.sounds == null)
            this.generateCreaks();

        ClientTardis current = ClientTardisUtil.getCurrentTardis();

        if (current == null) {
            this.stopSounds();
            return;
        }

        // todo should they play even with power? just make them more rare??
        if (current.fuel().hasPower()
                && (current.travel().getState() == TravelHandlerBase.State.LANDED || current.travel().autopilot())) {
            this.stopSounds();
            return;
        }

        // theyre in a tardis and theres no power so play creaks boi
        if (random.nextInt(512) == 32)
            this.playRandomCreak(current);
    }
}
