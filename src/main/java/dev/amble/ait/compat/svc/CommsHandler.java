package dev.amble.ait.compat.svc;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.client.renderers.PhoneCableRenderer;
import dev.amble.ait.client.screens.svc.CommScreen;
import dev.amble.ait.client.screens.svc.DialScreen;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.comms.RotaryPhones;
import dev.amble.ait.core.tardis.comms.TardisComms;
import dev.amble.ait.core.tardis.comms.TardisPhones;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.registry.impl.TardisComponentRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CommsHandler extends KeyedTardisComponent {

    public static final IdLike ID = new AbstractId<>("COMMS", CommsHandler::new, CommsHandler.class);

    public static final Identifier BROADCAST = AITMod.id("tardis_broadcast");
    public static final Identifier CALL_TRACING = AITMod.id("call_tracing");

    private static final BoolProperty TRACEABLE = new BoolProperty("call_tracing", true);
    private static final BoolProperty HANDSET = new BoolProperty("comms_handset", false);

    private final BoolValue callTracing = TRACEABLE.create(this);
    private final BoolValue handset = HANDSET.create(this);

    public CommsHandler() {
        super(ID);
    }

    public static CommsHandler of(Tardis tardis) {
        return tardis.handler(ID);
    }

    public static void init() {
        TardisComponentRegistry.getInstance().register(ID);

        AITVoiceChatBroadcast.init();
        AITVoiceDoors.init();
        TardisComms.init();
        TardisPhones.init();
        RotaryPhones.init();

        ServerPlayNetworking.registerGlobalReceiver(BROADCAST, ServerTardisManager.receiveTardis(
                SecurityControl.withLoyaltyCheck((tardis, server, player, handler, buf, responseSender) ->
                        server.execute(() -> AITVoiceChatBroadcast.toggle(player, tardis)))));

        ServerPlayNetworking.registerGlobalReceiver(CALL_TRACING, ServerTardisManager.receiveTardis(
                SecurityControl.withLoyaltyCheck((tardis, server, player, handler, buf, responseSender) -> {
                    boolean traceable = buf.readBoolean();
                    server.execute(() -> of(tardis).callTracing().set(traceable));
                })));
    }

    public static void clientInit() {
        CommScreen.registerClient();
        DialScreen.registerClient();
        PhoneCableRenderer.init();
    }

    public static void sendBroadcastToggle(Tardis tardis) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(tardis.getUuid());
        ClientPlayNetworking.send(BROADCAST, buf);
    }

    @Override
    public void onLoaded() {
        this.callTracing.of(this, TRACEABLE);
        this.handset.of(this, HANDSET);
    }

    public BoolValue callTracing() {
        return this.callTracing;
    }

    public BoolValue handset() {
        return this.handset;
    }
}
