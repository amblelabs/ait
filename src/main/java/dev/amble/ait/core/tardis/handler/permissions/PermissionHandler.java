package dev.amble.ait.core.tardis.handler.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

public class PermissionHandler extends KeyedTardisComponent {

    private static final Identifier P19_LOYALTY_SYNC = AITMod.id("p19_loyalty");

    private static final Property<Loyalty.Type> P19_LOYALTY = Property.forEnum("p19_loyalty", Loyalty.Type.class,
            Loyalty.Type.COMPANION);

    private final Map<UUID, PermissionMap> permissions;
    private final Value<Loyalty.Type> p19Loyalty = P19_LOYALTY.create(this);

    public PermissionHandler(Map<UUID, PermissionMap> map) {
        super(Id.PERMISSIONS);
        this.permissions = map;
    }

    public PermissionHandler() {
        this(new HashMap<>());
    }

    static {
        ServerPlayNetworking.registerGlobalReceiver(P19_LOYALTY_SYNC,
                ServerTardisManager.receiveTardis((tardis, server, player, handler, buf, responseSender) -> {
                    if (tardis == null)
                        return;

                    PermissionHandler permissions = tardis.handler(Id.PERMISSIONS);
                    Loyalty.Type type = buf.readEnumConstant(Loyalty.Type.class);

                    permissions.p19Loyalty.set(type);
                }));
    }

    @Override
    public void onLoaded() {
        p19Loyalty.of(this, P19_LOYALTY);
    }

    public static void p19Loyalty(ClientTardis tardis, Loyalty.Type type) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(tardis.getUuid());
        buf.writeEnumConstant(type);

        ClientPlayNetworking.send(P19_LOYALTY_SYNC, buf);
    }

    public Value<Loyalty.Type> p19Loyalty() {
        return p19Loyalty;
    }

    public boolean check(ServerPlayerEntity player, Permission permission) {
        return this.getPermissionMap(player).get(permission);
    }

    public boolean set(ServerPlayerEntity player, Permission permission, boolean value) {
        PermissionMap map = this.getPermissionMap(player);
        map.put(permission, value);

        this.sync();
        return value;
    }

    private PermissionMap getPermissionMap(ServerPlayerEntity player) {
        PermissionMap result = permissions.get(player.getUuid());

        if (result != null)
            return result;

        result = new PermissionMap();
        permissions.put(player.getUuid(), result);
        return result;
    }
}
