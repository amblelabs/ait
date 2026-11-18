package dev.amble.ait.compat.svc;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public final class AITVoiceChannels {

    private static final long MAGIC = 0xA17C0DE5L << 32;
    private static final long MAGIC_MASK = 0xFFFFFFFFL << 32;

    public enum Kind {
        BROADCAST, COMMS
    }

    private AITVoiceChannels() {
    }

    public static UUID tag(Kind kind, UUID source) {
        long high = MAGIC | ((long) kind.ordinal() << 16) | (source.getMostSignificantBits() & 0xFFFFL);
        return new UUID(high, source.getLeastSignificantBits());
    }

    @Nullable public static Kind kindOf(UUID channel) {
        long high = channel.getMostSignificantBits();

        if ((high & MAGIC_MASK) != MAGIC)
            return null;

        int ordinal = (int) ((high >>> 16) & 0xFFFFL);
        Kind[] kinds = Kind.values();

        return ordinal < kinds.length ? kinds[ordinal] : null;
    }
}
