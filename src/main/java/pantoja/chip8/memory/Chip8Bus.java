package pantoja.chip8.memory;

/**
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
public class Chip8Bus implements IBus {
    public static final int FONT_START = 0x50;
    public static final int PROGRAM_START = 0x200;
    public static final int RAM_END = 0xFFF;

    private final IRam ram;


    public Chip8Bus(IRam ram) {
        this.ram = ram;
    }


    @Override
    public int read8(int addr) {
        if (addr < 0 || addr > RAM_END) {
            throw new IndexOutOfBoundsException("Bus denied access to address: 0x" + Integer.toHexString(addr));
        }
        return ram.get(addr);
    }


    @Override
    public void write8(int addr, int value) {
        if (addr < 0 || addr > RAM_END) {
            throw new IndexOutOfBoundsException("Bus denied access to Address 0x" + Integer.toHexString(addr));
        }

        ram.set(addr, value);
    }


    @Override
    public int addressSpaceSize() {
        return RAM_END + 1;
    }
}
