package pantoja.chip8.ux;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sound {
    private static final float SAMPLE_RATE = 44100f;
    private static final int BITS = 8;
    private static final int CHANNELS = 1;
    private static final int MAX_WRITE_CAP_BYTES = 2048;

    public volatile boolean running = false;

    private final byte amplitude;
    private final AudioFormat format;
    private final int periodSamples;
    private final byte[] buffer;
    private final AtomicBoolean lineStarted = new AtomicBoolean(false);

    private SourceDataLine line;
    private boolean lastRunning = false;
    private int phaseSample = 0;


    public Sound(int frequencyHz, int amplitude) {
        if (frequencyHz <= 0)
            throw new IllegalArgumentException("frequencyHz must be > 0");
        if (amplitude < 0 || amplitude > 127)
            throw new IllegalArgumentException("amplitude must be in [0,127]");

        this.amplitude = (byte) amplitude;

        format = new AudioFormat(
                SAMPLE_RATE,
                BITS,
                CHANNELS,
                true,
                false
        );

        this.periodSamples = Math.max(1, Math.round(SAMPLE_RATE / (float) frequencyHz));
        this.buffer = new byte[Math.max(MAX_WRITE_CAP_BYTES, 512)];
    }


    public void audioLoop() {
        if (!running) {
            if (lastRunning) {
                line.flush();
            }
            lastRunning = false;
            return;
        }

        // lazy loading avoids a crackle when the line is first turned on
        if (!lineStarted.get()) {
            startLine();
        }

        lastRunning = true;

        int available = line.available();
        if (available <= 0) return;

        int toWrite = Math.min(available, MAX_WRITE_CAP_BYTES);
        toWrite = Math.min(toWrite, buffer.length);

        int availablePeriods = (toWrite / periodSamples) * periodSamples;
        if (availablePeriods <= 0) return;

        fillTriangleWave(buffer, availablePeriods);
        line.write(buffer, 0, availablePeriods);
    }


    private void startLine() {
        try {
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            lineStarted.set(true);
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Unable to open audio line");
        }
    }


    private void fillTriangleWave(byte[] out, int lenSamples) {
        int half = periodSamples / 2;

        for (int i = 0; i < lenSamples; i++) {
            int value = (phaseSample < half)
                    ? -amplitude + (2 * amplitude * phaseSample) / half
                    : amplitude - (2 * amplitude * (phaseSample - half)) / half;

            out[i] = (byte) value;

            phaseSample++;
            if (phaseSample >= periodSamples) {
                phaseSample = 0;
            }
        }
    }


    private void fillSquareWave(byte[] out, int lenSamples) {
        int half = periodSamples / 2;

        for (int i = 0; i < lenSamples; i++) {
            out[i] = (phaseSample < half) ? amplitude : (byte) -amplitude;
            phaseSample++;
            if (phaseSample >= periodSamples) phaseSample = 0;
        }
    }
}
