package dev.amble.ait.core.tardis;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.tardis.manager.ClientTardisManager;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.registry.SubSystemRegistry;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimationMap;
import dev.amble.ait.core.tardis.handler.SubSystemHandler;
import dev.amble.ait.core.tardis.handler.permissions.Permission;
import dev.amble.ait.core.tardis.handler.permissions.PermissionLike;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.Corners;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.TardisMap;
import dev.amble.ait.data.gson.*;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.dbl.DoubleValue;
import dev.amble.ait.data.properties.integer.IntValue;
import dev.amble.ait.data.properties.integer.ranged.RangedIntValue;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.TardisComponentRegistry;
import dev.amble.lib.data.DirectedBlockPos;
import dev.amble.lib.data.DirectedGlobalPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class TardisManager<T extends Tardis, C> {

    public static final Identifier SEND_PROPERTY = AITMod.id("send_property");

    public static final Identifier ASK = AITMod.id("ask_tardis");

    public static final Identifier SEND = AITMod.id("tardis/send");
    public static final Identifier SEND_BULK = AITMod.id("tardis/send_bulk");

    public static final Identifier REMOVE = AITMod.id("tardis/remove");

    public static final Identifier SEND_COMPONENT = AITMod.id("tardis/send_component");

    public static final boolean DEMENTIA = false;

    protected final Gson networkGson;
    protected final Gson fileGson;

    protected TardisManager() {
        this.networkGson = this.getNetworkGson(this.createGsonBuilder(Exclude.Strategy.NETWORK)).create();
        this.fileGson = this.getFileGson(this.createGsonBuilder(Exclude.Strategy.FILE)).create();
    }

    protected GsonBuilder createGsonBuilder(Exclude.Strategy strategy) {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes field) {
                Exclude exclude = field.getAnnotation(Exclude.class);

                if (exclude == null)
                    return false;

                Exclude.Strategy excluded = exclude.strategy();
                return excluded == Exclude.Strategy.ALL || excluded == strategy;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).registerTypeAdapter(TardisDesktopSchema.class, TardisDesktopSchema.serializer())
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
                .registerTypeAdapter(SubSystem.class, SubSystem.serializer())
                .registerTypeAdapter(TardisAnimationMap.class, TardisAnimationMap.serializer())
                .registerTypeAdapter(TardisAnimation.class, TardisAnimation.serializer());
    }

    protected GsonBuilder getNetworkGson(GsonBuilder builder) {
        return builder;
    }

    protected GsonBuilder getFileGson(GsonBuilder builder) {
        if (!AITMod.CONFIG.minifyJson)
            builder.setPrettyPrinting();

        return builder.registerTypeAdapter(Value.class, Value.serializer())
                .registerTypeAdapter(BoolValue.class, BoolValue.serializer())
                .registerTypeAdapter(IntValue.class, IntValue.serializer())
                .registerTypeAdapter(RangedIntValue.class, RangedIntValue.serializer())
                .registerTypeAdapter(DoubleValue.class, DoubleValue.serializer());
    }

    public static TardisManager<?, ?> getInstance(Entity entity) {
        return TardisManager.getInstance(entity.getWorld());
    }

    public static TardisManager<?, ?> getInstance(BlockEntity entity) {
        return TardisManager.getInstance(entity.getWorld());
    }

    public static TardisManager<?, ?> getInstance(World world) {
        return TardisManager.getInstance(!world.isClient());
    }

    public static TardisManager<?, ?> getInstance(Tardis tardis) {
        return TardisManager.getInstance((tardis instanceof ServerTardis));
    }

    public static TardisManager<?, ?> getInstance(boolean isServer) {
        return isServer ? ServerTardisManager.getInstance() : ClientTardisManager.getInstance();
    }

    public static <C, R> R with(BlockEntity entity, ContextManager<C, R> consumer) {
        return TardisManager.with(entity.getWorld(), consumer);
    }

    public static <C, R> R with(Entity entity, ContextManager<C, R> consumer) {
        return TardisManager.with(entity.getWorld(), consumer);
    }

    public static <C, R> R with(World world, ContextManager<C, R> consumer) {
        return TardisManager.with(world.isClient(), consumer, world::getServer);
    }

    @SuppressWarnings("unchecked")
    public static <C, R> R with(boolean isClient, ContextManager<C, R> consumer, Supplier<MinecraftServer> server) {
        TardisManager<?, C> manager = (TardisManager<?, C>) TardisManager.getInstance(!isClient);

        if (isClient) {
            return consumer.run((C) MinecraftClient.getInstance(), manager);
        } else {
            return consumer.run((C) server.get(), manager);
        }
    }

    public abstract void getTardis(C c, UUID uuid, Consumer<T> consumer);

    /**
     * By all means a bad practice. Use {@link #getTardis(Object, UUID, Consumer)}
     * instead. Ensures to return a {@link Tardis} instance as fast as possible.
     * <p>
     * By using this method you accept the risk of the tardis not being on the
     * client.
     *
     * @deprecated Have you read the comment?
     */
    @Nullable @Deprecated
    public abstract T demandTardis(C c, UUID uuid);

    protected abstract TardisMap<?> lookup();

    public void reset() {
        this.lookup().clear();
    }

    public Collection<UUID> ids() {
        return this.lookup().keySet();
    }

    public abstract void forEach(Consumer<T> consumer);

    public Gson getNetworkGson() {
        return this.networkGson;
    }

    public Gson getFileGson() {
        return fileGson;
    }

    @FunctionalInterface
    public interface ContextManager<C, R> {
        R run(C c, TardisManager<?, C> manager);
    }
}
