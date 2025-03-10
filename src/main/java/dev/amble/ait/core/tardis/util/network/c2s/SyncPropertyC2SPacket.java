package dev.amble.ait.core.tardis.util.network.c2s;

import java.util.UUID;

import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

public class SyncPropertyC2SPacket implements FabricPacket {
    public static final PacketType<SyncPropertyC2SPacket> TYPE = PacketType.create(AITMod.id("send_property"), SyncPropertyC2SPacket::new);

    private final UUID tardisId;
    private final String data;
    private final TardisComponent.IdLike component;
    private final String property;
    public SyncPropertyC2SPacket(UUID tardisId, Value<?> value) {
        this.tardisId = tardisId;
        this.data = TardisManager.getInstance(false).getNetworkGson().toJson(value.get()); // convert the values current state to string json
        this.property = value.getProperty().getName();
        this.component = value.getHolder().getId();
    }
    public SyncPropertyC2SPacket(PacketByteBuf buf) {
        this.tardisId = buf.readUuid();
        this.component = TardisComponentRegistry.getInstance().get(buf.readString());
        this.property = buf.readString();
        this.data = buf.readString();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(tardisId);
        buf.writeString(this.component.name());
        buf.writeString(this.property);
        buf.writeString(this.data);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean handle(ServerPlayerEntity source, PacketSender response) {
        Tardis found = ServerTardisManager.getInstance().demandTardis(ServerLifecycleHooks.get(), tardisId);
        if (found == null) return false;

        if (!(found.handler(this.component) instanceof KeyedTardisComponent keyed)) {
            return false;
        }

        // todo - flags
        Value<T> value = keyed.getPropertyData().getExact(this.property);
        Class<?> classOfT = value.getProperty().getType().getClazz();

        T obj = (T) ServerTardisManager.getInstance().getFileGson().fromJson(data, classOfT);
        value.set(obj);

        return true;
    }
}