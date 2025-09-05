package dev.amble.ait.registry.impl.console;

import dev.amble.lib.register.datapack.DatapackRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.type.*;

public class ConsoleRegistry extends DatapackRegistry<ConsoleTypeSchema> {
    private static final ConsoleRegistry INSTANCE = new ConsoleRegistry();

    public static ConsoleRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public ConsoleTypeSchema fallback() {
        return HARTNELL;
    }

    @Override
    public void syncToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        PacketByteBuf secondary = PacketByteBufs.create();

        int counter = 0;
        for (ConsoleTypeSchema schema : this.toList()) {
            if (!(schema instanceof DatapackConsole.SimpleType type)) continue;

            counter++;
            secondary.encodeAsJson(DatapackConsole.SimpleType.CODEC, type);
        }

        buf.writeInt(counter);
        buf.writeBytes(secondary);

        ServerPlayNetworking.send(player, AITMod.id("sync_console_type"), buf);
    }

    @Override
    public void readFromServer(PacketByteBuf buf) {
        for (ConsoleTypeSchema schema : this.toList()) {
            if (!(schema instanceof DatapackConsole.SimpleType type)) continue;

            this.REGISTRY.remove(type.id());
        }

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            DatapackConsole.SimpleType type = buf.decodeAsJson(DatapackConsole.SimpleType.CODEC);
            this.register(type);
        }
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void onClientInit() {
        ClientPlayNetworking.registerGlobalReceiver(AITMod.id("sync_console_type"),
                (client, handler, buf, responseSender) -> this.readFromServer(buf));
    }

    public static ConsoleTypeSchema CORAL;
    public static ConsoleTypeSchema HARTNELL;
    public static ConsoleTypeSchema COPPER;
    public static ConsoleTypeSchema TOYOTA;
    public static ConsoleTypeSchema ALNICO;
    public static ConsoleTypeSchema STEAM;
    public static ConsoleTypeSchema HUDOLIN;
    public static ConsoleTypeSchema CRYSTALLINE;
    public static ConsoleTypeSchema RENAISSANCE;
    public static ConsoleTypeSchema HOURGLASS;

    @Override
    public void onCommonInit() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> this.syncToClient(player));

        HARTNELL = register(new HartnellType());
        CORAL = register(new CoralType());
        COPPER = register(new CopperType());
        TOYOTA = register(new ToyotaType());
        ALNICO = register(new AlnicoType());
        STEAM = register(new SteamType());
        HUDOLIN = register(new HudolinType());
        CRYSTALLINE = register(new CrystallineType());
        RENAISSANCE = register(new RenaissanceType());
    }
}
