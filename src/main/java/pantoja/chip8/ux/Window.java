package pantoja.chip8.ux;

import javax.swing.JFrame;

public class Window extends JFrame {
    private final JFrame frame;
    public DisplayPanel display;
    private final Keypad keypad;
    private final Runnable onReload;


    public Window(Keypad keypad, Runnable onReload) {
        frame = new JFrame("CHIP-8 Emulator");
        this.keypad = keypad;
        this.onReload = onReload;
        setupDisplay();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }


    public void setupDisplay() {
        display = new DisplayPanel();
        KeyMappings.install(display, keypad, onReload);
        frame.add(display);
        frame.pack();
    }


    @Override
    public void show() {
        display.setVisible(true);
        frame.setVisible(true);
    }


    public void clear() {
        display.clearDisplay();
    }


    public boolean setSprite(int x, int y, int[] sprite) {
        return display.setSprite(x, y, sprite);
    }
}
