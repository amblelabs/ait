package dev.amble.ait.data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.Nullable;

import dev.amble.ait.core.tardis.Tardis;


public class OptionalTardisMap<T extends Tardis> extends ConcurrentHashMap<UUID, Either<T, Exception>> {

    private Either<T, Exception> wrap(T t) {
        return Either.left(t);
    }

    private Either<T, Exception> wrap(Exception e) {
        return Either.right(e);
    }

    @Nullable public Either<T, Exception> get(UUID key) {
        if (key == null)
            return null;

        return super.get(key);
    }

    @Nullable public Either<T, Exception> put(T t) {
        if (t == null)
            return null;

        return this.put(t.getUuid(), wrap(t));
    }

    public void empty(UUID id, Exception e) {
        this.put(id, wrap(e));
    }
}
