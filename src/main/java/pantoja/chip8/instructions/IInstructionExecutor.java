package pantoja.chip8.instructions;

/**
 * All the instructions and comments taken directly from
 * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#3.2
 * A spreadsheet I made of the instructions
 * https://docs.google.com/spreadsheets/d/1w8-RnpLtbKR3vlig5vO7Cvmx-_bs1cuyXjnuj8ix9m0/edit?usp=sharing
 */
public interface IInstructionExecutor {

    /**
     * A helper function, right now the only use case is the disassembler/dumping instructions
     */
    void storeInstruction(int instruction);

    /**
     * #### - Not defined
     * What the decoder will call whenever an instruction cannot be parsed
     * Not in the spec, used to handle errors, hopefully with grace
     */
    void nop();

    /**
     * 0nnn - SYS addr
     * Jump to a machine code routine at nnn.
     * This instruction is only used on the old computers on which Chip-8 was originally implemented.
     * It is ignored by modern interpreters (and this one)
     */
    void sys(int addr);

    /**
     * 00E0 - CLS
     * Clear the display.
     */
    void cls();

    /**
     * 00EE - RET
     * Return from a subroutine.
     * The interpreter sets the program counter to the address at the top of the stack,
     * then subtracts 1 from the stack pointer
     */
    void ret();

    /**
     * 1nnn - JP addr
     * Jump to location nnn.
     * The interpreter sets the program counter to nnn
     */
    void jump(int addr);

    /**
     * 2nnn - CALL addr
     * Call subroutine at nnn (addr).
     * The interpreter increments the stack pointer, then puts the current PC on the top of the stack.
     * The PC is then set to nnn.
     */
    void call(int addr);

    /**
     * 3xkk - SE Vx, byte
     * Skip next instruction if Vx = kk.
     * The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
     */
    void skipIfEqualToVal(int vx, int val);

    /**
     * 4xkk - SNE Vx, byte
     * Skip next instruction if Vx != kk.
     * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
     */
    void skipIfNotEqualToVal(int vx, int val);

    /**
     * 5xy0 - SE Vx, Vy
     * Skip next instruction if Vx = Vy.
     * Compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
     */
    void skipIfEqual(int vx, int vy);

    /**
     * 6xkk - LD Vx, byte
     * Set Vx = kk.
     * The interpreter puts the value kk into register Vx.
     */
    void loadValIntoReg(int vx, int val);

    /**
     * 7xkk - ADD Vx, byte
     * Set Vx = Vx + kk.
     * Adds the value kk to the value of register Vx, then stores the result in Vx.
     */
    void addValToReg(int vx, int val);

    /**
     * 8xy0 - LD Vx, Vy
     * Set Vx = Vy.
     * Stores the value of register Vy in register Vx.
     */
    void loadRegIntoReg(int vx, int vy);

    /**
     * 8xy1 - OR Vx, Vy
     * Set Vx = Vx OR Vy.
     * Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx.
     */
    void or(int vx, int vy);

    /**
     * 8xy2 - AND Vx, Vy
     * Set Vx = Vx AND Vy.
     * Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx.
     */
    void and(int vx, int vy);

    /**
     * 8xy3 - XOR Vx, Vy
     * Set Vx = Vx XOR Vy.
     * Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx.
     */
    void xor(int vx, int vy);

    /**
     * 8xy4 - ADD Vx, Vy
     * Set Vx = Vx + Vy, set VF = carry.
     * The values of Vx and Vy are added together.
     * Only the lowest 8 bits of the result are kept, and stored in Vx.
     * If the result is greater than 8 bits (i.e. > 255) VF is set to 1, otherwise 0.
     */
    void add(int vx, int vy);

    /**
     * 8xy5 - SUB Vx, Vy
     * Set Vx = Vx - Vy, set VF = NOT borrow.
     * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
     */
    void sub(int vx, int vy);

    /**
     * 8xy6 - SHR Vx {, Vy}
     * Set Vx = Vx SHR 1.
     * If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
     */
    void shiftRight(int vx);

    /**
     * 8xy7 - SUBN Vx, Vy
     * Set Vx = Vy - Vx, set VF = NOT borrow.
     * If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
     */
    void subNotBorrow(int vx, int vy);

    /**
     * 8xyE - SHL Vx {, Vy}
     * Set Vx = Vx SHL 1.
     * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
     */
    void shiftLeft(int vx);

    /**
     * 9xy0 - SNE Vx, Vy
     * Skip next instruction if Vx != Vy.
     * The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
     */
    void skipIfNotEqual(int vx, int vy);

    /**
     * Annn - LD I, addr
     * Set I = nnn.
     * The value of register I is set to nnn.
     */
    void loadValToI(int val);

    /**
     * Bnnn - JP V0, addr
     * Jump to location nnn + V0.
     * The program counter is set to nnn plus the value of V0.
     */
    void jumpPlusV0(int addr);

    /**
     * Cxkk - RND Vx, byte
     * Set Vx = random byte AND kk.
     * The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
     * The results are stored in Vx. See instruction 8xy2 for more information on AND.
     */
    void rand(int vx, int val);

    /**
     * Dxyn - DRW Vx, Vy, nibble
     * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
     * The interpreter reads n bytes from memory, starting at the address stored in I.
     * These bytes are then displayed as sprites on screen at coordinates (Vx, Vy).
     * Sprites are XORed onto the existing screen.
     * If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0.
     * The sprite wraps when positioned so part of it is outside the coordinates of the display
     */
    void draw(int vx, int vy, int val);

    /**
     * Ex9E - SKP Vx
     * Skip next instruction if key with the value of Vx is pressed.
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position
     * PC is increased by 2.
     */
    void skipIfPressed(int vx);

    /**
     * ExA1 - SKNP Vx
     * Skip next instruction if key with the value of Vx is not pressed.
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position
     * PC is increased by 2.
     */
    void skipIfNotPressed(int vx);

    /**
     * Fx07 - LD Vx, DT
     * Set Vx = delay timer value.
     * The value of DT is placed into Vx.
     */
    void loadFromDisplayTimer(int vx);

    /**
     * Fx0A - LD Vx, K
     * Wait for a key press, store the value of the key in Vx.
     * All execution stops until a key is pressed, then the value of that key is stored in Vx.
     */
    void loadKeyToReg(int vx);

    /**
     * Fx15 - LD DT, Vx
     * Set delay timer = Vx.
     * DT is set equal to the value of Vx.
     */
    void loadToDisplayTimer(int vx);

    /**
     * Fx18 - LD ST, Vx
     * Set sound timer = Vx.
     * ST is set equal to the value of Vx.
     */
    void loadToSoundTimer(int vx);

    /**
     * Fx1E - ADD I, Vx
     * Set I = I + Vx.
     * The values of I and Vx are added, and the results are stored in I.
     */
    void addToAddress(int vx);

    /**
     * Fx29 - LD I, Vx
     * Set I = location of sprite for digit Vx.
     * The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx.
     */
    void loadSpriteToI(int vx);

    /**
     * Fx33 - LD B, Vx
     * Store BCD representation of Vx in memory locations I, I+1, and I+2.
     * The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
     * the tens digit at location I+1, and the ones digit at location I+2.
     */
    void loadRegBcdToI(int vx);

    /**
     * Fx55 - LD [I], Vx
     * Store registers V0 through Vx in memory starting at location I.
     * The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
     * Unimplemented quirk where I is incremented after each write -
     * <a href="https://tobiasvl.github.io/blog/write-a-chip-8-emulator/#fx55-and-fx65-store-and-load-memory">blog</a>
     */
    void loadRegistersToI(int vx);

    /**
     * Fx65 - LD Vx, [I]
     * Read registers V0 through Vx from memory starting at location I.
     * The interpreter reads values from memory starting at location I into registers V0 through Vx.
     * Unimplemented quirk where I is incremented after each write -
     * <a href="https://tobiasvl.github.io/blog/write-a-chip-8-emulator/#fx55-and-fx65-store-and-load-memory">blog</a>
     */
    void loadIToRegisters(int vx);
}
