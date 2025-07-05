package dev.amble.ait.api.tardis.v2.data;

import mock.Identifier;

import java.util.*;

public class TDataRegistry {

    private static boolean frozen;

    private static final List<TData.Holder<?>> comps = new ArrayList<>();
    private static final Map<Identifier, TData.Holder<?>> idToHolder = new HashMap<>();

    public static void register(TData.Holder<?> holder) {
        if (frozen)
            throw new IllegalStateException("Already frozen");

        holder.index(comps.size());
        comps.add(holder);
        idToHolder.put(holder.id(), holder);
    }

    public TData.Holder<?> get(int index) {
        return comps.get(index);
    }

    public static TData.Holder<?> get(Identifier id) {
        return idToHolder.get(id);
    }

    public static int size() {
        return comps.size();
    }

    public static void freeze() {
        frozen = true;
    }
}
