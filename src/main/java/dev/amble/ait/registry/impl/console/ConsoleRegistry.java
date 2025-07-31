package dev.amble.ait.registry.impl.console;

import dev.amble.lib.register.datapack.DatapackRegistry;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.type.*;
import net.minecraft.server.network.ServerPlayerEntity;

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

    }

    @Override
    public void readFromServer(PacketByteBuf buf) {

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
