package pantoja.chip8.ux;

import pantoja.chip8.util.Config;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

public final class KeyMappings {

    private static final Map<Integer, Integer> KEY_MAPPINGS = Map.ofEntries(
            Map.entry(KeyEvent.VK_0, 0x0),
            Map.entry(KeyEvent.VK_1, 0x1),
            Map.entry(KeyEvent.VK_2, 0x2),
            Map.entry(KeyEvent.VK_3, 0x3),
            Map.entry(KeyEvent.VK_4, 0x4),
            Map.entry(KeyEvent.VK_5, 0x5),
            Map.entry(KeyEvent.VK_6, 0x6),
            Map.entry(KeyEvent.VK_7, 0x7),
            Map.entry(KeyEvent.VK_8, 0x8),
            Map.entry(KeyEvent.VK_9, 0x9),
            Map.entry(KeyEvent.VK_A, 0xA),
            Map.entry(KeyEvent.VK_B, 0xB),
            Map.entry(KeyEvent.VK_C, 0xC),
            Map.entry(KeyEvent.VK_D, 0xD),
            Map.entry(KeyEvent.VK_E, 0xE),
            Map.entry(KeyEvent.VK_F, 0xF)
    );


    public static void install(JComponent component, Keypad keypad, Runnable onReload) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        for (var entry : KEY_MAPPINGS.entrySet()) {
            int keyCode = entry.getKey();
            int chip8Key = entry.getValue();

            inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, false), "press_" + chip8Key);
            inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), "release_" + chip8Key);

            actionMap.put("press_" + chip8Key, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    keypad.press(chip8Key);
                }
            });

            actionMap.put("release_" + chip8Key, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    keypad.release(chip8Key);
                }
            });
        }

        // Launch settings frame when escape is pressed
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "press_esc");
        actionMap.put("press_esc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsFrame settings = new SettingsFrame(Config.get(), updated -> {
                    Config.set(updated);
                    System.out.println("Reloading emulator with ROM: " + updated.romPath);
                    onReload.run();
                });
                settings.setVisible(true);
            }
        });
    }
}
