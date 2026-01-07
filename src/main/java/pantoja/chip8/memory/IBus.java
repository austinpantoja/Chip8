package pantoja.chip8.memory;


/**
 * System bus for CPU-visible address space. The CPU must use the bus for all memory
 * reads/writes (instruction fetch, sprites, stack storage if memory-mapped later, etc.).
 * <p>
 * Admittedly, this isn't a very useful abstraction for CHIP-8, but I'm attempting to
 * build a more reusable emulator architecture, and it may prove useful for xo-chip
 */
public interface IBus {

    int read8(int addr);


    void write8(int addr, int value);


    default int read16(int addr) {
        return (read8(addr) << 8) | read8(addr + 1);
    }


    default void write16(int addr, int value) {
        write8(addr, (value >>> 8) & 0xFF);
        write8(addr + 1, value & 0xFF);
    }


    default int[] readRange(int addr, int len) {
        if (len < 0) throw new IllegalArgumentException("len must be >= 0");
        int[] memory = new int[len];
        for (int i = 0; i < len; i++) {
            memory[i] = read8(i + addr);
        }
        return memory;
    }


    default void writeRange(int addr, int[] src, int srcOff, int len) {
        if (len < 0) throw new IllegalArgumentException("len must be >= 0");
        for (int i = 0; i < len; i++) {
            write8(addr + i, src[srcOff + i] & 0xFF);
        }
    }

    int addressSpaceSize();
}
