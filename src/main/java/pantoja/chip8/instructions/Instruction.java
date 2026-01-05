package pantoja.chip8.instructions;

import java.util.List;

public class Instruction {
    private final int address;
    private final String opcode;
    private final String assembly;


    public Instruction(int address, String opcode, String assembly) {
        this.address = address;
        this.opcode = opcode;
        this.assembly = assembly;
    }


    public static String getInstructionWindow(List<Instruction> instructions, int pc) {
        StringBuilder sb = new StringBuilder();
        sb.append("─────── Instruction Window  ───────\n");
        sb.append(" Addr     Opcode  Assembly\n");

        for (Instruction instr : instructions) {
            boolean isPc = instr.address == pc;
            sb.append(instr.formatRow(isPc));
            sb.append("\n");
        }

        sb.append("───────────────────────────────────");
        return sb.toString();
    }


    public static void printInstructionWindow(List<Instruction> instructions, int pc) {
        System.out.println(getInstructionWindow(instructions, pc));
    }


    public static void printInstructionWindow(List<Instruction> instructions) {
        printInstructionWindow(instructions, -1);
    }


    public String formatRow(boolean isPc) {
        return String.format(
                "%s0x%04X   %s    %s",
                isPc ? "▶" : " ",
                address,
                opcode,
                assembly != null ? assembly : "(undecoded)"
        );
    }
}
