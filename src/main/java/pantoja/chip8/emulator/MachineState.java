package pantoja.chip8.emulator;

import pantoja.chip8.instructions.Decoder;
import pantoja.chip8.instructions.Instruction;
import pantoja.chip8.instructions.InstructionDisassembler;
import pantoja.chip8.util.FileIO;
import pantoja.chip8.util.Splash;
import pantoja.chip8.util.Sprites;
import pantoja.chip8.ux.Sound;

import java.io.IOException;

public class MachineState {
    public int pc;
    public int I;
    public int delayTimer;
    public int soundTimer;
    private final byte[] ram;
    private final byte[] V;
    private final int[] stack;
    private final Sound sound;
    private int sp;

    public static final int FONT_START = 0x50;
    private static final int RAM_SIZE = 4096;
    private static final int PROGRAM_START = 0x200;
    private static final int STACK_SIZE = 16;
    private static final int NUM_OF_REGISTERS = 16;


    public MachineState(Sound sound, boolean loadSplash) {
        this.sound = sound;
        ram = new byte[RAM_SIZE];
        System.arraycopy(Sprites.CHAR_SET, 0, ram, FONT_START, Sprites.CHAR_SET.length);
        pc = PROGRAM_START;
        V = new byte[NUM_OF_REGISTERS];
        stack = new int[STACK_SIZE];
        sp = 0;
        delayTimer = 0;
        soundTimer = 0;
        if (loadSplash) {
            System.arraycopy(Splash.rom, 0, ram, PROGRAM_START, Splash.rom.length);
        }
    }


    public MachineState(String romPath, Sound sound) throws IOException {
        this(sound, false);
        try {
            FileIO.readIntoBuffer(romPath, ram, PROGRAM_START);
        } catch (IOException ioe) {
            System.out.println("Failed read rom: " + romPath);
            System.arraycopy(Splash.rom, 0, ram, PROGRAM_START, Splash.rom.length);
        }
    }


    public void updateTimers() {
        if (delayTimer > 0) delayTimer--;
        if (soundTimer > 0) soundTimer--;
        sound.running = (soundTimer > 0);
    }


    public int memRead(int loc) {
        if (loc < 0 || loc >= RAM_SIZE) {
            throw new IllegalArgumentException("Illegal out of bounds read from memory, location: " + loc);
        }
        return ram[loc] & 0xFF;
    }


    public int[] memRangeRead(int start, int length) {
        if (start < 0 || length < 0 || start + length > RAM_SIZE) {
            throw new IllegalArgumentException("Illegal out of bounds read from memory, Start: " + start + ", Length: " + length);
        }

        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = ram[i + start] & 0xFF;
        }

        return result;
    }


    public int readRegister(int x) {
        return (V[x] & 0xFF);
    }


    public void writeRegister(int x, int val) {
        V[x] = (byte) (val & 0xFF);
    }


    public void memWrite(int loc, int value) {
        if (loc < 0 || loc >= RAM_SIZE) {
            throw new IllegalArgumentException("Illegal out of bounds write to memory, location: " + loc);
        }
        ram[loc] = (byte) (value & 0xFF);
    }


    public int fetchInstruction() {
        int instruction = (memRead(pc) << 8) | memRead(pc + 1);
        pc += 2;
        return instruction;
    }


    public void incrementPC() {
        pc += 2;
    }


    public void decrementPC() {
        pc -= 2;
    }


    public void stackPush(int addr) {
        if (sp >= stack.length) {
            throw new IllegalArgumentException("Stack overflow, at location: " + pc);
        }
        stack[sp] = addr;
        sp++;
    }


    public int stackPop() {
        if (sp <= 0) {
            throw new IllegalArgumentException("Stack underflow, nothing to remove from stack");
        }
        sp--;
        return stack[sp];
    }


    public String currentState() {
        StringBuilder sb = new StringBuilder();

        // printing instructions in memory near PC
        int start = Math.max(0x200, pc - 40);
        InstructionDisassembler disassembler = new InstructionDisassembler(start);
        Decoder decoder = new Decoder(disassembler);
        for (int i = start; i < Math.min(ram.length - 1, pc + 20); i += 2) {
            int instruction = (memRead(i) << 8) | memRead(i + 1);
            decoder.decode(instruction);
        }
        Instruction.printInstructionWindow(disassembler.getInstructions(), pc);

        // Printing registers
        sb.append("\nRegisters:");
        for (int r = 0; r < 8; r++) {
            sb.append(String.format("V%X:%02X ", r, readRegister(r)));
        }
        sb.append("\n");
        for (int r = 8; r < 16; r++) {
            sb.append(String.format("V%X:%02X ", r, readRegister(r)));
        }
        sb.append("\n\n");

        // Printing special registers
        sb.append(String.format("I :  0x%04X%n", I));
        sb.append(String.format("PC:  0x%04X%n", pc));
        sb.append(String.format("SP:  0x%02X%n", sp));
        sb.append(String.format("DT:  %02X%n", delayTimer));
        sb.append(String.format("ST:  %02X%n", soundTimer));
        return sb.toString();
    }
}
