package dev.amble.ait.core.tardis;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.amble.lib.data.DirectedBlockPos;
import dev.amble.lib.data.DirectedGlobalPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.tardis.manager.ClientTardisManager;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.registry.SubSystemRegistry;
import dev.amble.ait.core.tardis.handler.SubSystemHandler;
import dev.amble.ait.core.tardis.handler.permissions.Permission;
import dev.amble.ait.core.tardis.handler.permissions.PermissionLike;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.Corners;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.gson.*;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

public abstract class TardisManager<T extends Tardis> {

    public static final Identifier SEND = AITMod.id("tardis/send");
    public static final Identifier SEND_BULK = AITMod.id("tardis/send_bulk");

    public static final Identifier REMOVE = AITMod.id("tardis/remove");
    public static final Identifier SEND_DELTA = AITMod.id("tardis/send_component");

    public static final Identifier SEND_VALUE = AITMod.id("tardis/send_value");

    @Environment(EnvType.CLIENT)
    protected static ClientTardisManager client;

    @Environment(EnvType.CLIENT)
    public static ClientTardisManager client() {
        return client;
    }

    protected static ServerTardisManager server;

    public static ServerTardisManager server() {
        return server;
    }

    protected final Gson networkGson = this.createGson(
            Exclude.Strategy.NETWORK, this::getNetworkGson);

    protected TardisManager() {
    }

    public static TardisManager<?> where(Entity entity) {
        return in(entity.getWorld());
    }

    public static TardisManager<?> where(BlockEntity blockEntity) {
        return in(blockEntity.getWorld());
    }

    public static TardisManager<?> in(World world) {
        return world.isClient() ? client() : server();
    }

    public abstract T getTardis(UUID id);

    public void tardis(UUID id, Consumer<T> consumer) {
        T t = this.getTardis(id);

        if (t != null)
            consumer.accept(t);
    }

    public abstract void forEach(Consumer<T> consumer);

    public abstract Collection<UUID> ids();

    public abstract void reset();

    protected Gson createGson(Exclude.Strategy strategy, Function<GsonBuilder, GsonBuilder> f) {
        return f.apply(new GsonBuilder().setExclusionStrategies(new Exclude.Impl(strategy))
                .registerTypeAdapter(TardisDesktopSchema.class, TardisDesktopSchema.serializer())
                .registerTypeAdapter(ExteriorVariantSchema.class, ExteriorVariantSchema.serializer())
                .registerTypeAdapter(DoorSchema.class, DoorSchema.serializer())
                .registerTypeAdapter(ExteriorCategorySchema.class, ExteriorCategorySchema.serializer())
                .registerTypeAdapter(ConsoleTypeSchema.class, ConsoleTypeSchema.serializer())
                .registerTypeAdapter(ConsoleVariantSchema.class, ConsoleVariantSchema.serializer())
                .registerTypeAdapter(Corners.class, Corners.serializer())
                .registerTypeAdapter(PermissionLike.class, Permission.serializer())
                .registerTypeAdapter(DirectedGlobalPos.class, DirectedGlobalPos.serializer())
                .registerTypeAdapter(DirectedBlockPos.class, DirectedBlockPos.serializer())
                .registerTypeAdapter(NbtCompound.class, new NbtSerializer())
                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
                .registerTypeAdapter(Identifier.class, new IdentifierSerializer())
                .registerTypeAdapter(GlobalPos.class, new GlobalPosSerializer())
                .registerTypeAdapter(BlockPos.class, new BlockPosSerializer())
                .registerTypeAdapter(RegistryKey.class, new RegistryKeySerializer())
                .registerTypeAdapter(TardisHandlersManager.class, TardisHandlersManager.serializer())
                .registerTypeAdapter(TardisComponent.IdLike.class, TardisComponentRegistry.idSerializer())
                .registerTypeAdapter(SubSystemHandler.class, SubSystemHandler.serializer())
                .registerTypeAdapter(SubSystem.IdLike.class, SubSystemRegistry.idSerializer())
                .registerTypeAdapter(SubSystem.class, SubSystem.serializer())).create();
    }

    protected GsonBuilder getNetworkGson(GsonBuilder builder) {
        return builder;
    }

    public Gson getNetworkGson() {
        return this.networkGson;
    }
}
