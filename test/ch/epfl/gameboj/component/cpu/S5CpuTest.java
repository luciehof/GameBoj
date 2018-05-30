// Gameboj stage 5

package ch.epfl.gameboj.component.cpu;

import static ch.epfl.gameboj.component.cpu.Opcode.*;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Assembler.Program;

public final class S5CpuTest {
    // Jumps
    @Test
    void jpHlWorks() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int target = rng.nextInt(0x1_00_00);
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, target);
            asm.emit(JP_HL);
            assertEquals(CpuState.of(target, 0, target), stateAfter(asm));
        }
    }

    @Test
    void jpN16Works() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int target = rng.nextInt(0x1_00_00);
            Assembler asm = new Assembler();
            asm.emit(JP_N16, target);
            assertEquals(CpuState.of(target, 0, 0), stateAfter(asm));
        }
    }

    @Test
    void jpCcN16Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0006, 0x0000, 0x6600000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x6600000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0010000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0006, 0x0000, 0x6680000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0006, 0x0000, 0x6610000000000000L)).iterator();
        Opcode[][] ops = new Opcode[][] {
            { NOP, JP_Z_N16 },
            { XOR_A_A, JP_Z_N16 },
            { NOP, JP_C_N16 },
            { SCF, JP_C_N16 },
            { NOP, JP_NZ_N16 },
            { XOR_A_A, JP_NZ_N16 },
            { NOP, JP_NC_N16 },
            { SCF, JP_NC_N16 },
        };
        for (Opcode[] os: ops) {
            Assembler asm = new Assembler();
            asm.emit(os[0]);
            asm.emit(os[1], 0);
            asm.emit(LD_A_N8, 0x66);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void jrE8Works() {
        for (int d = Byte.MIN_VALUE; d <= Byte.MAX_VALUE; ++d) {
            Assembler asm = new Assembler();
            for (int i = 0; i < 126; ++i)
                asm.emit(NOP);
            asm.emit(JR_E8, d & 0xFF);
            assertEquals(CpuState.of(d - Byte.MIN_VALUE, 0, 0), stateAfter(asm));
        }
    }

    @Test
    void jrCcE8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x6600000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x6600000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0010000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0005, 0x0000, 0x6680000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0005, 0x0000, 0x6610000000000000L)).iterator();

        Opcode[][] ops = new Opcode[][] {
            { NOP, JR_Z_E8 },
            { XOR_A_A, JR_Z_E8 },
            { NOP, JR_C_E8 },
            { SCF, JR_C_E8 },
            { NOP, JR_NZ_E8 },
            { XOR_A_A, JR_NZ_E8 },
            { NOP, JR_NC_E8 },
            { SCF, JR_NC_E8 },
        };
        for (Opcode[] os: ops) {
            Assembler asm = new Assembler();
            asm.emit(os[0]);
            asm.emit(os[1], -3 & 0xFF);
            asm.emit(LD_A_N8, 0x66);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    // Calls and returns

    @Test
    void callN16Works() {
        Assembler asm = new Assembler();
        asm.emit(LD_SP_N16, 0xE000);
        asm.emit(CALL_N16, 6);
        asm.emit(POP_BC);
        assertEquals(CpuState.of(0x0007, 0xE000, 0x0000000600000000L), stateAfter(asm, new WorkRam()));
    }

    @Test
    void callCcN16Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000F, 0xE000, 0x0000DEADDEAD0000L),
                CpuState.of(0x000C, 0xDFFE, 0x0080000BDEAD0000L),
                CpuState.of(0x000F, 0xE000, 0x0000DEADDEAD0000L),
                CpuState.of(0x000C, 0xDFFE, 0x0010000BDEAD0000L),
                CpuState.of(0x000C, 0xDFFE, 0x0000000BDEAD0000L),
                CpuState.of(0x000F, 0xE000, 0x0080DEADDEAD0000L),
                CpuState.of(0x000C, 0xDFFE, 0x0000000BDEAD0000L),
                CpuState.of(0x000F, 0xE000, 0x0010DEADDEAD0000L)).iterator();

        Opcode[][] ops = new Opcode[][] {
            { NOP, CALL_Z_N16 },
            { XOR_A_A, CALL_Z_N16 },
            { NOP, CALL_C_N16 },
            { SCF, CALL_C_N16 },
            { NOP, CALL_NZ_N16 },
            { XOR_A_A, CALL_NZ_N16 },
            { NOP, CALL_NC_N16 },
            { SCF, CALL_NC_N16 },
        };
        for (Opcode[] os: ops) {
            Assembler asm = new Assembler();
            asm.emit(LD_SP_N16, 0xE000);
            asm.emit(LD_DE_N16, 0xDEAD);
            asm.emit(PUSH_DE);
            asm.emit(os[0]);
            asm.emit(os[1], 11);
            asm.emit(POP_BC);
            asm.emit(NOP);
            asm.emit(NOP);
            asm.emit(NOP);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void rstU3Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0000, 0xDFFE, 0x0000000000000000L),
                CpuState.of(0x0008, 0xDFFE, 0x0000000000000000L),
                CpuState.of(0x0010, 0xDFFE, 0x0000000000000000L),
                CpuState.of(0x0018, 0xDFFE, 0x0000000000000000L),
                CpuState.of(0x0020, 0xDFFE, 0x0000000000000000L),
                CpuState.of(0x0028, 0xDFFE, 0x0000000000000000L),
                CpuState.of(0x0030, 0xDFFE, 0x0000000000000000L),
                CpuState.of(0x0038, 0xDFFE, 0x0000000000000000L)).iterator();

        Iterator<Opcode> opIt = List.of(RST_0, RST_1, RST_2, RST_3, RST_4, RST_5, RST_6, RST_7).iterator();
        while (opIt.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(LD_SP_N16, 0xE000);
            asm.emit(opIt.next());
            WorkRam wRam = new WorkRam();
            assertEquals(exp.next(), stateAfter(asm, wRam));
            Assertions.assertEquals(0x00, wRam.read(0xDFFF));
            Assertions.assertEquals(0x04, wRam.read(0xDFFE));
        }
    }

    @Test
    void retWorks() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int target = rng.nextInt(0x1_0000);
            Assembler asm = new Assembler();
            asm.emit(LD_SP_N16, 0xE000);
            asm.emit(LD_BC_N16, target);
            asm.emit(PUSH_BC);
            asm.emit(RET);
            assertEquals(CpuState.of(target, 0xE000, (long)target << 32), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void retCcWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000F, 0xDFFE, 0x0000000000030000L),
                CpuState.of(0x0005, 0xE000, 0x0000000000050000L),
                CpuState.of(0x000F, 0xDFFE, 0x0000000000030000L),
                CpuState.of(0x0005, 0xE000, 0x0010000000050000L),
                CpuState.of(0x0005, 0xE000, 0x0000000000050000L),
                CpuState.of(0x000F, 0xDFFE, 0x0080000000030000L),
                CpuState.of(0x0005, 0xE000, 0x0000000000050000L),
                CpuState.of(0x000F, 0xDFFE, 0x0010000000030000L)).iterator();

        Opcode[][] ops = new Opcode[][] {
            { NOP, RET_Z },
            { XOR_A_A, RET_Z },
            { NOP, RET_C },
            { SCF, RET_C },
            { NOP, RET_NZ },
            { XOR_A_A, RET_NZ },
            { NOP, RET_NC },
            { SCF, RET_NC },

        };
        for (Opcode[] os: ops) {
            Assembler asm = new Assembler();
            asm.emit(LD_SP_N16, 0xE000);
            asm.emit(INC_E);
            asm.emit(INC_E);
            asm.emit(INC_E);
            asm.emit(PUSH_DE);
            asm.emit(RLCA); // Z = N = H = 0
            asm.emit(os[0]);
            asm.emit(os[1]);
            asm.emit(NOP);
            asm.emit(NOP);
            asm.emit(NOP);
            asm.emit(NOP);
            asm.emit(NOP);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }

    }

    // Interrupts

    @Test
    void imeIsInitiallyFalse() {
        Assembler asm = new Assembler();
        asm.emit(LD_A_N8, 0xFF);
        asm.emit(LD_N8R_A, 0xFF);
        asm.emit(LD_N8R_A, 0x0F);
        asm.emit(INC_A);
        assertEquals(CpuState.of(0x0007, 0x0000, 0x00A0000000000000L), stateAfter(asm));
    }

    @Test
    void eiWorks() {
        Assembler asm = new Assembler();
        asm.emit(DI);
        asm.emit(LD_SP_N16, 0xE000);
        asm.emit(LD_A_N8, 0xFF);
        asm.emit(LD_N8R_A, 0xFF);
        asm.emit(LD_N8R_A, 0x0F);
        asm.emit(EI);
        asm.emit(NOP);
        asm.emit(NOP);
        asm.emit(NOP);
        asm.emit(NOP);
        asm.emit(NOP);
        Component wRam = new WorkRam();
        assertEquals(CpuState.of(0x0040, 0xDFFE, 0xFF00000000000000L), stateAfter(asm, wRam));
        Assertions.assertEquals(0x00, wRam.read(0xDFFF));
        Assertions.assertEquals(0x0B, wRam.read(0xDFFE));
    }

    @Test
    void diWorks() {
        Assembler asm = new Assembler();
        asm.emit(EI);
        asm.emit(LD_SP_N16, 0xE000);
        asm.emit(LD_A_N8, 0xFF);
        asm.emit(LD_N8R_A, 0xFF);
        asm.emit(DI);
        asm.emit(LD_N8R_A, 0x0F);
        asm.emit(INC_A);
        asm.emit(INC_A);
        asm.emit(INC_A);
        asm.emit(INC_A);
        asm.emit(INC_A);
        asm.emit(INC_A);
        assertEquals(CpuState.of(0x0011, 0xE000, 0x0500000000000000L), stateAfter(asm));

    }

    @Test
    void retiWorks() {
        Assembler asm = new Assembler();
        asm.emit(DI);
        asm.emit(LD_SP_N16, 0xE000);
        asm.emit(LD_BC_N16, 9);
        asm.emit(PUSH_BC);
        asm.emit(RETI);
        asm.emit(LD_A_N8, 0xFF);
        asm.emit(LD_N8R_A, 0xFF);
        asm.emit(LD_N8R_A, 0x0F);
        asm.emit(NOP);
        asm.emit(NOP);
        asm.emit(NOP);
        asm.emit(NOP);
        asm.emit(NOP);
        WorkRam wRam = new WorkRam();
        assertEquals(CpuState.of(0x0040, 0xDFFE, 0xFF00000900000000L), stateAfter(asm, wRam));
        Assertions.assertEquals(0x00, wRam.read(0xDFFF));
        Assertions.assertEquals(0x0F, wRam.read(0xDFFE));
    }

    @Test
    void haltWorks() {
        Assembler asm = new Assembler();
        asm.emit(LD_BC_N16, 0xBEEF);
        asm.emit(HALT);
        asm.emit(INC_A);
        asm.emit(INC_B);
        asm.emit(INC_C);
        asm.emit(INC_D);
        asm.emit(INC_E);
        asm.emit(INC_H);
        asm.emit(INC_L);
        assertEquals(CpuState.of(0x0004, 0x0000, 0x0000BEEF00000000L), stateAfter(asm));
    }

    @Test
    void haltDoesNotHaltWhenAnInterruptIsPending() {
        Assembler asm = new Assembler();
        asm.emit(DI);
        asm.emit(LD_A_N8, 0xFF);
        asm.emit(LD_N8R_A, 0xFF);
        asm.emit(LD_N8R_A, 0x0F);
        asm.emit(XOR_A_A);
        asm.emit(HALT);
        asm.emit(LD_BC_N16, 0xDEAD);
        asm.emit(LD_DE_N16, 0xBEEF);
        asm.emit(LD_HL_N16, 0x0F00);
        // Note: the number of cycles is off by 1, since HALT.cycles == 0 (doesn't matter)
        assertEquals(CpuState.of(0x0012, 0x0000, 0x0080DEADBEEF0F00L), stateAfter(asm));
    }

    @Test
    void interruptsWakeProcessorEvenWhenImeIsFalse() {
        Assembler asm = new Assembler();
        asm.emit(DI);
        asm.emit(LD_A_N8, 0xFF);
        asm.emit(LD_N8R_A, 0xFF);
        asm.emit(XOR_A_A);
        asm.emit(HALT);
        asm.emit(INC_A);
        asm.emit(INC_B);
        asm.emit(INC_C);
        asm.emit(INC_D);
        asm.emit(INC_E);
        asm.emit(INC_H);
        asm.emit(INC_L);
        asm.emit(JR_E8, -9 & 0xFF);
        MiniGameBoy mgb = MiniGameBoy.forProgramOf(asm);
        int progCycles = mgb.prog.cycles();
        mgb.cycleUntil(progCycles);
        mgb.cpu.requestInterrupt(Cpu.Interrupt.VBLANK);
        mgb.cycleUntil(progCycles + 7);
        assertEquals(CpuState.of(0x000E, 0x0000, 0x0100010101010101L), mgb.cpuState());
    }

    @Test
    void interruptPriorityWorks() {
        for (int rIe = 0; rIe <= 0x1F; ++rIe) {
            for (int rIf = 0; rIf <= 0x1F; ++rIf) {
                Assembler asm = new Assembler();
                asm.emit(LD_SP_N16, 0xE000);
                asm.emit(LD_A_N8, rIe);
                asm.emit(LD_N8R_A, 0xFF);
                asm.emit(LD_A_N8, rIf);
                asm.emit(LD_N8R_A, 0x0F);
                asm.emit(XOR_A_A);
                asm.emit(EI);
                asm.emit(NOP);

                int ief = rIe & rIf;
                if (ief == 0) {
                    assertEquals(CpuState.of(0x000E, 0xE000, 0x0080000000000000L), stateAfter(asm));
                } else {
                    int i = 0;
                    while ((ief & (1 << i)) == 0)
                        ++i;
                    int expPC = 0x40 + (i << 3);
                    assertEquals(CpuState.of(expPC, 0xDFFE, 0x0080000000000000L), stateAfter(asm));
                }
            }
        }
    }

    // IE and IF registers
    
    @Test
    void ieAndIfCanBeWrittenAndRead() {
        for (int ief = 0; ief <= 0x1F; ++ief) {
            int iefN = ~ief & 0xFF;
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, ief);
            asm.emit(LD_N8R_A, 0xFF);
            asm.emit(LD_A_N8, iefN);
            asm.emit(LD_N8R_A, 0x0F);
            asm.emit(XOR_A_A);
            asm.emit(LD_A_N8R, 0xFF);
            asm.emit(LD_B_A);
            asm.emit(LD_A_N8R, 0x0F);
            assertEquals(CpuState.of(0xE, 0, iefN, 0x80, ief, 0, 0, 0, 0, 0), stateAfter(asm));
        }
    }
    
    // High RAM

    @Test
    void highRamCanBeRead() {
        Assembler asm = new Assembler();
        Random rng = newRandom();
        byte[] highRamData = new byte[0xFFFF - 0xFF80];
        rng.nextBytes(highRamData);
        for (int a = 0xFF80; a < 0xFFFF; ++a) {
            asm.emit(LD_A_N16R, a);
            asm.emit(LD_B_N8, Byte.toUnsignedInt(highRamData[a - 0xFF80]));
            asm.emit(CP_A_B);
            asm.emit(JR_NZ_E8, -2 & 0xFF);
        }
        MiniGameBoy mgb = MiniGameBoy.forProgramOf(asm);
        for (int a = 0xFF80; a < 0xFFFF; ++a)
            mgb.bus.write(a, Byte.toUnsignedInt(highRamData[a - 0xFF80]));
        mgb.cycleUntil(mgb.prog.cycles());
        assertEquals(CpuState.of(0x03F8, 0x0000, 0x8DC08D0000000000L), mgb.cpuState());
    }

    @Test
    void highRamCanBeWritten() {
        Assembler asm = new Assembler();
        Random rng = newRandom();
        byte[] highRamData = new byte[0xFFFF - 0xFF80];
        rng.nextBytes(highRamData);
        for (int a = 0xFF80; a < 0xFFFF; ++a) {
            int c = Byte.toUnsignedInt(highRamData[a - 0xFF80]);
            asm.emit(LD_A_N8, c);
            asm.emit(LD_N16R_A, a);
        }
        MiniGameBoy mgb = MiniGameBoy.forProgramOf(asm);
        mgb.cycleUntil(mgb.prog.cycles());
        for (int a = 0xFF80; a < 0xFFFF; ++a) {
            int exp = Byte.toUnsignedInt(highRamData[a - 0xFF80]);
            Assertions.assertEquals(exp, mgb.bus.read(a));
        }
    }

    // More complex tests
    
    @Test
    void iterativeFiboWorks() {
        Assembler asm = new Assembler();
        asm.emit(LD_B_N8, 0);
        asm.emit(LD_A_N8, 1);
        asm.emit(LD_C_N8, 10);
        asm.emit(LD_D_A);
        asm.emit(ADD_A_B);
        asm.emit(LD_B_D);
        asm.emit(DEC_C);
        asm.emit(JP_NZ_N16, 6);
        asm.emit(HALT);
        MiniGameBoy mgb = MiniGameBoy.forProgramOf(asm);
        mgb.cycleUntil(500);
        assertEquals(CpuState.of(0x000E, 0x0000, 0x59C0370037000000L), mgb.cpuState());
    }
    
    @Test
    void recursiveFiboWorks() {
        Assembler asm = new Assembler();
        asm.emit(LD_SP_N16, 0xFFFF);
        asm.emit(LD_A_N8, 11);
        asm.emit(CALL_N16, 9);
        asm.emit(HALT);
        asm.emit(CP_A_N8, 2);
        asm.emit(RET_C);
        asm.emit(PUSH_BC);
        asm.emit(DEC_A);
        asm.emit(LD_B_A);
        asm.emit(CALL_N16, 9);
        asm.emit(LD_C_A);
        asm.emit(LD_A_B);
        asm.emit(DEC_A);
        asm.emit(CALL_N16, 9);
        asm.emit(ADD_A_C);
        asm.emit(POP_BC);
        asm.emit(RET);
        MiniGameBoy mgb = MiniGameBoy.forProgramOf(asm);
        mgb.cycleUntil(10000);
        assertEquals(CpuState.of(0x0009, 0xFFFF, 0x5900000000000000L), mgb.cpuState());
    }
    
    // The following method can be used to generate correct test values
    @SuppressWarnings("unused")
    private static void assertEquals(CpuState actual) {
        System.out.println(actual.toJavaString() + ",");
    }

    private static void assertEquals(CpuState expected, CpuState actual) {
        Assertions.assertEquals(expected, actual,
                () -> String.format("Expected state: [%s], actual: [%s]", expected, actual));
    }

    private CpuState stateAfter(Assembler asm, Component... components) {
        MiniGameBoy mgb = MiniGameBoy.forProgramOf(asm, List.of(components));
        mgb.cycleUntil(mgb.prog.cycles());
        return mgb.cpuState();
    }

    private static class MiniGameBoy {
        public final Program prog;
        public final Bus bus;
        public final Cpu cpu;
        private long cycle;

        public static MiniGameBoy forProgramOf(Assembler asm, Component... components) {
            return forProgramOf(asm, List.of(components));
        }

        public static MiniGameBoy forProgramOf(Assembler asm, List<Component> components) {
            Program prog = asm.program();
            Component progRom = prog.rom();
            Cpu cpu = new Cpu();
            Bus bus = new Bus();
            progRom.attachTo(bus);
            cpu.attachTo(bus);
            for (Component c2: components)
                c2.attachTo(bus);
            MiniGameBoy mgb = new MiniGameBoy(prog, bus, cpu);
            return mgb;
        }

        public MiniGameBoy(Program prog, Bus bus, Cpu cpu) {
            this.prog = prog;
            this.bus = bus;
            this.cpu = cpu;
        }

        public void cycleUntil(long c) {
            while (cycle < c)
                cpu.cycle(cycle++);
        }

        public CpuState cpuState() {
            return CpuState.ofArray(cpu._testGetPcSpAFBCDEHL());
        }
    }

    private static class WorkRam implements Component {
        private final byte[] data = new byte[0xE000 - 0xC000];

        @Override
        public int read(int address) {
            if (0xC000 <= address && address < 0xE000)
                return Byte.toUnsignedInt(data[address - 0xC000]);
            else
                return 0x100;
        }

        @Override
        public void write(int address, int d) {
            if (0xC000 <= address && address < 0xE000)
                data[address - 0xC000] = (byte)d;
        }
    }
}
