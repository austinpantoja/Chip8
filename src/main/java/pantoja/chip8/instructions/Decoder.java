package pantoja.chip8.instructions;

public class Decoder {
    IInstructionExecutor executor;


    public Decoder(IInstructionExecutor executor) {
        this.executor = executor;
    }


    public void decode(int instruction) {
        int op = (instruction & 0xF000) >> 12;
        int vx = (instruction & 0x0F00) >> 8;
        int vy = (instruction & 0x00F0) >> 4;
        int nnn = (instruction & 0x0FFF);
        int nn = (instruction & 0x00FF);
        int n = (instruction & 0x000F);

        executor.storeInstruction(instruction);

        switch (op) {
            case 0x0 -> {
                switch (nn) {
                    case 0xE0 -> executor.cls();
                    case 0xEE -> executor.ret();
                    default -> executor.sys(nnn);
                }
            }
            case 0x1 -> executor.jump(nnn);
            case 0x2 -> executor.call(nnn);
            case 0x3 -> executor.skipIfEqualToVal(vx, nn);
            case 0x4 -> executor.skipIfNotEqualToVal(vx, nn);
            case 0x5 -> {
                if (n == 0) executor.skipIfEqual(vx, vy);
                else executor.nop();
            }
            case 0x6 -> executor.loadValIntoReg(vx, nn);
            case 0x7 -> executor.addValToReg(vx, nn);
            case 0x8 -> {
                switch (n) {
                    case 0x0 -> executor.loadRegIntoReg(vx, vy);
                    case 0x1 -> executor.or(vx, vy);
                    case 0x2 -> executor.and(vx, vy);
                    case 0x3 -> executor.xor(vx, vy);
                    case 0x4 -> executor.add(vx, vy);
                    case 0x5 -> executor.sub(vx, vy);
                    case 0x6 -> executor.shiftRight(vx, vy);
                    case 0x7 -> executor.subNotBorrow(vx, vy);
                    case 0xE -> executor.shiftLeft(vx, vy);
                    default -> executor.nop();
                }
            }
            case 0x9 -> {
                if (n == 0) executor.skipIfNotEqual(vx, vy);
                else executor.nop();
            }
            case 0xA -> executor.loadValToI(nnn);
            case 0xB -> executor.jumpPlusV0(nnn);
            case 0xC -> executor.rand(vx, nn);
            case 0xD -> executor.draw(vx, vy, n);
            case 0xE -> {
                switch (nn) {
                    case 0x9E -> executor.skipIfPressed(vx);
                    case 0xA1 -> executor.skipIfNotPressed(vx);
                    default -> executor.nop();
                }
            }
            case 0xF -> {
                switch (nn) {
                    case (0x07) -> executor.loadFromDisplayTimer(vx);
                    case (0x0A) -> executor.loadKeyToReg(vx);
                    case (0x15) -> executor.loadToDisplayTimer(vx);
                    case (0x18) -> executor.loadToSoundTimer(vx);
                    case (0x1E) -> executor.addToAddress(vx);
                    case (0x29) -> executor.loadSpriteToI(vx);
                    case (0x33) -> executor.loadRegBcdToI(vx);
                    case (0x55) -> executor.loadRegistersToI(vx);
                    case (0x65) -> executor.loadIToRegisters(vx);
                    default -> executor.nop();
                }
            }
            default -> executor.nop();
        }
    }
}
