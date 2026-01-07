package pantoja.chip8.memory;

public interface IRam {
    int get(int addr);

    void set(int addr, int value);

    void reset();

    void resetWithRom(String romPath);
}
