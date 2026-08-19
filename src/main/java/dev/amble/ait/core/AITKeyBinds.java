package dev.amble.ait.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.bind.KeyBind;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.tardis.Tardis;

public class AITKeyBinds {

    private static final List<KeyBind> BINDS = new ArrayList<>();

    public static KeyBind SNAP;
    public static KeyBind INCREASE_SPEED;
    public static KeyBind DECREASE_SPEED;
    public static KeyBind TOGGLE_ANTIGRAVS;
    public static KeyBind PHASE;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (KeyBind bind : BINDS)
                bind.tick(client);
        });

        SNAP = register(new KeyBind.Held("snap", "main", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, client -> {
            ClientPlayerEntity player = client.player;

            if (player == null)
                return;

            if (player.hasVehicle()) {
                Entity entity = player.getVehicle();
                if (entity instanceof FlightTardisEntity flightTardis) {
                    if (!flightTardis.isLinked()) return;
                    Tardis tardis = flightTardis.tardis().get();

                    ClientTardisUtil.snapToOpenDoors(tardis);
                    return;
                }
            }

            Collection<ItemStack> keys = KeyItem.getKeysInInventory(player);

            for (ItemStack stack : keys) {
                if (stack.getItem() instanceof KeyItem key && key.hasProtocol(KeyItem.Protocols.SNAP)) {
                    Tardis tardis = key.getTardis(player.getWorld(), stack);

                    if (tardis == null)
                        return;

                    ClientTardisUtil.snapToOpenDoors(tardis);
                }
            }
        }));
        INCREASE_SPEED = register(new KeyBind.Held("increase_speed", "main", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, client -> {
            ClientPlayerEntity player = client.player;

            if (player == null || !player.hasVehicle())
                return;

            Entity entity = player.getVehicle();
            if (entity instanceof FlightTardisEntity flightTardis) {
                if (!flightTardis.isLinked()) return;
                Tardis tardis = flightTardis.tardis().get();

                ClientTardisUtil.flyingSpeedPacket(tardis, "up");
            }
        }));
        DECREASE_SPEED = register(new KeyBind.Held("decrease_speed", "main", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, client -> {
            ClientPlayerEntity player = client.player;

            if (player == null || !player.hasVehicle())
                return;

            Entity entity = player.getVehicle();
            if (entity instanceof FlightTardisEntity flightTardis) {
                if (!flightTardis.isLinked()) return;
                Tardis tardis = flightTardis.tardis().get();

                ClientTardisUtil.flyingSpeedPacket(tardis, "down");
            }
        }));
        TOGGLE_ANTIGRAVS = register(new KeyBind.Held("toggle_antigravs", "main", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, client -> {
            ClientPlayerEntity player = client.player;

            if (player == null || !player.hasVehicle())
                return;

            Entity entity = player.getVehicle();
            if (entity instanceof FlightTardisEntity flightTardis) {
                if (!flightTardis.isLinked()) return;
                Tardis tardis = flightTardis.tardis().get();

                ClientTardisUtil.toggleAntigravs(tardis);
            }
        }));
        PHASE = register(new KeyBind.Held("phase", "main", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, client -> {
            ClientPlayerEntity player = client.player;

            if (player == null || !player.hasVehicle())
                return;

            Entity entity = player.getVehicle();
            if (entity instanceof FlightTardisEntity flightTardis) {
                if (!flightTardis.isLinked()) return;
                Tardis tardis = flightTardis.tardis().get();

                ClientTardisUtil.phasePacket(tardis);
            }
        }));
    }

    private static KeyBind register(KeyBind bind) {
        bind.register();
        BINDS.add(bind);

        return bind;
    }
}
