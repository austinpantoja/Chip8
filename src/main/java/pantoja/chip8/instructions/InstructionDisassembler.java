package pantoja.chip8.instructions;

import java.util.ArrayList;
import java.util.List;

public class InstructionDisassembler implements IInstructionExecutor {
    private final List<String> instructions;
    private final int startingLocation;
    private List<String> assembly;


    public InstructionDisassembler(int startingLocation) {
        this.startingLocation = startingLocation;
        instructions = new ArrayList<>(0xFFFF);
        assembly = new ArrayList<>(0xFFFF);
    }


    public InstructionDisassembler() {
        this(0x200);
    }


    public void reset() {
        assembly = new ArrayList<>(0xFFFF);
    }


    public List<Instruction> getInstructions() {
        List<Instruction> output = new ArrayList<>(assembly.size());

        if (instructions.size() != assembly.size()) {
            System.out.println("ERROR: Instructions and Assembly have different lengths");
            System.out.println("Assembly Size: " + assembly.size());
            System.out.println("Instruction Size: " + instructions.size());
        }

        for (int i = 0; i < assembly.size(); i++) {
            output.add(
                    new Instruction(
                            startingLocation + (i * 2),
                            instructions.get(i),
                            assembly.get(i)
                    )
            );
        }

        return output;
    }


    @Override
    public void storeInstruction(int instruction) {
        instructions.add(String.format("%04X", instruction & 0xFFFF));
    }


    @Override
    // #### - No op
    public void nop() {
        assembly.add("");
    }


    // 0nnn - SYS addr
    @Override
    public void sys(int addr) {
        assembly.add("SYS " + Integer.toHexString(addr));
    }


    // 00E0 - CLS
    @Override
    public void cls() {
        assembly.add("CLS");
    }


    // 00EE - RET
    @Override
    public void ret() {
        assembly.add("RET");
    }


    // 1nnn - JP addr
    @Override
    public void jump(int addr) {
        assembly.add("JP " + Integer.toHexString(addr));
    }


    // 2nnn - CALL addr
    @Override
    public void call(int addr) {
        assembly.add("CALL " + Integer.toHexString(addr));
    }


    // 3xkk - SE Vx, byte
    @Override
    public void skipIfEqualToVal(int vx, int val) {
        assembly.add("SE v" + vx + ", " + Integer.toHexString(val));
    }


    // 4xkk - SNE Vx, byte
    @Override
    public void skipIfNotEqualToVal(int vx, int val) {
        assembly.add("SNE v" + vx + ", " + Integer.toHexString(val));
    }


    // 5xy0 - SE Vx, Vy
    @Override
    public void skipIfEqual(int vx, int vy) {
        assembly.add("SE v" + vx + ", v" + vy);
    }


    // 7xkk - ADD Vx, byte
    @Override
    public void addValToReg(int vx, int val) {
        assembly.add("ADD v" + vx + ", " + Integer.toHexString(val));
    }


    // 8xy4 - ADD Vx, Vy
    @Override
    public void add(int vx, int vy) {
        assembly.add("ADD v" + vx + ", v" + vy);
    }


    // 8xy1 - OR Vx, Vy
    @Override
    public void or(int vx, int vy) {
        assembly.add("OR v" + vx + ", v" + vy);
    }


    // 8xy2 - AND Vx, Vy
    @Override
    public void and(int vx, int vy) {
        assembly.add("AND v" + vx + ", v" + vy);
    }


    // 8xy3 - XOR Vx, Vy
    @Override
    public void xor(int vx, int vy) {
        assembly.add("XOR v" + vx + ", v" + vy);
    }


    // 8xy5 - SUB Vx, Vy
    @Override
    public void sub(int vx, int vy) {
        assembly.add("SUB v" + vx + ", v" + vy);
    }


    // 8xy6 - SHR Vx {, Vy}
    @Override
    public void shiftRight(int vx) {
        assembly.add("SHR v" + vx);
    }


    // 8xyE - SHL Vx {, Vy}
    @Override
    public void shiftLeft(int vx) {
        assembly.add("SHL v" + vx);
    }


    // 8xy7 - SUBN Vx, Vy
    @Override
    public void subNotBorrow(int vx, int vy) {
        assembly.add("SUBN v" + vx + ", v" + vy);
    }


    // 9xy0 - SNE Vx, Vy
    @Override
    public void skipIfNotEqual(int vx, int vy) {
        assembly.add("SNE v" + vx + ", v" + vy);
    }


    // Annn - LD I, addr
    @Override
    public void loadValToI(int val) {
        assembly.add("LD I, " + Integer.toHexString(val));
    }


    // Bnnn - JP V0, addr
    @Override
    public void jumpPlusV0(int addr) {
        assembly.add("JP v0, " + Integer.toHexString(addr));
    }


    // Cxkk - RND Vx, byte
    @Override
    public void rand(int vx, int val) {
        assembly.add("RAND v" + vx + ", " + Integer.toHexString(val));
    }


    // 6xkk - LD Vx, byte
    @Override
    public void loadValIntoReg(int vx, int val) {
        assembly.add("LD v" + vx + ", " + Integer.toHexString(val));
    }


    // 8xy0 - LD Vx, Vy
    @Override
    public void loadRegIntoReg(int vx, int vy) {
        assembly.add("LD v" + vx + ", v" + vy);
    }


    // Dxyn - DRW Vx, Vy, nibble
    @Override
    public void draw(int vx, int vy, int val) {
        assembly.add("DRAW v" + vx + ", v" + vy + ", " + Integer.toHexString(val));
    }


    // Ex9E - SKP Vx
    @Override
    public void skipIfPressed(int vx) {
        assembly.add("SKP v" + vx);
    }


    // ExA1 - SKNP Vx
    @Override
    public void skipIfNotPressed(int vx) {
        assembly.add("SKNP v" + vx);
    }


    // Fx07 - LD Vx, DT
    @Override
    public void loadFromDisplayTimer(int vx) {
        assembly.add("LD v" + vx + ", DT");
    }


    // Fx15 - LD DT, Vx
    @Override
    public void loadToDisplayTimer(int vx) {
        assembly.add("LD DT, v" + vx);
    }


    // Fx18 - LD ST, Vx
    @Override
    public void loadToSoundTimer(int vx) {
        assembly.add("LD ST, v" + vx);
    }


    // Fx1E - ADD I, Vx
    @Override
    public void addToAddress(int vx) {
        assembly.add("ADD I, v" + vx);
    }


    // Fx29 - LD F, Vx
    @Override
    public void loadSpriteToI(int vx) {
        assembly.add("LD F, v" + vx);
    }


    // Fx33 - LD B, Vx
    @Override
    public void loadRegBcdToI(int vx) {
        assembly.add("LD B, v" + vx);
    }


    // Fx55 - LD [I], Vx
    @Override
    public void loadRegistersToI(int vx) {
        assembly.add("LD [I], v" + vx);
    }


    // Fx65 - LD Vx, [I]
    @Override
    public void loadIToRegisters(int vx) {
        assembly.add("LD v" + vx + ", [I]");
    }


    // Fx0A - LD Vx, K
    @Override
    public void loadKeyToReg(int vx) {
        assembly.add("LD v" + vx + ", K");
    }
}
