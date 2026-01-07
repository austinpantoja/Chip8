package pantoja.chip8.emulator;

import pantoja.chip8.instructions.Chip8Executor;
import pantoja.chip8.instructions.Decoder;
import pantoja.chip8.memory.Chip8Bus;
import pantoja.chip8.memory.Chip8Ram;
import pantoja.chip8.memory.CpuState;
import pantoja.chip8.memory.IBus;
import pantoja.chip8.memory.IRam;
import pantoja.chip8.util.Config;
import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Sound;
import pantoja.chip8.ux.Window;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class Emulator {
    private final Window window;
    private final Keypad keypad;
    private final IBus bus;
    private final IRam ram;

    private Sound sound;
    private CpuState cpuState;
    private Decoder decoder;

    ScheduledFuture<?> scheduledTick;
    ScheduledExecutorService executor;


    public Emulator(Window window, Keypad keypad) {
        this.window = window;
        this.keypad = keypad;
        this.ram = new Chip8Ram();
        this.bus = new Chip8Bus(ram);
        this.executor = Executors.newScheduledThreadPool(1);
    }


    private synchronized void loadFromConfig() {
        try {
            Config.Configuration cfg = Config.get();
            window.setupDisplay();
            sound = new Sound(cfg.soundFreq, cfg.soundAmplitude);
            ram.resetWithRom(cfg.romPath);
            cpuState = new CpuState(cfg.romPath, sound, bus);
            decoder = new Decoder(new Chip8Executor(cpuState, window, keypad, bus));
        } catch (IOException e) {
            System.out.println("Failed to load ROM config!");
            throw new RuntimeException(e);
        }
    }


    public synchronized void start() {
        if (scheduledTick != null) {
            scheduledTick.cancel(false);
        }
        loadFromConfig();

        // Used to measure the 60HZ timer updates
        long[] timer = new long[]{
                System.nanoTime(),  // last cpu tick time
                0,                  // timer accumulator
        };

        scheduledTick = executor.scheduleAtFixedRate(
                () -> {
                    int instruction = cpuState.fetchInstruction();
                    decoder.decode(instruction);
                    long now = System.nanoTime();
                    timer[1] += (now - timer[0]);
                    timer[0] = now;
                    if (timer[1] > Config.get().timerPeriodNs) {
                        cpuState.updateTimers();
                        sound.audioLoop();
                        EventQueue.invokeLater(() -> window.display.repaint());
                        timer[1] -= Config.get().timerPeriodNs;
                    }
                },
                0,
                Config.get().cpuPeriodNs,
                TimeUnit.NANOSECONDS
        );
    }


    public void stop() {
        // TODO stop sound
        if (scheduledTick != null) {
            scheduledTick.cancel(true);
        }
        try {
            executor.shutdownNow();
            boolean terminated = executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!terminated) {
                Thread.currentThread().interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
