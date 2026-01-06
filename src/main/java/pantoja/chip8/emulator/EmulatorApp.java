package pantoja.chip8.emulator;

import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Window;

import java.io.IOException;

public class EmulatorApp {

    private final Keypad keypad;
    private final Window window;
    private final Emulator emulator;
    private Thread emuThread;


    public EmulatorApp() throws IOException {
        this.keypad = new Keypad();
        this.window = new Window(keypad, this::reload);
        this.emulator = new Emulator(window, keypad);
    }


    public void start() {
        window.show();
        emuThread = new Thread(emulator);
        emuThread.start();
    }


    public void reload() {
        emulator.reload();
    }


    public void shutdown() {
        emulator.close();
        try {
            emuThread.join(500L);
        } catch (InterruptedException e) {
            emuThread.interrupt();
        }
        window.dispose();
    }


    public Window getWindow() {
        return window;
    }


    public Keypad getKeypad() {
        return keypad;
    }
}
