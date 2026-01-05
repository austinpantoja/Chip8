package pantoja.chip8;

import pantoja.chip8.emulator.EmulatorApp;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
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