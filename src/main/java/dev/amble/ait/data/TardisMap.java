package dev.amble.ait.data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.Nullable;

import dev.amble.ait.core.tardis.Tardis;

public abstract class TardisMap<T> extends ConcurrentHashMap<UUID, T> {

    private TardisMap() { }

    @Nullable public T get(UUID id) {
        if (id == null)
            return null;

        return super.get(id);
    }

    public static class Direct<T extends Tardis> extends TardisMap<T> {

        public T put(T t) {
            return super.put(t.getUuid(), t);
        }
    }

    public static class Optional<T extends Tardis> extends TardisMap<Either<T, Exception>> {

        private Either<T, Exception> wrap(T t) {
            return Either.left(t);
        }

        private Either<T, Exception> wrap(Exception e) {
            return Either.right(e);
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
}
