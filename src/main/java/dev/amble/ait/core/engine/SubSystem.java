package dev.amble.ait.core.engine;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.*;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.Initializable;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.engine.impl.*;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.enummap.Ordered;

public abstract class SubSystem extends Initializable<SubSystem.InitContext> implements Disposable {
    @Exclude protected Tardis tardis;

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private final IdLike id;
    private boolean enabled = false;

    protected SubSystem(IdLike id) {
        this.id = id;
    }

    public abstract Item asItem();

    public IdLike getId() {
        return id;
    }

    public Tardis tardis() {
        return this.tardis;
    }

    public Text name() {
        return Text.translatable(this.getId().toTranslationKey());
    }
    public void setTardis(Tardis tardis) {
        this.tardis = tardis;
    }

    public boolean isClient() {
        return !isServer();
    }

    public boolean isServer() {
        return this.tardis() instanceof ServerTardis;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isUsable() {
        return this.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    protected void onEnable() {
        TardisEvents.SUBSYSTEM_ENABLE.invoker().onEnable(this);
    }

    protected void onDisable() {
        TardisEvents.SUBSYSTEM_DISABLE.invoker().onDisable(this);
    }

    public void tick() {

    }

    /**
     * TEMPORARY - will be removed when ARS is implemented
     */
    @Deprecated(forRemoval = true)
    public List<ItemStack> toStacks() {
        List<ItemStack> stacks = new ArrayList<>();

        if (this instanceof StructureHolder holder && holder.getStructure() != null && !holder.getStructure().isEmpty())
            stacks.addAll(holder.getStructure().toStacks());

        stacks.add(this.asItem().getDefaultStack());
        stacks.add(AITBlocks.GENERIC_SUBSYSTEM.asItem().getDefaultStack());
        return stacks;
    }

    protected void sync() {
        ServerTardisManager.getInstance().markComponentDirty(this.tardis.subsystems());
    }

    @Override
    public void dispose() {
        this.tardis = null;
    }

    public static void init(SubSystem system, Tardis tardis, TardisComponent.InitContext context) {
        if (system == null) return;

        system.setTardis(tardis);
        Initializable.init(system, InitContext.fromTardisContext(context));
    }

    public enum Id implements SubSystem.IdLike {
        ENGINE(EngineSystem.class, EngineSystem::new),
        DEMAT(DematCircuit.class, DematCircuit::new),
        LIFE_SUPPORT(LifeSupportCircuit.class, LifeSupportCircuit::new),
        SHIELDS(ShieldsCircuit.class, ShieldsCircuit::new),
        DESPERATION(DesperationCircuit.class, DesperationCircuit::new),
        CHAMELEON(ChameleonCircuit.class, ChameleonCircuit::new),
        EMERGENCY_POWER(EmergencyPower.class, EmergencyPower::new),
        STABILISERS(Stabilisers.class, Stabilisers::new),
        GRAVITATIONAL(GravitationalCircuit.class, GravitationalCircuit::new),;
        private final Supplier<SubSystem> creator;

        private final Class<? extends SubSystem> clazz;

        private Integer index = null;

        @SuppressWarnings("unchecked")
        <T extends SubSystem> Id(Class<T> clazz, Supplier<T> creator) {
            this.clazz = clazz;
            this.creator = (Supplier<SubSystem>) creator;
        }

        @Override
        public Class<? extends SubSystem> clazz() {
            return clazz;
        }

        @Override
        public SubSystem create() {
            return this.creator.get();
        }

        @Override
        public boolean creatable() {
            return this.creator != null;
        }

        @Override
        public int index() {
            return index;
        }

        @Override
        public void index(int i) {
            this.index = i;
        }

        public static SubSystem.IdLike[] ids() {
            return SubSystem.Id.values();
        }
    }

    public interface IdLike extends Ordered {
        default void set(ClientTardis tardis, SubSystem component) {
            tardis.subsystems().add(component);
        }

        default SubSystem get(ClientTardis tardis) {
            return tardis.subsystems().get(this);
        }

        Class<? extends SubSystem> clazz();

        SubSystem create();

        boolean creatable();

        String name();

        int index();

        void index(int i);

        default String toTranslationKey() {
            return "subsystem." + AITMod.MOD_ID + "." + this.name().toLowerCase();
        }
    }

    public record InitContext(@Nullable CachedDirectedGlobalPos pos,
                              boolean deserialized) implements Initializable.Context {

        public static InitContext createdAt(CachedDirectedGlobalPos pos) {
            return new InitContext(pos, false);
        }

        public static InitContext deserialize() {
            return new InitContext(null, true);
        }

        public static InitContext fromTardisContext(TardisComponent.InitContext context) {
            return new InitContext(context.pos(), context.deserialized());
        }

        @Override
        public boolean created() {
            return !deserialized;
        }
    }

    public static Object serializer() {
        return new Adapter();
    }

    private static class Adapter implements JsonSerializer<SubSystem>, JsonDeserializer<SubSystem> {

        @Override
        public SubSystem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            IdLike id = context.deserialize(jsonObject.get("id"), IdLike.class);
            JsonElement element = jsonObject.get("data");

            return context.deserialize(element, id.clazz());
        }

        @Override
        public JsonElement serialize(SubSystem src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();

            result.add("id", context.serialize(src.getId()));
            result.add("data", context.serialize(src, src.getClass()));

            return result;
        }
    }
}
