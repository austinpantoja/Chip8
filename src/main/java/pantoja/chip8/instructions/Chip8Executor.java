package pantoja.chip8.instructions;

import pantoja.chip8.memory.CpuState;
import pantoja.chip8.memory.IBus;
import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Chip8Executor implements IInstructionExecutor {
    private final CpuState cpuState;
    private final Window window;
    private final Keypad keypad;
    private final List<String> instructions;
    private final IBus bus;


    public Chip8Executor(
            final CpuState cpuState,
            final Window window,
            final Keypad keypad,
            final IBus bus
    ) {
        this.cpuState = cpuState;
        this.window = window;
        this.keypad = keypad;
        this.bus = bus;
        this.instructions = new ArrayList<>(0xFFFF);
    }


    @Override
    public void storeInstruction(int instruction) {
        instructions.add(String.format("%04X", instruction & 0xFFFF));
    }


    @Override
    public void nop() {
    }


    @Override
    public void sys(int addr) {
    }


    @Override
    public void cls() {
        window.clear();
    }


    @Override
    public void ret() {
        cpuState.pc = cpuState.stackPop();
    }


    @Override
    public void jump(int addr) {
        cpuState.pc = addr;
    }


    @Override
    public void call(int addr) {
        cpuState.stackPush(cpuState.pc);
        cpuState.pc = addr;
    }


    @Override
    public void skipIfEqualToVal(int vx, int val) {
        if (cpuState.readRegister(vx) == val) {
            cpuState.incrementPC();
        }
    }


    @Override
    public void skipIfNotEqualToVal(int vx, int val) {
        if (cpuState.readRegister(vx) != val) {
            cpuState.incrementPC();
        }
    }


    @Override
    public void skipIfEqual(int vx, int vy) {
        if (cpuState.readRegister(vx) == cpuState.readRegister(vy)) {
            cpuState.incrementPC();
        }
    }


    @Override
    public void loadValIntoReg(int vx, int val) {
        cpuState.writeRegister(vx, val);
    }


    @Override
    public void addValToReg(int vx, int val) {
        cpuState.writeRegister(vx, cpuState.readRegister(vx) + val);
    }


    @Override
    public void loadRegIntoReg(int vx, int vy) {
        int toLoad = cpuState.readRegister(vy);
        cpuState.writeRegister(vx, toLoad);
    }


    @Override
    public void or(int vx, int vy) {
        int toLoad = cpuState.readRegister(vx) | cpuState.readRegister(vy);
        cpuState.writeRegister(vx, toLoad);
        // Chip-8 Quirk (reset flag)
        cpuState.writeRegister(0xF, 0);
    }


    @Override
    public void and(int vx, int vy) {
        int toLoad = cpuState.readRegister(vx) & cpuState.readRegister(vy);
        cpuState.writeRegister(vx, toLoad);
        // Chip-8 Quirk (reset flag)
        cpuState.writeRegister(0xF, 0);
    }


    @Override
    public void xor(int vx, int vy) {
        int toLoad = cpuState.readRegister(vx) ^ cpuState.readRegister(vy);
        cpuState.writeRegister(vx, toLoad);
        // Chip-8 Quirk (reset flag)
        cpuState.writeRegister(0xF, 0);
    }


    @Override
    public void add(int vx, int vy) {
        int toLoad = cpuState.readRegister(vx) + cpuState.readRegister(vy);
        int carry = (toLoad & 0x100) >> 8;
        cpuState.writeRegister(vx, toLoad & 0xFF);
        cpuState.writeRegister(0xF, carry);
    }


    @Override
    public void sub(int vx, int vy) {
        int x = cpuState.readRegister(vx);
        int y = cpuState.readRegister(vy);
        cpuState.writeRegister(vx, (x - y) & 0xFF);
        cpuState.writeRegister(0xF, (x >= y) ? 1 : 0);
    }


    // TODO make quirk configurable
    @Override
    public void shiftRight(int vx, int vy) {
        int y = cpuState.readRegister(vy);
        int carry = y % 2;
        cpuState.writeRegister(vx, y >> 1);
        cpuState.writeRegister(0xF, carry);
    }


    @Override
    public void subNotBorrow(int vx, int vy) {
        int x = cpuState.readRegister(vx);
        int y = cpuState.readRegister(vy);

        cpuState.writeRegister(vx, (y - x) & 0xFF);
        cpuState.writeRegister(0xF, (y >= x) ? 1 : 0);
    }


    //TODO make quirk configurable
    @Override
    public void shiftLeft(int vx, int vy) {
        int y = cpuState.readRegister(vy) << 1;
        cpuState.writeRegister(vx, y & 0xFF);
        cpuState.writeRegister(0xF, (y & 0x100) >> 8);
    }


    @Override
    public void skipIfNotEqual(int vx, int vy) {
        if (cpuState.readRegister(vx) != cpuState.readRegister(vy)) {
            cpuState.incrementPC();
        }
    }


    @Override
    public void loadValToI(int val) {
        cpuState.I = val;
    }


    @Override
    public void jumpPlusV0(int addr) {
        cpuState.pc = addr + cpuState.readRegister(0);
    }


    @Override
    public void rand(int vx, int val) {
        int rand = ThreadLocalRandom.current().nextInt(256);
        cpuState.writeRegister(vx, val & rand);
    }


    @Override
    public void draw(int vx, int vy, int val) {
        int x = cpuState.readRegister(vx);
        int y = cpuState.readRegister(vy);
        int[] sprite = bus.readRange(cpuState.I, val);

        boolean carry = window.setSprite(x, y, sprite);
        cpuState.writeRegister(0xF, (carry) ? 1 : 0);
    }


    @Override
    public void skipIfPressed(int vx) {
        if (keypad.isPressed(cpuState.readRegister(vx))) {
            cpuState.incrementPC();
        }
    }


    @Override
    public void skipIfNotPressed(int vx) {
        if (!keypad.isPressed(cpuState.readRegister(vx))) {
            cpuState.incrementPC();
        }
    }


    @Override
    public void loadFromDisplayTimer(int vx) {
        cpuState.writeRegister(vx, cpuState.delayTimer);
    }


    @Override
    public void loadKeyToReg(int vx) {
        int key = keypad.pollForKeyPress();
        if (key == -1) {
            cpuState.decrementPC();
        } else {
            cpuState.writeRegister(vx, key);
        }
    }


    @Override
    public void loadToDisplayTimer(int vx) {
        cpuState.delayTimer = cpuState.readRegister(vx);
    }


    @Override
    public void loadToSoundTimer(int vx) {
        cpuState.soundTimer = cpuState.readRegister(vx);
    }


    @Override
    public void addToAddress(int vx) {
        int val = cpuState.readRegister(vx) + cpuState.I;
        cpuState.I = val;
    }


    @Override
    public void loadSpriteToI(int vx) {
        int x = cpuState.readRegister(vx);
        cpuState.I = CpuState.FONT_START + (5 * x);
    }


    @Override
    public void loadRegBcdToI(int vx) {
        int val = cpuState.readRegister(vx);
        int ones = val % 10;
        int tens = (val / 10) % 10;
        int hundreds = (val / 100) % 10;
        bus.write8(cpuState.I, hundreds);
        bus.write8(cpuState.I + 1, tens);
        bus.write8(cpuState.I + 2, ones);
    }


    @Override
    public void loadRegistersToI(int vx) {
        for (int reg = 0; reg <= vx; reg++) {
            int val = cpuState.readRegister(reg);
            bus.write8(cpuState.I, val);
            // CHIP-8 Quirk, I is incremented
            cpuState.I++;
        }
    }


    @Override
    public void loadIToRegisters(int vx) {
        for (int reg = 0; reg <= vx; reg++) {
            int val = bus.read8(cpuState.I);
            cpuState.writeRegister(reg, val);
            // CHIP-8 Quirk, I is incremented
            cpuState.I++;
        }
    }
}
