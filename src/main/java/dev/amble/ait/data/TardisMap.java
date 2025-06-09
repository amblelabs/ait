package dev.amble.ait.data;

import com.mojang.datafixers.util.Either;
import dev.amble.ait.core.tardis.Tardis;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TardisMap<T> extends ConcurrentHashMap<UUID, T> {

    private TardisMap() { }

    public static class Direct<T extends Tardis> extends TardisMap<T> {

        public T put(T t) {
            return super.put(t.getUuid(), t);
        }
    }

    /**
     * This type of tardis map has a {@link UUID} for a key and a {@link Either}<{@link Tardis}, {@link Exception}> for a value.
     * The map will return {@link null} when no value is associated with the id and the {@link Either} if there is.
     * <p>
     * In case a tardis has failed to load, the {@link Either} will wrap an {@link Exception}.
     *
     * @param <T>
     */
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
