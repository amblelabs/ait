package dev.amble.ait.client.sounds.hum.interior;

import dev.amble.ait.api.ClientWorldEvents;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.tardis.handler.ServerHumHandler;
import dev.amble.ait.data.hum.Hum;
import dev.amble.ait.registry.impl.HumRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ClientHumHandler extends SoundHandler {

    private LoopingSound current;

    static {
        ClientWorldEvents.CHANGE_WORLD.register((client, world) -> {
            if (world == null)
                return;

            ClientHumHandler handler = ClientSoundManager.getHum();
            handler.stopSounds();
            handler.current = null;

            ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

            if (tardis == null)
                return;

            handler.getHum(tardis);
        });
    }

    protected ClientHumHandler() {
        ClientPlayNetworking.registerGlobalReceiver(ServerHumHandler.SEND, (client, handler, buf, responseSender) -> {
            Identifier id = buf.readIdentifier();
            SoundInstance sound = findSoundById(id);

            if (sound.getId() == SoundEvents.INTENTIONALLY_EMPTY.getId())
                return;

            if (!(sound instanceof LoopingSound hum))
                return; // it aint a hum.

            this.setHum(ClientTardisUtil.getCurrentTardis(), hum);
        });
    }

    public void onSynced() {
        this.sounds = registryToList();
    }

    public LoopingSound getHum(ClientTardis tardis) {
        if (this.current == null)
            this.current = (LoopingSound) findSoundByEvent(
                    tardis.<ServerHumHandler>handler(TardisComponent.Id.HUM).get().sound());

        return this.current;
    }

    public void setHum(ClientTardis tardis, LoopingSound hum) {
        LoopingSound previous = this.getHum(tardis);
        this.current = hum;

        this.stopSound(previous);
    }

    public void setServersHum(ClientTardis tardis, Hum hum) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(tardis.getUuid());
        buf.writeIdentifier(hum.id());

        ClientPlayNetworking.send(ServerHumHandler.RECEIVE, buf);
    }

    public static ClientHumHandler create() {
        ClientHumHandler handler = new ClientHumHandler();

        handler.generateHums();
        return handler;
    }

    private void generateHums() {
        this.sounds = registryToList();
    }

    private List<SoundInstance> registryToList() {
        List<SoundInstance> list = new ArrayList<>();

        for (Hum sound : HumRegistry.getInstance().toList()) {
            list.add(new PlayerFollowingLoopingSound(sound.sound(), SoundCategory.AMBIENT,
                    AITModClient.CONFIG.interiorHumVolume));
        }

        return list;
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.fuel().hasPower();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (this.sounds == null)
            this.generateHums();

        if (this.shouldPlaySounds(tardis)) {
            this.startIfNotPlaying(this.getHum(tardis));
        } else {
            this.stopSounds();
        }
    }

    @Override
    public SoundInstance findSoundById(Identifier id) {
        if (this.sounds == null) sounds = registryToList();

        return super.findSoundById(id);
    }
}
