package dev.amble.ait.compat.svc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import dev.amble.ait.client.AITModClient;

public final class AITVoiceFilters {

    private static final double SAMPLE_RATE = 48000;
    private static final long STALE_MILLIS = 10_000;

    private static final Map<UUID, Filter> FILTERS = new ConcurrentHashMap<>();
    private static long lastSweep = 0;

    private AITVoiceFilters() {
    }

    public static void onStaticSound(ClientReceiveSoundEvent.StaticSound event) {
        AITVoiceChannels.Kind kind = AITVoiceChannels.kindOf(event.getId());

        if (kind == null || kind == AITVoiceChannels.Kind.MUFFLED || kind == AITVoiceChannels.Kind.MUFFLED_OUTSIDE
                || kind == AITVoiceChannels.Kind.EARPIECE
                || !AITModClient.CONFIG.voiceFilters)
            return;

        apply(event, kind);
    }

    public static void onLocationalSound(ClientReceiveSoundEvent.LocationalSound event) {
        AITVoiceChannels.Kind kind = AITVoiceChannels.kindOf(event.getId());

        if (kind == AITVoiceChannels.Kind.MUFFLED || kind == AITVoiceChannels.Kind.MUFFLED_OUTSIDE
                || kind == AITVoiceChannels.Kind.EARPIECE)
            apply(event, kind);
    }

    private static void apply(ClientReceiveSoundEvent event, AITVoiceChannels.Kind kind) {
        short[] audio = event.getRawAudio();

        if (audio == null || audio.length == 0)
            return;

        long now = System.currentTimeMillis();
        Filter filter = FILTERS.computeIfAbsent(event.getId(), id -> switch (kind) {
            case COMMS -> new TelephoneFilter();
            case BROADCAST -> new TannoyFilter();
            case MUFFLED -> new MuffleFilter();
            case MUFFLED_OUTSIDE -> new HeavyMuffleFilter();
            case EARPIECE -> new EarpieceFilter(AITModClient.CONFIG.voiceFilters);
        });

        filter.lastUsed = now;
        event.setRawAudio(filter.process(audio));

        if (now - lastSweep > STALE_MILLIS) {
            lastSweep = now;
            FILTERS.values().removeIf(entry -> now - entry.lastUsed > STALE_MILLIS);
        }
    }

    private abstract static class Filter {
        private long lastUsed;

        abstract double sample(double input);

        short[] process(short[] input) {
            short[] output = new short[input.length];

            for (int i = 0; i < input.length; i++) {
                double value = this.sample(input[i] / 32768.0);
                output[i] = (short) Math.round(Math.max(-1.0, Math.min(1.0, value)) * 32767.0);
            }

            return output;
        }
    }

    private static final class TelephoneFilter extends Filter {
        private final Biquad lowCut1 = Biquad.highPass(300, 0.707);
        private final Biquad lowCut2 = Biquad.highPass(300, 0.707);
        private final Biquad highCut1 = Biquad.lowPass(3400, 0.707);
        private final Biquad highCut2 = Biquad.lowPass(3400, 0.707);
        private long noiseSeed = 0x5DEECE66DL;

        @Override
        double sample(double input) {
            double value = this.highCut2.process(this.highCut1.process(this.lowCut2.process(this.lowCut1.process(input))));
            value = Math.tanh(value * 2.2) * 0.8;

            this.noiseSeed = this.noiseSeed * 6364136223846793005L + 1442695040888963407L;
            double noise = ((this.noiseSeed >>> 40) / (double) (1L << 24) - 0.5) * 0.006;

            return value + noise;
        }
    }

    private static final class EarpieceFilter extends Filter {
        private static final double GAIN = 0.3;

        private final TelephoneFilter telephone;

        private EarpieceFilter(boolean filtered) {
            this.telephone = filtered ? new TelephoneFilter() : null;
        }

        @Override
        double sample(double input) {
            return (this.telephone == null ? input : this.telephone.sample(input)) * GAIN;
        }
    }

    private static final class HeavyMuffleFilter extends Filter {
        private final Biquad highCut1 = Biquad.lowPass(900, 0.707);
        private final Biquad highCut2 = Biquad.lowPass(900, 0.707);

        @Override
        double sample(double input) {
            return this.highCut2.process(this.highCut1.process(input)) * 0.6;
        }
    }

    private static final class MuffleFilter extends Filter {
        private final Biquad highCut = Biquad.lowPass(1500, 0.707);

        @Override
        double sample(double input) {
            return this.highCut.process(input) * 0.75;
        }
    }

    private static final class TannoyFilter extends Filter {
        private static final int ECHO_SAMPLES = (int) (SAMPLE_RATE * 0.085);

        private final Biquad lowCut = Biquad.highPass(400, 0.8);
        private final Biquad highCut = Biquad.lowPass(5000, 0.8);
        private final double[] echo = new double[ECHO_SAMPLES];
        private int echoIndex = 0;

        @Override
        double sample(double input) {
            double dry = this.highCut.process(this.lowCut.process(input));
            double delayed = this.echo[this.echoIndex];

            this.echo[this.echoIndex] = dry + delayed * 0.3;
            this.echoIndex = (this.echoIndex + 1) % ECHO_SAMPLES;

            return Math.tanh((dry + delayed * 0.35) * 2.6) * 0.75;
        }
    }

    private static final class Biquad {
        private final double b0;
        private final double b1;
        private final double b2;
        private final double a1;
        private final double a2;
        private double x1;
        private double x2;
        private double y1;
        private double y2;

        private Biquad(double b0, double b1, double b2, double a0, double a1, double a2) {
            this.b0 = b0 / a0;
            this.b1 = b1 / a0;
            this.b2 = b2 / a0;
            this.a1 = a1 / a0;
            this.a2 = a2 / a0;
        }

        static Biquad lowPass(double frequency, double q) {
            double w0 = 2 * Math.PI * frequency / SAMPLE_RATE;
            double cos = Math.cos(w0);
            double alpha = Math.sin(w0) / (2 * q);

            return new Biquad((1 - cos) / 2, 1 - cos, (1 - cos) / 2, 1 + alpha, -2 * cos, 1 - alpha);
        }

        static Biquad highPass(double frequency, double q) {
            double w0 = 2 * Math.PI * frequency / SAMPLE_RATE;
            double cos = Math.cos(w0);
            double alpha = Math.sin(w0) / (2 * q);

            return new Biquad((1 + cos) / 2, -(1 + cos), (1 + cos) / 2, 1 + alpha, -2 * cos, 1 - alpha);
        }

        double process(double input) {
            double output = this.b0 * input + this.b1 * this.x1 + this.b2 * this.x2 - this.a1 * this.y1 - this.a2 * this.y2;

            this.x2 = this.x1;
            this.x1 = input;
            this.y2 = this.y1;
            this.y1 = output;

            return output;
        }
    }
}
