package pantoja.chip8.emulator;

import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Window;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class EmulatorApp {

    private final Keypad keypad;
    private final Window window;
    private final Emulator emulator;


    public EmulatorApp() throws IOException {
        this.keypad = new Keypad();
        this.window = new Window(keypad, this::reload);
        this.emulator = new Emulator(window, keypad);
    }


    public void start() {
        try {
            emulator.loadFromConfig();
            window.show();
            emulator.start();
        } catch (InterruptedException | InvocationTargetException | IOException ioe) {
            System.out.println("Failed to start emulator");
            throw new RuntimeException(ioe);
        }
    }


    public void reload() {
        try {
            emulator.loadFromConfig();
            emulator.start();
        } catch (InterruptedException | InvocationTargetException | IOException ioe) {
            System.out.println("Failed to reload emulator from config");
            throw new RuntimeException(ioe);
        }
    }


    public void shutdown() {
        emulator.close();
        window.dispose();
    }


    public Window getWindow() {
        return window;
    }


    public Keypad getKeypad() {
        return keypad;
    }
}
