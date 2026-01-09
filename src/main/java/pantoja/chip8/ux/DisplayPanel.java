package pantoja.chip8.ux;

import pantoja.chip8.util.Config;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Arrays;

public class DisplayPanel extends JPanel {

    private final int width;
    private final int height;
    private final int scale;
    private final Color foreground;
    private final boolean[][] display;


    public DisplayPanel() {
        height = Config.get().height;
        width = Config.get().width;
        scale = Config.get().scale;
        foreground = Config.get().foreground;
        setPreferredSize(new Dimension(width * scale, height * scale));
        setBackground(Config.get().background);

        display = new boolean[width][height];
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(foreground);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (display[x][y]) {
                    g.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }
    }


    public boolean setSprite(int x, int y, int[] sprite) {
        boolean erased = false;

        for (int yi = y; yi < y + sprite.length; yi++) {
            // Quirk should be configurable
            if (yi >= height) break;
            int py = (yi) % height;

            for (int i = 0; i < 8; i++) {
                // Quirk - should be configurable
                if ((x + i) >= width) break;
                int px = (x + i) % width;
                boolean flipPixel = ((sprite[yi - y] & (0b1000_0000 >> i)) != 0);

                if (flipPixel) {
                    display[px][py] = !display[px][py];
                    if (!display[px][py]) {
                        erased = true;
                    }
                }
            }
        }

        return erased;
    }


    public void clearDisplay() {
        for (int x = 0; x < width; x++) {
            Arrays.fill(display[x], false);
        }
    }
}
