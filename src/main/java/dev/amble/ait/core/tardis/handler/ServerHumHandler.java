package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.hum.Hum;
import dev.amble.ait.registry.impl.HumRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerHumHandler extends TardisComponent {
    public static final Identifier SEND = AITMod.id("send_hum");
    public static final Identifier RECEIVE = AITMod.id("receive_hum");
    private Hum current;

    static {
        ServerPlayNetworking.registerGlobalReceiver(ServerHumHandler.RECEIVE,
                ServerTardisManager.receiveTardis((tardis, server, player, handler, buf, responseSender) -> {
                    Hum hum = HumRegistry.getInstance().get(buf.readIdentifier()); // todo use theos properties

                    if (tardis == null || hum == null)
                        return;

                    tardis.hum().set(hum);
                }));
    }

    public ServerHumHandler() {
        super(Id.HUM);
    }

    public Hum get() {
        if (current == null) {
            this.current = HumRegistry.getInstance().getRandom();
        }

        return this.current;
    }

    public void set(Hum hum) {
        this.current = hum;

        this.updateClientHum();
    }

    private void updateClientHum() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(this.current.sound().getId());

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(this.tardis.asServer())) {
            ServerPlayNetworking.send(player, SEND, buf);
        }
    }
}
