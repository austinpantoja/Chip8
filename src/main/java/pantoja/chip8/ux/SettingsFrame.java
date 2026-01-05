package pantoja.chip8.ux;

import pantoja.chip8.util.Config;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.function.Consumer;

public final class SettingsFrame extends JFrame {

    private final Consumer<Config.Configuration> onApply;

    private final JTextField romPathField = new JTextField();
    private final JPanel bgSwatch = new JPanel();
    private final JPanel fgSwatch = new JPanel();
    private final JSpinner cpuHzSpinner;
    private final JSpinner timerHzSpinner;
    private final JSpinner soundFreqSpinner;
    private final JSpinner soundAmpSpinner;
    private final JSpinner widthSpinner;
    private final JSpinner heightSpinner;
    private final JSpinner scaleSpinner;
    private Color background;
    private Color foreground;


    public SettingsFrame(Config.Configuration initial, Consumer<Config.Configuration> onApply) {
        super("CHIP-8 Settings");
        this.onApply = onApply;

        // Initialize state from existing config
        romPathField.setText(initial.romPath);
        romPathField.setEditable(false);

        background = initial.background;
        foreground = initial.foreground;

        cpuHzSpinner = spinnerInt(initial.cpuHz, 1, 100_000, 50);
        timerHzSpinner = spinnerInt(initial.timerHz, 1, 1000, 1);

        soundFreqSpinner = spinnerInt(initial.soundFreq, 1, 10_000, 10);
        soundAmpSpinner = spinnerInt(initial.soundAmplitude, 0, 127, 1);

        widthSpinner = spinnerInt(initial.width, 1, 1024, 1);
        heightSpinner = spinnerInt(initial.height, 1, 1024, 1);
        scaleSpinner = spinnerInt(initial.scale, 1, 200, 1);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(buildContentPane());
        pack();
        setLocationRelativeTo(null);
    }


    private Container buildContentPane() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // ROM Path (file picker)
        JButton browse = new JButton("Browse...");
        browse.addActionListener(e -> chooseRomFile());

        addRow(form, c, row++,
                new JLabel("ROM Path"),
                wrapWithButton(romPathField, browse));

        // Timing
        addRow(form, c, row++, new JLabel("CPU Hz"), cpuHzSpinner);
        addRow(form, c, row++, new JLabel("Timer Hz"), timerHzSpinner);

        // Sound
        addRow(form, c, row++, new JLabel("Sound Frequency (Hz)"), soundFreqSpinner);
        addRow(form, c, row++, new JLabel("Sound Amplitude (0–127)"), soundAmpSpinner);

        // Display
        addRow(form, c, row++, new JLabel("Display Width"), widthSpinner);
        addRow(form, c, row++, new JLabel("Display Height"), heightSpinner);
        addRow(form, c, row++, new JLabel("Scale"), scaleSpinner);

        // Colors
        JButton pickBg = new JButton("Pick...");
        pickBg.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Background Color", background);
            if (chosen != null) {
                background = chosen;
                updateSwatches();
            }
        });

        JButton pickFg = new JButton("Pick...");
        pickFg.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Foreground Color", foreground);
            if (chosen != null) {
                foreground = chosen;
                updateSwatches();
            }
        });

        bgSwatch.setPreferredSize(new Dimension(40, 20));
        fgSwatch.setPreferredSize(new Dimension(40, 20));
        bgSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        fgSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        updateSwatches();

        addRow(form, c, row++,
                new JLabel("Background"),
                wrapWithSwatchAndButton(bgSwatch, pickBg));

        addRow(form, c, row++,
                new JLabel("Foreground"),
                wrapWithSwatchAndButton(fgSwatch, pickFg));

        root.add(form, BorderLayout.CENTER);
        root.add(buildButtons(), BorderLayout.SOUTH);

        return root;
    }


    private JPanel buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton reset = new JButton("Reset to Defaults");
        reset.addActionListener(e -> resetToDefaults());

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());

        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> applySettings());

        buttons.add(reset);
        buttons.add(cancel);
        buttons.add(apply);
        return buttons;
    }


    private void applySettings() {
        try {
            // Build new config using your immutable Config + Builder
            Config.Configuration current = Config.get();

            Config.Configuration.Builder b = current.toBuilder()
                    .romPath(romPathField.getText())
                    .cpuHz(getInt(cpuHzSpinner))
                    .timerHz(getInt(timerHzSpinner))
                    .soundFreq(getInt(soundFreqSpinner))
                    .soundAmplitude(getInt(soundAmpSpinner))
                    .width(getInt(widthSpinner))
                    .height(getInt(heightSpinner))
                    .scale(getInt(scaleSpinner))
                    .palette(background, foreground);

            Config.Configuration updated = b.build();
            onApply.accept(updated);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage() != null ? ex.getMessage() : ex.toString(),
                    "Invalid Settings",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void resetToDefaults() {
        Config.Configuration d = Config.defaults();

        romPathField.setText(d.romPath);
        setSpinner(cpuHzSpinner, d.cpuHz);
        setSpinner(timerHzSpinner, d.timerHz);
        setSpinner(soundFreqSpinner, d.soundFreq);
        setSpinner(soundAmpSpinner, d.soundAmplitude);
        setSpinner(widthSpinner, d.width);
        setSpinner(heightSpinner, d.height);
        setSpinner(scaleSpinner, d.scale);

        background = d.background;
        foreground = d.foreground;
        updateSwatches();
    }


    private void chooseRomFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select CHIP-8 ROM");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Start in current ROM directory if possible
        String current = romPathField.getText();
        if (current != null && !current.isBlank()) {
            File f = new File(current);
            File dir = f.isDirectory() ? f : f.getParentFile();
            if (dir != null && dir.exists()) chooser.setCurrentDirectory(dir);
        }

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                romPathField.setText(file.getPath());
            }
        }
    }


    private void updateSwatches() {
        bgSwatch.setBackground(background);
        fgSwatch.setBackground(foreground);
        bgSwatch.repaint();
        fgSwatch.repaint();
    }

    // ----- Small UI helpers -----


    private static JSpinner spinnerInt(int initial, int min, int max, int step) {
        return new JSpinner(new SpinnerNumberModel(initial, min, max, step));
    }


    private static int getInt(JSpinner spinner) {
        Object v = spinner.getValue();
        return (v instanceof Number) ? ((Number) v).intValue() : Integer.parseInt(v.toString());
    }


    private static void setSpinner(JSpinner spinner, int value) {
        spinner.setValue(value);
    }


    private static void addRow(JPanel panel, GridBagConstraints c, int row, JComponent label, JComponent field) {
        c.gridy = row;

        c.gridx = 0;
        c.weightx = 0.0;
        panel.add(label, c);

        c.gridx = 1;
        c.weightx = 1.0;
        panel.add(field, c);
    }


    private static JComponent wrapWithButton(JComponent field, JButton button) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.add(field, BorderLayout.CENTER);
        p.add(button, BorderLayout.EAST);
        return p;
    }


    private static JComponent wrapWithSwatchAndButton(JComponent swatch, JButton button) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.add(swatch, BorderLayout.WEST);
        p.add(button, BorderLayout.EAST);
        return p;
    }
}
