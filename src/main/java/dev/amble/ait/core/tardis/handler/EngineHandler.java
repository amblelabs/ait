package dev.amble.ait.core.tardis.handler;

import org.joml.Vector2i;

import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
@Deprecated(forRemoval = true, since = "1.2.0") // todo to be moved to SubSystemHandler
public class EngineHandler extends KeyedTardisComponent {

    private static final Vector2i ZERO = new Vector2i();

    private static final Property<Vector2i> ENGINE_CORE_POS = new Property<>(Property.Type.VEC2I, "engine_core_pos",
            (Vector2i) null);
    private final Value<Vector2i> engineCorePos = ENGINE_CORE_POS.create(this);

    public EngineHandler() {
        super(Id.ENGINE);
    }

    @Override
    public void onLoaded() {
        engineCorePos.of(this, ENGINE_CORE_POS);
    }

    @Deprecated(forRemoval = true, since = "1.2.0")
    public boolean hasEngineCore() {
        return engineCorePos.get() != null;
    }

    @Deprecated(forRemoval = true, since = "1.2.0")
    public Vector2i getCorePos() {
        Vector2i result = engineCorePos.get();
        return result != null ? result : ZERO;
    }

    @Deprecated(forRemoval = true, since = "1.2.0")
    public void linkEngine(int x, int z) {
        engineCorePos.set(new Vector2i(x, z));
    }

    @Deprecated(forRemoval = true, since = "1.2.0")
    public void unlinkEngine() {
        engineCorePos.set((Vector2i) null);
    }

    @Deprecated
    public boolean hasPower() {
        return this.tardis.fuel().hasPower();
    }
    @Deprecated
    public void togglePower() {
        this.tardis.fuel().togglePower();
    }
    @Deprecated
    public void disablePower() {
        this.tardis.fuel().disablePower();
    }
    @Deprecated
    public void enablePower() {
        this.tardis.fuel().enablePower();
    }
}
