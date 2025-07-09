package dev.amble.ait.api.tardis.v2.data;

import net.minecraft.util.Identifier;

import java.util.*;

public class TDataRegistry {

    private static boolean frozen;

    private static final List<TDataHolder<?>> comps = new ArrayList<>();
    private static final Map<Identifier, TDataHolder<?>> idToHolder = new HashMap<>();

    public static void register(TDataHolder<?> holder) {
        if (frozen)
            throw new IllegalStateException("Already frozen");

        holder.index(comps.size());
        comps.add(holder);
        idToHolder.put(holder.id(), holder);
    }

    public TDataHolder<?> get(int index) {
        return comps.get(index);
    }

    public static TDataHolder<?> get(Identifier id) {
        return idToHolder.get(id);
    }

    public static int size() {
        return comps.size();
    }

    public static void freeze() {
        frozen = true;
    }
}
