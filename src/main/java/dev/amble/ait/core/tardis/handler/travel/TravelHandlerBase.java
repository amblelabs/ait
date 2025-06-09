package dev.amble.ait.core.tardis.handler.travel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.handler.TardisCrashHandler;
import dev.amble.ait.core.util.SafePosSearch;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.enummap.Ordered;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.integer.IntProperty;
import dev.amble.ait.data.properties.integer.IntValue;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class TravelHandlerBase extends KeyedTardisComponent implements TardisTickable {

    private static final Property<State> STATE = Property.forEnum("state", State.class, State.LANDED);
    private static final BoolProperty LEAVE_BEHIND = new BoolProperty("leave_behind", false);

    private static final Property<CachedDirectedGlobalPos> POSITION = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "position", (CachedDirectedGlobalPos) null);
    private static final Property<CachedDirectedGlobalPos> DESTINATION = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "destination", (CachedDirectedGlobalPos) null);
    private static final Property<CachedDirectedGlobalPos> PREVIOUS_POSITION = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "previous_position", (CachedDirectedGlobalPos) null);

    private static final BoolProperty CRASHING = new BoolProperty("crashing", false);
    private static final BoolProperty ANTIGRAVS = new BoolProperty("antigravs", false);

    private static final IntProperty SPEED = new IntProperty("speed", 0);
    private static final IntProperty MAX_SPEED = new IntProperty("max_speed", 7);

    private static final Property<SafePosSearch.Kind> VGROUND_SEARCH = Property.forEnum("vground_search", SafePosSearch.Kind.class,
            SafePosSearch.Kind.CEILING);
    private static final BoolProperty HGROUND_SEARCH = new BoolProperty("hground_search", true);

    protected final Value<State> state = STATE.create(this);
    protected final Value<CachedDirectedGlobalPos> position = POSITION.create(this);
    protected final Value<CachedDirectedGlobalPos> destination = DESTINATION.create(this);
    protected final Value<CachedDirectedGlobalPos> previousPosition = PREVIOUS_POSITION.create(this);

    private final BoolValue leaveBehind = LEAVE_BEHIND.create(this);
    protected final BoolValue crashing = CRASHING.create(this);
    protected final BoolValue antigravs = ANTIGRAVS.create(this);

    protected final IntValue speed = SPEED.create(this);
    protected final IntValue maxSpeed = MAX_SPEED.create(this);

    protected final Value<SafePosSearch.Kind> vGroundSearch = VGROUND_SEARCH.create(this);
    protected final BoolValue hGroundSearch = HGROUND_SEARCH.create(this);

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    protected int hammerUses = 0;

    public TravelHandlerBase(Id id) {
        super(id);
    }

    @Override
    public void onLoaded() {
        state.of(this, STATE);

        position.of(this, POSITION);
        destination.of(this, DESTINATION);
        previousPosition.of(this, PREVIOUS_POSITION);
        leaveBehind.of(this, LEAVE_BEHIND);

        speed.of(this, SPEED);
        maxSpeed.of(this, MAX_SPEED);

        crashing.of(this, CRASHING);
        antigravs.of(this, ANTIGRAVS);

        vGroundSearch.of(this, VGROUND_SEARCH);
        hGroundSearch.of(this, HGROUND_SEARCH);

        if (this.isClient())
            return;

        @SuppressWarnings("resource")
        MinecraftServer current = TravelHandlerBase.server();

        this.position.ifPresent(cached -> cached.init(current), false);
        this.destination.ifPresent(cached -> cached.init(current), false);
        this.previousPosition.ifPresent(cached -> cached.init(current), false);
    }

    @Override
    public void tick(MinecraftServer server) {
        TardisCrashHandler crash = tardis.crash();

        if (crash.getState() != TardisCrashHandler.State.NORMAL)
            crash.addRepairTicks(2 * this.speed());

        if (server.getTicks() % 200 == 0)
            this.hammerUses--;
    }

    public BoolValue leaveBehind() {
        return leaveBehind;
    }

    public void speed(int value) {
        this.speed.set(this.clampSpeed(value));
    }

    public int speed() {
        return this.speed.get();
    }

    protected int clampSpeed(int value) {
        return MathHelper.clamp(value, 0, this.maxSpeed.get());
    }

    public IntValue maxSpeed() {
        return maxSpeed;
    }

    public State getState() {
        return state.get();
    }

    protected void setState(State state) {
        this.state.set(state);
    }

    public CachedDirectedGlobalPos position() {
        return this.position.get();
    }

    public BoolValue antigravs() {
        return antigravs;
    }

    public boolean isCrashing() {
        return crashing.get();
    }

    public void setCrashing(boolean crashing) {
        this.crashing.set(crashing);
    }

    public int getHammerUses() {
        return hammerUses;
    }

    public int instability() {
        return this.getHammerUses() + 1;
    }

    public void useHammer() {
        this.hammerUses++;
    }

    public void resetHammerUses() {
        this.hammerUses = 0;
    }

    public void forcePosition(CachedDirectedGlobalPos cached) {
        cached.init(TravelHandlerBase.server());
        this.previousPosition.set(this.position);
        this.position.set(cached);
    }

    public void forcePosition(Function<CachedDirectedGlobalPos, CachedDirectedGlobalPos> position) {
        this.forcePosition(position.apply(this.position()));
    }

    public CachedDirectedGlobalPos destination() {
        return destination.get();
    }

    public void destination(CachedDirectedGlobalPos cached) {
        this.destination(cached, false);
    }

    public void destination(CachedDirectedGlobalPos cached, boolean force) {
        if (!force && this.destination().equals(cached))
            return;

        cached.init(TravelHandlerBase.server());

        WorldBorder border = cached.getWorld().getWorldBorder();
        BlockPos pos = cached.getPos();

        cached = border.contains(pos) ? cached : cached.pos(border.clamp(pos.getX(), pos.getY(), pos.getZ()));

        // TODO what is the point of this? the only time this should be done is on landing - unless it gets optimized enough to run here. - Loqor
        //cached = WorldUtil.locateSafe(cached, this.vGroundSearch.get(), this.hGroundSearch.get());

        this.forceDestination(cached);
    }

    public void forceDestination(CachedDirectedGlobalPos cached) {
        cached.init(TravelHandlerBase.server());
        this.destination.set(cached);
    }

    // only use when you're sure the position you're g
    public void destination(Function<CachedDirectedGlobalPos, CachedDirectedGlobalPos> position) {
        this.destination(position.apply(this.destination()));
    }

    public void forceDestination(Function<CachedDirectedGlobalPos, CachedDirectedGlobalPos> position) {
        this.forceDestination(position.apply(this.destination()));
    }

    public CachedDirectedGlobalPos previousPosition() {
        return previousPosition.get();
    }

    public BoolValue horizontalSearch() {
        return hGroundSearch;
    }

    public Value<SafePosSearch.Kind> verticalSearch() {
        return vGroundSearch;
    }

    protected static MinecraftServer server() {
        return WorldUtil.getOverworld().getServer();
    }

    public enum State implements Ordered {
        LANDED(null), DEMAT(TravelHandler::finishDemat), FLIGHT(null, false), MAT(TravelHandler::finishRemat);

        public static final Codec<State> CODEC = Codecs.NON_EMPTY_STRING.flatXmap(s -> {
            try {
                return DataResult.success(State.valueOf(s.toUpperCase()));
            } catch (Exception e) {
                return DataResult.error(() -> "Invalid state: " + s + "! | " + e.getMessage());
            }
        }, var -> DataResult.success(var.toString()));

        private final boolean animated;

        private final Consumer<TravelHandler> finish;

        State(Consumer<TravelHandler> finish) {
            this(finish, true);
        }

        State(Consumer<TravelHandler> finish, boolean animated) {
            this.animated = animated;

            this.finish = finish;
        }

        public boolean animated() {
            return animated;
        }

        public void finish(TravelHandler handler) {
            if (this.finish == null) return;

            this.finish.accept(handler);
        }

        @Override
        public int index() {
            return ordinal();
        }
    }
}
