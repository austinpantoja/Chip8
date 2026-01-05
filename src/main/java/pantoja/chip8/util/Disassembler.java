package pantoja.chip8.util;

import pantoja.chip8.instructions.Decoder;
import pantoja.chip8.instructions.Instruction;
import pantoja.chip8.instructions.InstructionDisassembler;

import java.io.IOException;

public final class Disassembler {

    public static void runDisassembler() {
        runDisassembler(Config.get().romPath);
    }


    public static void runDisassembler(String romPath) {
        try {
            byte[] rom = FileIO.readAllBytes(romPath);
            InstructionDisassembler disassembler = new InstructionDisassembler();
            Decoder decoder = new Decoder(disassembler);

            for (int i = 0; i < rom.length - 1; i += 2) {
                int instruction = ((rom[i] & 0xFF) << 8) | (rom[i + 1] & 0xFF);
                decoder.decode(instruction);
            }

            Instruction.printInstructionWindow(disassembler.getInstructions());
        } catch (IOException ioe) {
            System.out.printf("Failed to read ROM from path: %s\n", romPath);
        }
    }
}
