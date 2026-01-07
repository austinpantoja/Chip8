package pantoja.chip8.emulator;

import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Window;

public class EmulatorApp {

    private final Window window;
    private final Emulator emulator;


    public EmulatorApp() {
        Keypad keypad = new Keypad();
        this.window = new Window(keypad, this::start);
        this.emulator = new Emulator(window, keypad);
    }


    public void start() {
        window.show();
        emulator.start();
    }
}
