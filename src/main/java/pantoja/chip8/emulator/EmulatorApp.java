package pantoja.chip8.emulator;

import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Window;

import java.io.IOException;

public class EmulatorApp {

    private final Keypad keypad;
    private final Window window;
    private final Emulator emulator;


    public EmulatorApp() throws IOException {
        this.keypad = new Keypad();
        this.window = new Window(keypad, this::start);
        this.emulator = new Emulator(window, keypad);
    }


    public void start() {
        window.show();
        emulator.start();
    }


    public void shutdown() {
        emulator.stop();
        window.dispose();
    }


    public Window getWindow() {
        return window;
    }


    public Keypad getKeypad() {
        return keypad;
    }
}
