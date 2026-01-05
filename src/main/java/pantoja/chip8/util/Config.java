package pantoja.chip8.util;

import java.awt.Color;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class Config {

    private Config() {
    }


    /**
     * Immutable config snapshot. All fields are final.
     */
    public static final class Configuration {
        public final String romPath;

        public final int timerHz;
        public final int cpuHz;

        public final long cpuPeriodNs;
        public final long timerPeriodNs;

        public final int soundFreq;
        public final int soundAmplitude;

        public final int width;
        public final int height;
        public final int scale;

        public final Color background;
        public final Color foreground;


        private Configuration(Builder b) {
            this.romPath = requireNonBlank(b.romPath, "romPath");

            this.timerHz = requirePositive(b.timerHz, "timerHz");
            this.cpuHz = requirePositive(b.cpuHz, "cpuHz");

            this.timerPeriodNs = 1_000_000_000L / this.timerHz;
            this.cpuPeriodNs = 1_000_000_000L / this.cpuHz;

            this.soundFreq = requirePositive(b.soundFreq, "soundFreq");
            this.soundAmplitude = requireInRange(b.soundAmplitude, 0, 127, "soundAmplitude");

            this.width = requirePositive(b.width, "width");
            this.height = requirePositive(b.height, "height");
            this.scale = requirePositive(b.scale, "scale");

            this.background = Objects.requireNonNull(b.background, "background");
            this.foreground = Objects.requireNonNull(b.foreground, "foreground");
        }


        /**
         * Create a builder pre-populated with this config’s values.
         */
        public Builder toBuilder() {
            return new Builder(this);
        }


        /**
         * Builder is mutable; Config is immutable.
         */
        public static final class Builder {
            private String romPath;

            private int timerHz;
            private int cpuHz;

            private int soundFreq;
            private int soundAmplitude;

            private int width;
            private int height;
            private int scale;

            private Color background;
            private Color foreground;


            public Builder() {
            }


            private Builder(Configuration c) {
                this.romPath = c.romPath;
                this.timerHz = c.timerHz;
                this.cpuHz = c.cpuHz;
                this.soundFreq = c.soundFreq;
                this.soundAmplitude = c.soundAmplitude;
                this.width = c.width;
                this.height = c.height;
                this.scale = c.scale;
                this.background = c.background;
                this.foreground = c.foreground;
            }


            public Builder romPath(String v) {
                this.romPath = v;
                return this;
            }


            public Builder timerHz(int v) {
                this.timerHz = v;
                return this;
            }


            public Builder cpuHz(int v) {
                this.cpuHz = v;
                return this;
            }


            public Builder soundFreq(int v) {
                this.soundFreq = v;
                return this;
            }


            public Builder soundAmplitude(int v) {
                this.soundAmplitude = v;
                return this;
            }


            public Builder width(int v) {
                this.width = v;
                return this;
            }


            public Builder height(int v) {
                this.height = v;
                return this;
            }


            public Builder scale(int v) {
                this.scale = v;
                return this;
            }


            public Builder palette(Color bg, Color fg) {
                this.background = bg;
                this.foreground = fg;
                return this;
            }


            public Configuration build() {
                return new Configuration(this);
            }
        }
    }

    // ----- Defaults  -----
    public static final Color FOREGROUND_COLOR = new Color(0xA6, 0xA1, 0xFF);
    public static final Color BACKGROUND_COLOR = new Color(0x35, 0x28, 0x79);


    public static Configuration defaults() {
        return new Configuration.Builder()
                .romPath("data/roms/splash.ch8")
                .timerHz(60)
                .cpuHz(1000)
                .soundFreq(329)
                .soundAmplitude(40)
                .width(64)
                .height(32)
                .scale(15)
                .palette(BACKGROUND_COLOR, FOREGROUND_COLOR)
                .build();
    }


    // ----- Thread-safe current config -----
    private static final AtomicReference<Configuration> CURRENT = new AtomicReference<>(defaults());


    public static Configuration get() {
        return CURRENT.get();
    }


    public static void set(Configuration newConfiguration) {
        CURRENT.set(Objects.requireNonNull(newConfiguration, "newConfig"));
    }


    public static void reset() {
        CURRENT.set(defaults());
    }


    /**
     * Atomic update convenience: apply a modification function via a builder pattern.
     * Example:
     * Configuration.update(b -> b.cpuHz(700).timerHz(60));
     */
    public static void update(java.util.function.UnaryOperator<Configuration.Builder> mutator) {
        Objects.requireNonNull(mutator, "mutator");

        CURRENT.updateAndGet(old -> {
            Configuration.Builder b = old.toBuilder();
            Configuration.Builder mutated = mutator.apply(b);
            if (mutated == null) {
                throw new IllegalStateException("mutator returned null builder");
            }
            return mutated.build();
        });
    }


    private static int requirePositive(int v, String name) {
        if (v <= 0) throw new IllegalArgumentException(name + " must be > 0");
        return v;
    }


    private static int requireInRange(int v, int min, int max, String name) {
        if (v < min || v > max)
            throw new IllegalArgumentException(name + " must be in [" + min + "," + max + "]");
        return v;
    }


    private static String requireNonBlank(String s, String name) {
        if (s == null || s.isBlank())
            throw new IllegalArgumentException(name + " cannot be null/blank");
        return s;
    }
}
