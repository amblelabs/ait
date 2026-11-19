package dev.amble.ait.compat.svc;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public final class AITVoiceChannels {

    private static final long MAGIC = 0xA17C0DE5L << 32;
    private static final long MAGIC_MASK = 0xFFFFFFFFL << 32;

    public enum Kind {
        BROADCAST, COMMS, MUFFLED, EARPIECE
    }

    private AITVoiceChannels() {
    }

    /**
     * Builds a Simple Voice Chat channel id that carries which kind of AIT audio it is, so the client can pick a filter
     * from the id alone.
     * <p>
     * Layout of the most significant 64 bits: the top 32 bits are {@link #MAGIC} (marks the id as ours), the next 16 are
     * the {@link Kind} ordinal, and the low 16 are kept from {@code source}. The least significant 64 bits are copied from
     * {@code source} unchanged, so different senders still get distinct channels per kind.
     *
     * @param kind   what the audio is, decides the client-side filter
     * @param source the sender (usually a player uuid) the channel belongs to
     * @return a channel id that {@link #kindOf(UUID)} maps back to {@code kind}
     */
    public static UUID tag(Kind kind, UUID source) {
        long high = MAGIC | ((long) kind.ordinal() << 16) | (source.getMostSignificantBits() & 0xFFFFL);
        return new UUID(high, source.getLeastSignificantBits());
    }

    /**
     * Reads the {@link Kind} back out of a channel id made by {@link #tag(Kind, UUID)}.
     *
     * @param channel a voice chat channel id
     * @return the kind, or {@code null} if the id wasn't made by {@link #tag(Kind, UUID)} (or the ordinal is unknown)
     */
    @Nullable public static Kind kindOf(UUID channel) {
        long high = channel.getMostSignificantBits();

        if ((high & MAGIC_MASK) != MAGIC)
            return null;

        int ordinal = (int) ((high >>> 16) & 0xFFFFL);
        Kind[] kinds = Kind.values();

        return ordinal < kinds.length ? kinds[ordinal] : null;
    }
}
