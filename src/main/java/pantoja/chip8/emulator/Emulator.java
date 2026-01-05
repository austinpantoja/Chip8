package pantoja.chip8.emulator;

import pantoja.chip8.instructions.Chip8Executor;
import pantoja.chip8.instructions.Decoder;
import pantoja.chip8.util.Config;
import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Sound;
import pantoja.chip8.ux.Window;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class Emulator implements AutoCloseable {
    private final Window window;
    private final Keypad keypad;
    private final ScheduledExecutorService exec;

    private ScheduledFuture<?> cpuTask;
    private ScheduledFuture<?> timerTask;
    private Sound sound;
    private MachineState machineState;
    private Chip8Executor executorImpl;
    private Decoder decoder;


    public Emulator(Window window, Keypad keypad) throws IOException {
        this.window = window;
        this.keypad = keypad;
        this.exec = Executors.newScheduledThreadPool(2);
    }


    public synchronized void loadFromConfig()
            throws IOException, InterruptedException, InvocationTargetException {
        stop();

        Config.Configuration cfg = Config.get();
        window.setupDisplay();
        sound = new Sound(cfg.soundFreq, cfg.soundAmplitude);
        machineState = new MachineState(cfg.romPath, sound);
        decoder = new Decoder(new Chip8Executor(machineState, window, keypad));
    }


    public synchronized void start() {
        if (machineState == null || decoder == null) {
            throw new IllegalStateException("Emulator not loaded. Call loadFromConfig() first.");
        }

        stopSchedules();

        cpuTask = exec.scheduleAtFixedRate(
                () -> {
                    int instruction = machineState.fetchInstruction();
                    decoder.decode(instruction);
                },
                0,
                Config.get().cpuPeriodNs,
                TimeUnit.NANOSECONDS
        );

        timerTask = exec.scheduleAtFixedRate(
                () -> {
                    window.display.repaint();
                    machineState.updateTimers();
                    sound.audioLoop();
                },
                0,
                Config.get().timerPeriodNs,
                TimeUnit.NANOSECONDS
        );
    }


    public synchronized void stop() {
        stopSchedules();
        // TODO update Sound to be stoppable
    }


    public synchronized void stopSchedules() {
        if (cpuTask != null) {
            cpuTask.cancel(true);
        }
        if (timerTask != null) {
            timerTask.cancel(true);
        }
    }


    @Override
    public void close() {
        stop();
        try {
            exec.shutdownNow();
            boolean terminated = exec.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!terminated) {
                Thread.currentThread().interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
