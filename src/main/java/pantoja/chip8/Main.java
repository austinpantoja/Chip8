package pantoja.chip8;

import pantoja.chip8.emulator.EmulatorApp;

import java.awt.EventQueue;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                EmulatorApp app = new EmulatorApp();
                app.start();
            } catch (Exception e) {
                System.out.println("The emulator crashed from an unhandled exception!");
                throw new RuntimeException(e);
            }
        });
    }

}