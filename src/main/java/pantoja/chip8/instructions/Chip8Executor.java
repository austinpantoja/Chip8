package pantoja.chip8.instructions;

import pantoja.chip8.emulator.MachineState;
import pantoja.chip8.ux.Keypad;
import pantoja.chip8.ux.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Chip8Executor implements IInstructionExecutor {
    private final MachineState machineState;
    private final Window window;
    private final Keypad keypad;
    private final List<String> instructions;


    public Chip8Executor(
            final MachineState machineState,
            final Window window,
            final Keypad keypad
    ) {
        this.machineState = machineState;
        this.window = window;
        this.keypad = keypad;
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
        machineState.pc = machineState.stackPop();
    }


    @Override
    public void jump(int addr) {
        machineState.pc = addr;
    }


    @Override
    public void call(int addr) {
        machineState.stackPush(machineState.pc);
        machineState.pc = addr;
    }


    @Override
    public void skipIfEqualToVal(int vx, int val) {
        if (machineState.readRegister(vx) == val) {
            machineState.incrementPC();
        }
    }


    @Override
    public void skipIfNotEqualToVal(int vx, int val) {
        if (machineState.readRegister(vx) != val) {
            machineState.incrementPC();
        }
    }


    @Override
    public void skipIfEqual(int vx, int vy) {
        if (machineState.readRegister(vx) == machineState.readRegister(vy)) {
            machineState.incrementPC();
        }
    }


    @Override
    public void loadValIntoReg(int vx, int val) {
        machineState.writeRegister(vx, val);
    }


    @Override
    public void addValToReg(int vx, int val) {
        machineState.writeRegister(vx, machineState.readRegister(vx) + val);
    }


    @Override
    public void loadRegIntoReg(int vx, int vy) {
        int toLoad = machineState.readRegister(vy);
        machineState.writeRegister(vx, toLoad);
    }


    @Override
    public void or(int vx, int vy) {
        int toLoad = machineState.readRegister(vx) | machineState.readRegister(vy);
        machineState.writeRegister(vx, toLoad);
        // Chip-8 Quirk (reset flag)
        machineState.writeRegister(0xF, 0);
    }


    @Override
    public void and(int vx, int vy) {
        int toLoad = machineState.readRegister(vx) & machineState.readRegister(vy);
        machineState.writeRegister(vx, toLoad);
        // Chip-8 Quirk (reset flag)
        machineState.writeRegister(0xF, 0);
    }


    @Override
    public void xor(int vx, int vy) {
        int toLoad = machineState.readRegister(vx) ^ machineState.readRegister(vy);
        machineState.writeRegister(vx, toLoad);
        // Chip-8 Quirk (reset flag)
        machineState.writeRegister(0xF, 0);
    }


    @Override
    public void add(int vx, int vy) {
        int toLoad = machineState.readRegister(vx) + machineState.readRegister(vy);
        int carry = (toLoad & 0x100) >> 8;
        machineState.writeRegister(vx, toLoad & 0xFF);
        machineState.writeRegister(0xF, carry);
    }


    @Override
    public void sub(int vx, int vy) {
        int x = machineState.readRegister(vx);
        int y = machineState.readRegister(vy);
        machineState.writeRegister(vx, (x - y) & 0xFF);
        machineState.writeRegister(0xF, (x >= y) ? 1 : 0);
    }


    @Override
    public void shiftRight(int vx) {
        int x = machineState.readRegister(vx);
        int carry = x % 2;
        machineState.writeRegister(vx, x >> 1);
        machineState.writeRegister(0xF, carry);
    }


    @Override
    public void subNotBorrow(int vx, int vy) {
        int x = machineState.readRegister(vx);
        int y = machineState.readRegister(vy);

        machineState.writeRegister(vx, (y - x) & 0xFF);
        machineState.writeRegister(0xF, (y >= x) ? 1 : 0);
    }


    @Override
    public void shiftLeft(int vx) {
        int x = machineState.readRegister(vx) << 1;
        machineState.writeRegister(vx, x & 0xFF);
        machineState.writeRegister(0xF, (x & 0x100) >> 8);
    }


    @Override
    public void skipIfNotEqual(int vx, int vy) {
        if (machineState.readRegister(vx) != machineState.readRegister(vy)) {
            machineState.incrementPC();
        }
    }


    @Override
    public void loadValToI(int val) {
        machineState.I = val;
    }


    @Override
    public void jumpPlusV0(int addr) {
        machineState.pc = addr + machineState.readRegister(0);
    }


    @Override
    public void rand(int vx, int val) {
        int rand = ThreadLocalRandom.current().nextInt(256);
        machineState.writeRegister(vx, val & rand);
    }


    @Override
    public void draw(int vx, int vy, int val) {
        int x = machineState.readRegister(vx);
        int y = machineState.readRegister(vy);
        int[] sprite = machineState.memRangeRead(machineState.I, val);

        boolean carry = window.setSprite(x, y, sprite);
        machineState.writeRegister(0xF, (carry) ? 1 : 0);
    }


    @Override
    public void skipIfPressed(int vx) {
        if (keypad.isPressed(machineState.readRegister(vx))) {
            machineState.incrementPC();
        }
    }


    @Override
    public void skipIfNotPressed(int vx) {
        if (!keypad.isPressed(machineState.readRegister(vx))) {
            machineState.incrementPC();
        }
    }


    @Override
    public void loadFromDisplayTimer(int vx) {
        machineState.writeRegister(vx, machineState.delayTimer);
    }


    @Override
    public void loadKeyToReg(int vx) {
        int key = keypad.pollForKeyPress();
        if (key == -1) {
            machineState.decrementPC();
        } else {
            machineState.writeRegister(vx, key);
        }
    }


    @Override
    public void loadToDisplayTimer(int vx) {
        machineState.delayTimer = machineState.readRegister(vx);
    }


    @Override
    public void loadToSoundTimer(int vx) {
        machineState.soundTimer = machineState.readRegister(vx);
    }


    @Override
    public void addToAddress(int vx) {
        int val = machineState.readRegister(vx) + machineState.I;
        machineState.I = val;
    }


    @Override
    public void loadSpriteToI(int vx) {
        int x = machineState.readRegister(vx);
        machineState.I = MachineState.FONT_START + (5 * x);
    }


    @Override
    public void loadRegBcdToI(int vx) {
        int val = machineState.readRegister(vx);
        int ones = val % 10;
        int tens = (val / 10) % 10;
        int hundreds = (val / 100) % 10;
        machineState.memWrite(machineState.I, hundreds);
        machineState.memWrite(machineState.I + 1, tens);
        machineState.memWrite(machineState.I + 2, ones);
    }


    @Override
    public void loadRegistersToI(int vx) {
        for (int reg = 0; reg <= vx; reg++) {
            int val = machineState.readRegister(reg);
            machineState.memWrite(machineState.I, val);
            // CHIP-8 Quirk, I is incremented
            machineState.I++;
        }
    }


    @Override
    public void loadIToRegisters(int vx) {
        for (int reg = 0; reg <= vx; reg++) {
            int val = machineState.memRead(machineState.I);
            machineState.writeRegister(reg, val);
            // CHIP-8 Quirk, I is incremented
            machineState.I++;
        }
    }
}
