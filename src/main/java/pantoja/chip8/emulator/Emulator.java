package pantoja.chip8.emulator;

import pantoja.chip8.instructions.Chip8Executor;
import pantoja.chip8.instructions.Decoder;
import pantoja.chip8.util.Config;
import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Sound;
import pantoja.chip8.ux.Window;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public final class Emulator implements AutoCloseable, Runnable {
    private final Window window;
    private final Keypad keypad;

    private Sound sound;
    private MachineState machineState;
    private Decoder decoder;
    private final AtomicBoolean running;
    private final AtomicBoolean reloadRequested;


    public Emulator(Window window, Keypad keypad) {
        this.window = window;
        this.keypad = keypad;
        this.running = new AtomicBoolean(false);
        this.reloadRequested = new AtomicBoolean(false);
    }


    private synchronized void loadFromConfig() {
        try {
            Config.Configuration cfg = Config.get();
            window.setupDisplay();
            sound = new Sound(cfg.soundFreq, cfg.soundAmplitude);
            machineState = new MachineState(cfg.romPath, sound);
            decoder = new Decoder(new Chip8Executor(machineState, window, keypad));
        } catch (IOException e) {
            System.out.println("Failed to load ROM config!");
            throw new RuntimeException(e);
        }
    }


    public synchronized void reload() {
        reloadRequested.set(true);
    }


    @Override
    public void close() {
        running.set(false);
    }


    @Override
    public void run() {
        loadFromConfig();
        running.set(true);
        long last = System.nanoTime();
        long timerAcc = 0L;
        long cpuAcc = 0L;

        while (running.get()) {
            long now = System.nanoTime();
            long dt = now - last;
            last = now;

            cpuAcc += dt;
            timerAcc += dt;

            if (reloadRequested.get()) {
                reloadRequested.set(false);
                loadFromConfig();
                cpuAcc = 0L;
                timerAcc = 0L;
                now = System.nanoTime();
                last = now;

            }
            // CPU stepping
            if (cpuAcc >= Config.get().cpuPeriodNs) {
                int instruction = machineState.fetchInstruction();
                decoder.decode(instruction);
                cpuAcc -= Config.get().cpuPeriodNs;
            }

            // Timers / sound / display stepping
            if (timerAcc >= Config.get().timerPeriodNs) {
                machineState.updateTimers();
                sound.audioLoop();
                EventQueue.invokeLater(() -> window.display.repaint());
                timerAcc -= Config.get().timerPeriodNs;
            }

            long parkNs = Math.min(Config.get().cpuPeriodNs - cpuAcc, Config.get().timerPeriodNs - timerAcc);
            if (parkNs > 50_000L) { // 50 microseconds
                LockSupport.parkNanos(parkNs);
            } else {
                Thread.onSpinWait();
            }
        }
    }
}
