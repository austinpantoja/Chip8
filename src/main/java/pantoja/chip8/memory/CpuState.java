package pantoja.chip8.memory;

import pantoja.chip8.instructions.Decoder;
import pantoja.chip8.instructions.Instruction;
import pantoja.chip8.instructions.InstructionDisassembler;
import pantoja.chip8.ux.Sound;

import java.io.IOException;

public class CpuState {
    public int pc;
    public int I;
    public int delayTimer;
    public int soundTimer;
    public boolean waitingForDisplay;
    private final IBus bus;
    private final byte[] V;
    private final int[] stack;
    private final Sound sound;
    private int sp;

    public static final int FONT_START = 0x50;
    private static final int PROGRAM_START = 0x200;
    private static final int STACK_SIZE = 16;
    private static final int NUM_OF_REGISTERS = 16;


    public CpuState(Sound sound, boolean loadSplash, IBus bus) {
        this.sound = sound;
        this.bus = bus;
        pc = PROGRAM_START;
        V = new byte[NUM_OF_REGISTERS];
        stack = new int[STACK_SIZE];
        sp = 0;
        delayTimer = 0;
        soundTimer = 0;
        waitingForDisplay = false;
    }


    public CpuState(String romPath, Sound sound, IBus bus) throws IOException {
        this(sound, false, bus);
    }


    public void updateTimers() {
        if (delayTimer > 0) delayTimer--;
        if (soundTimer > 0) soundTimer--;
        sound.running = (soundTimer > 0);
    }


    public int readRegister(int x) {
        return (V[x] & 0xFF);
    }


    public void writeRegister(int x, int val) {
        V[x] = (byte) (val & 0xFF);
    }


    public int fetchInstruction() {
        int instruction = bus.read16(pc);
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
        for (int i = start; i < Math.min(bus.addressSpaceSize(), pc + 20); i += 2) {
            int instruction = bus.read16(i);
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
