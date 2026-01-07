package pantoja.chip8.memory;

import pantoja.chip8.util.FileIO;
import pantoja.chip8.util.Splash;
import pantoja.chip8.util.Sprites;

import java.io.IOException;

/**
 * Memory map is handled by the bus, but memory is managed by Chip8Ram
 * ┌─────────────────────────────┐ 0xFFF ← Last valid address
 * │         Program RAM         │
 * │        (ROM + data)         │
 * ├─────────────────────────────┤ 0x200 ← PC starts here
 * │            Free             │
 * ├─────────────────────────────┤ 0x0A0
 * │        Font sprites         │
 * ├─────────────────────────────┤ 0x050 ← Fx29 targets start here
 * │   Interpreter / reserved    │
 * └─────────────────────────────┘ 0x000 ← First valid address
 */
public class Chip8Ram implements IRam {
    private byte[] ram;


    public Chip8Ram() {
        reset();
    }


    public Chip8Ram(String romPath) {
        resetWithRom(romPath);
    }


    @Override
    public int get(int addr) {
        return 0xFF & ram[addr];
    }


    @Override
    public void set(int addr, int value) {
        ram[addr] = (byte) (value & 0xFF);
    }


    @Override
    public void reset() {
        ram = new byte[Chip8Bus.RAM_END + 1];
        System.arraycopy(Sprites.CHAR_SET, 0, ram, Chip8Bus.FONT_START, Sprites.CHAR_SET.length);
        System.arraycopy(Splash.rom, 0, ram, Chip8Bus.PROGRAM_START, Splash.rom.length);
    }


    @Override
    public void resetWithRom(String romPath) {
        ram = new byte[Chip8Bus.RAM_END + 1];
        System.arraycopy(Sprites.CHAR_SET, 0, ram, Chip8Bus.FONT_START, Sprites.CHAR_SET.length);
        try {
            FileIO.readIntoBuffer(romPath, ram, Chip8Bus.PROGRAM_START);
        } catch (IOException e) {
            System.out.println("Unable to load RAM: " + romPath);
            System.arraycopy(Splash.rom, 0, ram, Chip8Bus.PROGRAM_START, Splash.rom.length);
        }
    }
}
