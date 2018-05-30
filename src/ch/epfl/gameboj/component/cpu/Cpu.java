package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.*;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Ram;

import java.util.Objects;

import static ch.epfl.gameboj.component.cpu.Alu.RotDir.LEFT;
import static ch.epfl.gameboj.component.cpu.Alu.RotDir.RIGHT;

/**
 * Central processing unit
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */
public final class Cpu implements Component, Clocked {

    private final static int OPCODE_PREFIX = 0xCB;

    private long nextNonIdleCycle;
    private long cycle;

    private int PC;
    private int SP;
    private int IE;
    private int IF;
    private boolean IME;

    private Bus bus;
    private Ram highRam = new Ram(AddressMap.HIGH_RAM_SIZE);

    private static final Opcode[] DIRECT_OPCODE_TABLE = buildOpcodeTable(
            Opcode.Kind.DIRECT);
    private static final Opcode[] PREFIXED_OPCODE_TABLE = buildOpcodeTable(
            Opcode.Kind.PREFIXED);

    private final RegisterFile<Reg> regFile;

    /**
     * CPU 8 bits registers
     */
    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
    }

    /**
     * CPU 16 bits register (addresses)
     */
    private enum Reg16 implements Register {
        AF(Cpu.Reg.A, Cpu.Reg.F), BC(Cpu.Reg.B, Cpu.Reg.C), DE(Cpu.Reg.D,
                Cpu.Reg.E), HL(Cpu.Reg.H, Cpu.Reg.L);

        private Reg reg1;
        private Reg reg2;

        Reg16(Reg reg1, Reg reg2) {
            this.reg1 = reg1;
            this.reg2 = reg2;
        }
    }

    /**
     * CPU interruptions
     */
    public enum Interrupt implements Bit {
        VBLANK, LCD_STAT, TIMER, SERIAL, JOYPAD
    }

    /**
     * Constructs a CPU
     */
    public Cpu() {
        PC = 0;
        SP = 0;
        IE = 0;
        IF = 0;
        IME = false;
        regFile = new RegisterFile<>(Reg.values());
    }

    /**
     * Raises the given interruption by setting the corresponding bit to 1 in
     * the register IF.
     *
     * @param i, the interruption to raise
     */
    public void requestInterrupt(Interrupt i) {
        IF = Bits.set(IF, i.index(), true);
    }

    private int checkInterrupt() {
        return Bits.clip(5, IE & IF);
    }

    private static Opcode[] buildOpcodeTable(Opcode.Kind k) {
        Opcode[] opcodes = new Opcode[256];
        for (Opcode o : Opcode.values()) {
            if (o.kind == k)
                opcodes[o.encoding] = o;
        }

        return opcodes;
    }

    @Override public void cycle(long cycle) {
        assert (cycle <= nextNonIdleCycle) :
                "Current cycle is bigger than nextNonIdleCycle";

        this.cycle = cycle;
        if (nextNonIdleCycle == Long.MAX_VALUE && checkInterrupt() != 0) {
            nextNonIdleCycle = cycle;
            reallyCycle();
        }
        if (cycle == nextNonIdleCycle)
            reallyCycle();
    }

    /**
     * Checks if the interruptions are activated (IME true) and if an
     * interruption is waiting, in which case it treats it.
     */
    private void reallyCycle() {
        if (IME && checkInterrupt() != 0) {
            IME = false;
            int i = Integer
                    .numberOfTrailingZeros(Integer.lowestOneBit(checkInterrupt()));
            IF = Bits.set(IF, i, false);
            push16(PC);
            PC = AddressMap.INTERRUPTS[i];
            nextNonIdleCycle += 5;
        } else {
            int indicator = read8(PC);
            if (indicator != OPCODE_PREFIX) {
                dispatch(DIRECT_OPCODE_TABLE[indicator]);
            } else {
                dispatch(PREFIXED_OPCODE_TABLE[read8AfterOpcode()]);
            }
        }
    }

    private void dispatch(Opcode opcode) {

        if (opcode == null)
            System.out.println("opcode is null !");

        int nextPC = PC + opcode.totalBytes;
        nextNonIdleCycle += opcode.cycles;

        switch (opcode.family) {
        case NOP: {
        }
        break;
        case LD_R8_HLR: {
            regFile.set(extractReg(opcode, 3), read8AtHl());
        }
        break;
        case LD_A_HLRU: {
            regFile.set(Reg.A, read8AtHl());
            setReg16(Reg16.HL, Bits.clip(16,
                    reg16(Reg16.HL) + extractHlIncrement(opcode)));
        }
        break;
        case LD_A_N8R: {
            regFile.set(Reg.A,
                    read8(AddressMap.REGS_START + read8AfterOpcode()));
        }
        break;
        case LD_A_CR: {
            regFile.set(Reg.A,
                    read8(AddressMap.REGS_START + regFile.get(Reg.C)));
        }
        break;
        case LD_A_N16R: {
            regFile.set(Reg.A, read8(read16AfterOpcode()));
        }
        break;
        case LD_A_BCR: {
            regFile.set(Reg.A, read8(reg16(Reg16.BC)));
        }
        break;
        case LD_A_DER: {
            regFile.set(Reg.A, read8(reg16(Reg16.DE)));
        }
        break;
        case LD_R8_N8: {
            regFile.set(extractReg(opcode, 3), read8AfterOpcode());
        }
        break;
        case LD_R16SP_N16: {
            setReg16SP(extractReg16(opcode), read16AfterOpcode());
        }
        break;
        case POP_R16: {
            setReg16(extractReg16(opcode), pop16());
        }
        break;
        case LD_HLR_R8: {
            write8AtHl(regFile.get(extractReg(opcode, 0)));
        }
        break;
        case LD_HLRU_A: {
            write8AtHl(regFile.get(Reg.A));
            setReg16(Reg16.HL, Bits.clip(16,
                    reg16(Reg16.HL) + extractHlIncrement(opcode)));
        }
        break;
        case LD_N8R_A: {
            write8(AddressMap.REGS_START + read8AfterOpcode(),
                    regFile.get(Reg.A));
        }
        break;
        case LD_CR_A: {
            write8(AddressMap.REGS_START + regFile.get(Reg.C),
                    regFile.get(Reg.A));
        }
        break;
        case LD_N16R_A: {
            write8(read16AfterOpcode(), regFile.get(Reg.A));
        }
        break;
        case LD_BCR_A: {
            write8(reg16(Reg16.BC), regFile.get(Reg.A));
        }
        break;
        case LD_DER_A: {
            write8(reg16(Reg16.DE), regFile.get(Reg.A));
        }
        break;
        case LD_HLR_N8: {
            write8AtHl(read8AfterOpcode());
        }
        break;
        case LD_N16R_SP: {
            write16(read16AfterOpcode(), SP);
        }
        break;
        case LD_R8_R8: {
            regFile.set(extractReg(opcode, 3),
                    regFile.get(extractReg(opcode, 0)));

        }
        break;
        case LD_SP_HL: {
            SP = reg16(Reg16.HL);
        }
        break;
        case PUSH_R16: {
            push16(reg16(extractReg16(opcode)));
        }
        break;

        // Add
        case ADD_A_R8: {
            setRegFlags(Reg.A, Alu.add(regFile.get(Reg.A),
                    regFile.get(extractReg(opcode, 0)), testCarry(opcode)));
        }
        break;
        case ADD_A_N8: {
            setRegFlags(Reg.A,
                    Alu.add(regFile.get(Reg.A), read8AfterOpcode(), testCarry(opcode)));
        }
        break;
        case ADD_A_HLR: {
            boolean b0 = Bits.test(regFile.get(Reg.F), 4) && Bits
                    .test(opcode.encoding, 3);
            setRegFlags(Reg.A, Alu.add(regFile.get(Reg.A), read8AtHl(), testCarry(opcode)));
        }
        break;
        case INC_R8: {
            Reg r = extractReg(opcode, 3);
            int vf = Alu.add(regFile.get(r), 1);
            setRegFromAlu(r, vf);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.CPU);
        }
        break;
        case INC_HLR: {
            int vf = Alu.add(read8AtHl(), 1);
            write8AtHl(Alu.unpackValue(vf));
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.CPU);
        }
        break;
        case INC_R16SP: {
            setReg16SP(extractReg16(opcode),
                    Bits.clip(16, extractIntReg16SP(opcode) + 1));
        }
        break;
        case ADD_HL_R16SP: {
            int r = extractIntReg16SP(opcode);
            int vf = Alu.add16H(reg16(Reg16.HL), r);
            setReg16SP(Reg16.HL, Alu.unpackValue(vf));
            combineAluFlags(vf, FlagSrc.CPU, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.ALU);
        }
        break;
        case LD_HLSP_S8: {
            int vf = Alu.add16L(SP,
                    Bits.clip(16, signedNextInstruction()));
            Reg16 r = (Bits.test(opcode.encoding, 4)) ?
                    Reg16.HL : Reg16.AF;
            setReg16SP(r, Alu.unpackValue(vf));
            setFlags(vf);
        }
        break;

        // Subtract
        case SUB_A_R8: {
            setRegFlags(Reg.A, Alu.sub(regFile.get(Reg.A),
                    regFile.get(extractReg(opcode, 0)), testCarry(opcode)));
        }
        break;
        case SUB_A_N8: {
            setRegFlags(Reg.A,
                    Alu.sub(regFile.get(Reg.A), read8AfterOpcode(), testCarry(opcode)));
        }
        break;
        case SUB_A_HLR: {
            setRegFlags(Reg.A, Alu.sub(regFile.get(Reg.A), read8AtHl(), testCarry(opcode)));
        }
        break;
        case DEC_R8: {
            Reg r = extractReg(opcode, 3);
            int vf = Alu.sub(regFile.get(r), 1);
            setRegFromAlu(r, vf);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V1, FlagSrc.ALU,
                    FlagSrc.CPU);
        }
        break;
        case DEC_HLR: {
            int sub = Alu.sub(read8AtHl(), 1);
            combineAluFlags(sub, FlagSrc.ALU, FlagSrc.V1, FlagSrc.ALU,
                    FlagSrc.CPU);
            write8AtHl(Alu.unpackValue(sub));
        }
        break;
        case CP_A_R8: {
            setFlags(Alu.sub(regFile.get(Reg.A),
                    regFile.get(extractReg(opcode, 0))));
        }
        break;
        case CP_A_N8: {
            setFlags(Alu.sub(regFile.get(Reg.A), read8AfterOpcode()));
        }
        break;
        case CP_A_HLR: {
            setFlags(Alu.sub(regFile.get(Reg.A), read8AtHl()));
        }
        break;
        case DEC_R16SP: {
            setReg16SP(extractReg16(opcode),
                    (Bits.clip(16, extractIntReg16SP(opcode) - 1)));
        }
        break;

        // And, or, xor, complement
        case AND_A_N8: {
            setRegFlags(Reg.A, Alu.and(regFile.get(Reg.A), read8AfterOpcode()));
        }
        break;
        case AND_A_R8: {
            setRegFlags(Reg.A, Alu.and(regFile.get(Reg.A),
                    regFile.get(extractReg(opcode, 0))));
        }
        break;
        case AND_A_HLR: {
            setRegFlags(Reg.A, Alu.and(regFile.get(Reg.A), read8AtHl()));
        }
        break;
        case OR_A_R8: {
            setRegFlags(Reg.A, Alu.or(regFile.get(Reg.A),
                    regFile.get(extractReg(opcode, 0))));
        }
        break;
        case OR_A_N8: {
            setRegFlags(Reg.A, Alu.or(regFile.get(Reg.A), read8AfterOpcode()));
        }
        break;
        case OR_A_HLR: {
            setRegFlags(Reg.A, Alu.or(regFile.get(Reg.A), read8AtHl()));
        }
        break;
        case XOR_A_R8: {
            setRegFlags(Reg.A, Alu.xor(regFile.get(Reg.A),
                    regFile.get(extractReg(opcode, 0))));
        }
        break;
        case XOR_A_N8: {
            setRegFlags(Reg.A, Alu.xor(regFile.get(Reg.A), read8AfterOpcode()));
        }
        break;
        case XOR_A_HLR: {
            setRegFlags(Reg.A, Alu.xor(regFile.get(Reg.A), read8AtHl()));
        }
        break;
        case CPL: {
            regFile.set(Reg.A, Bits.complement8(regFile.get(Reg.A)));
            combineAluFlags(0, FlagSrc.CPU, FlagSrc.V1, FlagSrc.V1,
                    FlagSrc.CPU);
        }
        break;

        // Rotate, shift
        case ROTCA: {
            int v = Alu.rotate(rotateDir(opcode), regFile.get(Reg.A));
            setRegFromAlu(Reg.A, v);
            combineAluFlags(v, FlagSrc.V0, FlagSrc.V0, FlagSrc.V0, FlagSrc.ALU);
        }
        break;
        case ROTA: {
            int rot = Alu.rotate(rotateDir(opcode), regFile.get(Reg.A),
                    testRotateCarry(opcode));
            setRegFromAlu(Reg.A, rot);
            combineAluFlags(rot, FlagSrc.V0, FlagSrc.V0, FlagSrc.V0,
                    FlagSrc.ALU);
        }
        break;
        case ROTC_R8: {
            Reg reg = extractReg(opcode, 0);
            setRegFlags(reg, Alu.rotate(rotateDir(opcode), regFile.get(reg)));
        }
        break;
        case ROT_R8: {
            Reg reg = extractReg(opcode, 0);
            setRegFlags(reg,
                    Alu.rotate(rotateDir(opcode), regFile.get(reg),
                            testRotateCarry(opcode)));

        }
        break;
        case ROTC_HLR: {
            write8AtHlAndSetFlags(Alu.rotate(rotateDir(opcode), read8AtHl()));
        }
        break;
        case ROT_HLR: {
            write8AtHlAndSetFlags(
                    Alu.rotate(rotateDir(opcode), read8AtHl(),
                            testRotateCarry(opcode)));

        }
        break;
        case SWAP_R8: {
            Reg reg = extractReg(opcode, 0);
            setRegFlags(reg, Alu.swap(regFile.get(reg)));
        }
        break;
        case SWAP_HLR: {
            write8AtHlAndSetFlags(Alu.swap(read8AtHl()));
        }
        break;
        case SLA_R8: {
            setRegFlags(extractReg(opcode, 0),
                    Alu.shiftLeft(regFile.get(extractReg(opcode, 0))));
        }
        break;
        case SRA_R8: {
            setRegFlags(extractReg(opcode, 0),
                    Alu.shiftRightA(regFile.get(extractReg(opcode, 0))));
        }
        break;
        case SRL_R8: {
            setRegFlags(extractReg(opcode, 0),
                    Alu.shiftRightL(regFile.get(extractReg(opcode, 0))));
        }
        break;
        case SLA_HLR: {
            write8AtHlAndSetFlags(Alu.shiftLeft(read8AtHl()));
        }
        break;
        case SRA_HLR: {
            write8AtHlAndSetFlags(Alu.shiftRightA(read8AtHl()));
        }
        break;
        case SRL_HLR: {
            write8AtHlAndSetFlags(Alu.shiftRightL(read8AtHl()));
        }
        break;

        // Bit test and set
        case BIT_U3_R8: {
            combineAluFlags(
                    Alu.testBit(regFile.get(extractReg(opcode,0)),
                    getIndex(opcode)), FlagSrc.ALU, FlagSrc.V0,
                    FlagSrc.V1, FlagSrc.CPU);
        }
        break;
        case BIT_U3_HLR: {
            combineAluFlags(Alu.testBit(read8AtHl(), getIndex(opcode)),
                    FlagSrc.ALU, FlagSrc.V0, FlagSrc.V1, FlagSrc.CPU);
        }
        break;
        case CHG_U3_R8: {
            Reg reg = extractReg(opcode, 0);
            boolean SET = get6thBit(opcode);
            if (SET) {
                int newValue = Bits.set(regFile.get(reg), getIndex(opcode), true);
                regFile.set(reg, newValue);
            } else {
                //TODO essayer d'utiliser Bits.set
                int newValue = Bits.set(regFile.get(reg), getIndex(opcode), false);
                regFile.set(reg, newValue);
            }
        }
        break;
        case CHG_U3_HLR: {
            if (get6thBit(opcode)) {
                write8AtHl(read8AtHl() | (1 << Bits
                        .extract(opcode.encoding, 3, 3)));
            } else {
                write8AtHl(read8AtHl() & ~(1 << Bits
                        .extract(opcode.encoding, 3, 3)));
            }
        }
        break;

        // Misc. ALU
        case DAA: {
            int regA = regFile.get(Reg.A);
            int regF = regFile.get(Reg.F);
            int bcd = Alu.bcdAdjust(regA, Bits.test(regF, 6),
                    Bits.test(regF, 5), Bits.test(regF, 4));
            setRegFromAlu(Reg.A, bcd);
            combineAluFlags(bcd, FlagSrc.ALU, FlagSrc.CPU, FlagSrc.V0,
                    FlagSrc.ALU);
        }
        break;
        case SCCF: {
            boolean c = !(Bits.test(regFile.get(Reg.F), 4) && Bits
                    .test(opcode.encoding, 3));
            FlagSrc C = c ? FlagSrc.V1 : FlagSrc.V0;
            combineAluFlags(0, FlagSrc.CPU, FlagSrc.V0, FlagSrc.V0, C);
        }
        break;

        // Jumps
        case JP_HL: {
            nextPC = reg16(Reg16.HL);
        }
        break;
        case JP_N16: {
            nextPC = read16AfterOpcode();
        }
        break;
        case JP_CC_N16: {
            if (extractCondition(opcode)) {
                nextPC = read16AfterOpcode();
                nextNonIdleCycle += opcode.additionalCycles;
            }
        }
        break;
        case JR_E8: {
            nextPC += Bits.clip(16, signedNextInstruction());
        }
        break;
        case JR_CC_E8: {
            if (extractCondition(opcode)) {
                nextPC += Bits.clip(16, signedNextInstruction());
                nextNonIdleCycle += opcode.additionalCycles;
            }
        }
        break;

        // Calls and returns
        case CALL_N16: {
            push16(nextPC);
            nextPC = read16AfterOpcode();
        }
        break;
        case CALL_CC_N16: {
            if (extractCondition(opcode)) {
                push16(nextPC);
                nextPC = read16AfterOpcode();
                nextNonIdleCycle += opcode.additionalCycles;
            }
        }
        break;
        case RST_U3: {
            push16(nextPC);
            nextPC = AddressMap.RESETS
                    [getIndex(opcode)];
        }
        break;
        case RET: {
            nextPC = pop16();
        }
        break;
        case RET_CC: {
            if (extractCondition(opcode)) {
                nextPC = pop16();
                nextNonIdleCycle += opcode.additionalCycles;
            }
        }
        break;

        // Interrupts
        case EDI: {
            IME = Bits.test(opcode.encoding, 3);
        }
        break;
        case RETI: {
            IME = true;
            nextPC = pop16();
        }
        break;

        // Misc control
        case HALT: {
            nextNonIdleCycle = Long.MAX_VALUE;
        }
        break;
        case STOP:
            throw new Error("STOP is not implemented");
        }

        PC = Bits.clip(16, nextPC);
    }

    @Override public int read(int address) {
        Preconditions.checkBits16(address);

        if (address == AddressMap.REG_IE) {
            return IE;
        } else if (address == AddressMap.REG_IF) {
            return IF;
        } else if (AddressMap.HIGH_RAM_START <= address
                && address < AddressMap.HIGH_RAM_END) {
            return highRam.read(address - AddressMap.HIGH_RAM_START);
        }
        return NO_DATA;
    }

    @Override public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        if (address == AddressMap.REG_IE) {
            IE = data;
        } else if (address == AddressMap.REG_IF) {
            IF = data;
        } else if (AddressMap.HIGH_RAM_START <= address
                && address < AddressMap.HIGH_RAM_END) {
            highRam.write(address - AddressMap.HIGH_RAM_START, data);
        }
    }



    /* ***********************************************************************
      TESTS HELPER ***********************************************************
     */

    /**
     * Returns a table containing (respectively) registers  PC, SP, A, F, B, C,
     * D, E, H and L.
     *
     * @return a table of registers (integers)
     */
    public int[] _testGetPcSpAFBCDEHL() {
        int[] tabR = new int[] { PC, SP, regFile.get(Reg.A), regFile.get(Reg.F),
                regFile.get(Reg.B), regFile.get(Reg.C), regFile.get(Reg.D),
                regFile.get(Reg.E), regFile.get(Reg.H), regFile.get(Reg.L) };

        return tabR;
    }



    /* ***********************************************************************
      REGISTERS MANAGEMENT ***************************************************
     */

    /**
     * Returns the value contained in the given pair of registers r.
     *
     * @param r Reg16, pair of registers (each containing an 8-bits value)
     * @return a 16-bits integer value from r
     */
    private int reg16(Reg16 r) {
        int h = regFile.get((r.reg1));
        int l = regFile.get((r.reg2));
        return Bits.make16(h, l);
    }

    /**
     * Changes the value contained in the pair of registers r; the LSB are
     * changed to 0 if the given pair is AF.
     *
     * @param r    pair of 8-bits registers
     * @param newV integer, the new value that will be contained by the register
     */
    private void setReg16(Reg16 r, int newV) {
        Preconditions.checkBits16(newV);

        int l = Bits.clip(8, newV);
        int m = Bits.extract(newV, 8, 8);
        if (r == Reg16.AF) {
            regFile.set(Reg.A, m);
            regFile.set(Reg.F, l & 0xF0);
        } else {
            regFile.set(r.reg1, m);
            regFile.set(r.reg2, l);
        }
    }

    /**
     * Changes the value contained in the pair of registers r; register SP is
     * changed instead of AF if the given pair is AF.
     *
     * @param r    pair of 8-bits registers
     * @param newV integer, the new value that will be contained by the register
     */
    private void setReg16SP(Reg16 r, int newV) {
        Preconditions.checkBits16(newV);

        if (r == Reg16.AF)
            SP = newV;
        else
            setReg16(r, newV);
    }



    /* ***********************************************************************
      PARAMETERS EXTRACTION **************************************************
     */

    /**
     * Extracts an 8-bits register's ID from the given opcode, from the given
     * index.
     *
     * @param opcode,  operation code
     * @param startBit integer, index
     * @return a Reg, the register's ID
     * @throws IllegalArgumentException if startBit is greater or equal to 6
     */
    private Reg extractReg(Opcode opcode, int startBit) {
        Preconditions.checkArgument(startBit < 6);
        Reg[] registers = new Reg[]
                {Reg.B, Reg.C, Reg.D, Reg.E, Reg.H, Reg.L, null, Reg.A };

        return registers[Bits.extract(opcode.encoding, startBit, 3)];
    }

    /**
     * Extracts 2 register's IDs, from the given opcode.
     *
     * @param opcode, operation code
     * @return a Reg16, a pair of register's IDs
     */
    private Reg16 extractReg16(Opcode opcode) {
        Reg16[] registers = new Reg16[]
                { Reg16.BC, Reg16.DE, Reg16.HL, Reg16.AF };

        return registers[Bits.extract(opcode.encoding, 4, 2)];
    }

    private int extractIntReg16SP(Opcode opcode) {
        return ((Bits.extract(opcode.encoding, 4, 2) == 0b11)) ?
                SP : reg16(extractReg16(opcode));
    }

    /**
     * Returns 1 (if bit = 0) or -1 (if bit = 1) depending on the bit of index 4
     * in the opcode's encoding; used to represent the incrementation or
     * decrementing of the registers pair HL.
     *
     * @param opcode, operation code
     * @return an integer equal to 1 or -1
     */
    private int extractHlIncrement(Opcode opcode) {
        return (Bits.test(opcode.encoding, 4)) ? -1 : 1;
    }

    /**
     * Extracts the condition (bits 3 & 4 of opcode) and checks if it is true
     * or false.
     *
     * @param opcode, operation code
     * @return true if the condition is true, false otherwise
     */
    private boolean extractCondition(Opcode opcode) {
        int Z = Bits.extract(regFile.get(Reg.F), 7, 1);
        int C = Bits.extract(regFile.get(Reg.F), 4, 1);
        boolean[] conditions = new boolean[] {Z == 0, Z == 1, C == 0, C == 1};

        return conditions[Bits.extract(opcode.encoding, 3, 2)];
    }

    /**
     * Extracts the rotation's direction from bit 3 in the given opcode.
     *
     * @param opcode operation code of an instruction
     * @return a boolean, true if direction is left, false otherwise
     */
    private Alu.RotDir rotateDir(Opcode opcode) {
        boolean b = Bits.test(opcode.encoding, 3);
        return b ? RIGHT : LEFT;
    }

    /**
     * Returns the index of the bit (from bit 3 to 5) we want to test or modify.
     *
     * @param opcode operation code of an instruction
     * @return a integer representing the index
     */
    private int getIndex(Opcode opcode) {
        return Bits.extract(opcode.encoding, 3, 3);
    }

    /**
     * Returns the value from the bit 6 of the given opcode.
     *
     * @param opcode operation code of an instruction
     * @return an boolean, true the bit is 1, false otherwise
     */
    private boolean get6thBit(Opcode opcode) {
        return Bits.test(opcode.encoding, 6);
    }

    private boolean testCarry(Opcode opcode) {
        return Bits.test(regFile.get(Reg.F), 4) && Bits
                .test(opcode.encoding, 3);
    }

    private boolean testRotateCarry(Opcode opcode) {
        return Bits.test(regFile.get(Reg.F), 4);
    }

    private int signedNextInstruction() {
        return Bits.signExtend8(read8AfterOpcode());
    }

    /* ***********************************************************************
      ACCESS TO THE BUS ******************************************************
     */

    /**
     * Connects the Cpu to the system bus.
     *
     * @param bus, system bus
     */
    public void attachTo(Bus bus) {
        this.bus = bus;
        Component.super.attachTo(bus);
    }

    /**
     * Reads the 8-bit value at the given address of the bus.
     *
     * @param address int
     * @return 8-bit value
     */
    private int read8(int address) {
        return bus.read(address);
    }

    /**
     * Reads from the bus the 8-bit value at the address from HL.
     *
     * @return 8-bit value
     */
    private int read8AtHl() {
        return read8(reg16(Reg16.HL));
    }

    /**
     * Reads from the bus the 8-bit value at the address following pc.
     *
     * @return 8-bit value
     */
    private int read8AfterOpcode() {
        return read8(PC + 1);
    }

    /**
     * Reads from the bus the 16-bit value at the given address.
     *
     * @param address int
     * @return 16-bit value
     */
    private int read16(int address) {
        int left = Preconditions.checkBits8(read8(address + 1));
        int right = Preconditions.checkBits8(read8(address));
        return Bits.make16(left, right);
    }

    /**
     * Reads from the bus the 16-bit value at the address following pc.
     *
     * @return 16-bit value
     */
    private int read16AfterOpcode() {
        return read16(PC + 1);
    }

    /**
     * Writes on the bus the given value at the given address.
     *
     * @param address int
     * @param v       8-bit value
     */
    private void write8(int address, int v) {
        Preconditions.checkBits8(v);

        bus.write(address, v);
    }

    /**
     * Writes on the bus the given value at the given address.
     *
     * @param address int
     * @param v       16-bit value
     */
    private void write16(int address, int v) {
        write8(address, Bits.clip(8, v));
        write8(address + 1, Bits.extract(v, 8, 8));
    }

    /**
     * Writes on the bus the given value at the address contained in
     * register HL.
     *
     * @param v 8-bit value
     */
    private void write8AtHl(int v) {
        write8(reg16(Reg16.HL), v);
    }

    /**
     * Decrements the address contained in the stack pointer by 2 units,
     * then writes the given 16-bit value at this new address.
     *
     * @param v 16-bit value
     */
    private void push16(int v) {
        SP -= 2;
        SP = Bits.clip(16, SP);
        write16(SP, v);
    }

    /**
     * Reads and returns the 16-bit value at the address contained in SP,
     * then increments SP by 2 units.
     *
     * @return 16-bit value pointed at by SP
     */
    private int pop16() {
        int x = SP;
        SP += 2;
        SP = Bits.clip(16, SP);

        return read16(x);
    }



    /* ***********************************************************************
      FLAGS MANAGEMENT *******************************************************
     */

    /**
     * Extracts the value contained in the given pair vf (value and flags
     * packed) and places it in the given register r.
     *
     * @param r  Reg, register in which we put the value
     * @param vf integer, containing the wanted value with its flags
     */
    private void setRegFromAlu(Reg r, int vf) {
        regFile.set(r, Alu.unpackValue(vf));
    }

    /**
     * Extracts the flags from the given pair valueFlags and places them in
     * register F.
     *
     * @param valueFlags integer, containing the wanted flags and a value
     */
    private void setFlags(int valueFlags) {
        regFile.set(Reg.F, Alu.unpackFlags(valueFlags));
    }

    /**
     * Combines the effects of previous setRegFromAlu and setFlags methods.
     *
     * @param r  Reg, register in which we put the value
     * @param vf integer, containing the wanted value and flags
     */
    private void setRegFlags(Reg r, int vf) {
        setRegFromAlu(r, vf);
        setFlags(vf);
    }

    /**
     * Extracts the value contained in the given pair vf and write it in the bus
     * at the address from HL, then extracts the flags from vf and put them
     * in F.
     *
     * @param vf integer, containing the wanted value and flags
     */
    private void write8AtHlAndSetFlags(int vf) {
        write8AtHl(Bits.clip(8, Alu.unpackValue(vf)));
        setFlags(vf);
    }

    public enum FlagSrc {
        V0, V1, ALU, CPU
    }

    /**
     * Combines the flags contained in the given pair vf with the ones in F,
     * depending on z, n, h and c; Stores the final result in F.
     *
     * @param vf integer, containing the wanted flags and a value
     * @param z  FlagSrc, flag source
     * @param n  FlagSrc, flag source
     * @param h  FlagSrc, flag source
     * @param c  FlagSrc, flag source
     */
    private void combineAluFlags(int vf, FlagSrc z, FlagSrc n, FlagSrc h,
            FlagSrc c) {
        int vecALU = vecFlags(FlagSrc.ALU, z, n, h, c) & vf;
        int vecCPU = vecFlags(FlagSrc.CPU, z, n, h, c) & regFile.get(Reg.F);
        int vecV1 = vecFlags(FlagSrc.V1, z, n, h, c);
        regFile.set(Reg.F, vecALU | vecCPU | vecV1);
    }

    /**
     * Generates a vector corresponding to the given source src.
     *
     * @param src FlagSrc, the source
     * @param z   FlagSrc, flag source
     * @param n   FlagSrc, flag source
     * @param h   FlagSrc, flag source
     * @param c   FlagSrc, flag source
     */
    private int vecFlags(FlagSrc src, FlagSrc z, FlagSrc n, FlagSrc h,
            FlagSrc c) {
        return Alu.maskZNHC(src == z, src == n, src == h, src == c);
    }
}
