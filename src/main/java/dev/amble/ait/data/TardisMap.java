package dev.amble.ait.data;

import java.util.HashMap;
import java.util.UUID;

import dev.amble.ait.core.tardis.Tardis;

public class TardisMap<T extends Tardis> extends HashMap<UUID, T> {

    public T put(T t) {
        return this.put(t.getUuid(), t);
    }
}
