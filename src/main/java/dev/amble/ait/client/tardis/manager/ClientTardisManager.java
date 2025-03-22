package dev.amble.ait.client.tardis.manager;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.ClientWorldEvents;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.DirectTardisMap;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

@Environment(EnvType.CLIENT)
public class ClientTardisManager extends TardisManager<ClientTardis> {

    private static ClientTardisManager instance;

    public static void init() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
            throw new UnsupportedOperationException("Tried to initialize ClientTardisManager on the server!");

        instance = new ClientTardisManager();
    }

    private final DirectTardisMap<ClientTardis> map = new DirectTardisMap<>();

    private TardisRef current;

    private ClientTardisManager() {
        ClientWorldEvents.CHANGE_WORLD.register((client, world) -> this.updateCurrent(world));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> this.updateCurrent(client.world));

        ClientPlayNetworking.registerGlobalReceiver(SEND, (client, handler, buf, response) -> this.syncTardis(buf));
        ClientPlayNetworking.registerGlobalReceiver(SEND_BULK, (client, handler, buf, response) -> this.syncBulk(buf));
        ClientPlayNetworking.registerGlobalReceiver(SEND_DELTA, (client, handler, buf, response) -> this.syncDelta(buf));

        ClientPlayNetworking.registerGlobalReceiver(REMOVE, (client, handler, buf, response) -> this.remove(buf));

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.reset());
        ClientLoginConnectionEvents.DISCONNECT.register((client, reason) -> this.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> this.reset());

        ClientTickEvents.END_CLIENT_TICK.register(this::endTick);
    }

    private void endTick(MinecraftClient client) {
        if (client.player == null || client.world == null)
            return;

        for (ClientTardis tardis : this.map.values()) {
            tardis.tick(client);
        }

        ClientSoundManager.tick(client);
    }

    public void remove(UUID id) {
        this.map.remove(id);
    }

    private void remove(PacketByteBuf buf) {
        this.remove(buf.readUuid());
    }

    private void syncTardis(String json) {
        try {
            ClientTardis tardis = this.networkGson.fromJson(json, ClientTardis.class);
            Tardis.init(tardis, TardisComponent.InitContext.deserialize());

            ClientTardis old = this.map.put(tardis);

            if (old != null)
                old.age();
        } catch (Throwable t) {
            AITMod.LOGGER.error("Received malformed JSON file {}", json);
            AITMod.LOGGER.error("Failed to deserialize TARDIS data: ", t);
        }
    }

    private void syncTardis(PacketByteBuf buf) {
        buf.readUuid();
        this.syncTardis(buf.readString());
    }

    private void syncBulk(PacketByteBuf buf) {
        int count = buf.readInt();

        for (int i = 0; i < count; i++) {
            this.syncTardis(buf);
        }
    }

    private void syncDelta(PacketByteBuf buf) {
        UUID uuid = buf.readUuid();
        int count = buf.readShort();

        ClientTardis tardis = this.getTardis(uuid);

        if (tardis == null)
            return; // wait 'till the server sends a full update

        for (int i = 0; i < count; i++) {
            String rawId = buf.readString();

            TardisComponent.IdLike id = TardisComponentRegistry.getInstance().get(rawId);
            TardisComponent component = this.networkGson.fromJson(buf.readString(), id.clazz());

            id.set(tardis, component);
            TardisComponent.init(component, tardis, TardisComponent.InitContext.deserialize());
        }
    }

    public void syncValue(Value<?> value) {
        TardisComponent holder = value.getHolder();

        if (!(holder instanceof KeyedTardisComponent))
            throw new IllegalArgumentException("Can't sync a value of a non keyed component");

        Tardis tardis = holder.tardis();

        if (tardis == null)
            return;

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeUuid(tardis.getUuid());
        buf.writeString(holder.getId().toString());
        buf.writeString(value.getProperty().getName());

        // convert the values current state to string json
        buf.writeString(this.networkGson.toJson(value.get()));

        ClientPlayNetworking.send(SEND_VALUE, buf);
    }

    @Override
    protected GsonBuilder getNetworkGson(GsonBuilder builder) {
        return builder.registerTypeAdapter(ClientTardis.class, ClientTardis.creator());
    }

    @Override
    public ClientTardis getTardis(UUID id) {
        return this.map.get(id);
    }

    public ClientTardis getCurrent() {
        if (current == null)
            return null;

        return (ClientTardis) current.get();
    }

    public TardisRef current() {
        return current;
    }

    public boolean inTardis() {
        return current != null;
    }

    private void updateCurrent(ClientWorld world) {
        UUID id = TardisServerWorld.getTardisId(world);
        this.current = id == null ? null : new TardisRef(id, this::getTardis);
    }

    @Override
    public void forEach(Consumer<ClientTardis> consumer) {
        this.map.forEach((uuid, tardis) -> consumer.accept(tardis));
    }

    @Override
    public Collection<UUID> ids() {
        return this.map.keySet();
    }

    @Override
    public void reset() {
        this.current = null;

        this.forEach(ClientTardis::dispose);
        this.map.clear();
    }

    public static ClientTardisManager get() {
        return instance;
    }
}
