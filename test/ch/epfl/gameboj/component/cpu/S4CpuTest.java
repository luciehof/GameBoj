// Gameboj stage 4

package ch.epfl.gameboj.component.cpu;

import static ch.epfl.gameboj.component.cpu.Opcode.*;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Assembler.Program;

public class S4CpuTest {
    // Additions
    @Test
    void addAN8Works() {
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 },
            { 0xFF, 0xFF },
            { 0xF, 0xF1 },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0004, 0x0000, 0x1020000000000000L),
                CpuState.of(0x0004, 0x0000, 0xF020000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFE30000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00B0000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(ADD_A_N8, lr[0]);
            asm.emit(ADD_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void addAR8Works() {
        List<Opcode> adds =
                List.of(ADD_A_A, ADD_A_B, ADD_A_C, ADD_A_D, ADD_A_E, ADD_A_H, ADD_A_L);
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 }
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0200000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100010000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000100000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000001000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000010000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000100L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000001L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0005, 0x0000, 0x1E20000000000000L),
                CpuState.of(0x0005, 0x0000, 0x10200F0000000000L),
                CpuState.of(0x0005, 0x0000, 0x1020000F00000000L),
                CpuState.of(0x0005, 0x0000, 0x102000000F000000L),
                CpuState.of(0x0005, 0x0000, 0x10200000000F0000L),
                CpuState.of(0x0005, 0x0000, 0x1020000000000F00L),
                CpuState.of(0x0005, 0x0000, 0x102000000000000FL),
                CpuState.of(0x0005, 0x0000, 0x0200000000000000L),
                CpuState.of(0x0005, 0x0000, 0xF020010000000000L),
                CpuState.of(0x0005, 0x0000, 0xF020000100000000L),
                CpuState.of(0x0005, 0x0000, 0xF020000001000000L),
                CpuState.of(0x0005, 0x0000, 0xF020000000010000L),
                CpuState.of(0x0005, 0x0000, 0xF020000000000100L),
                CpuState.of(0x0005, 0x0000, 0xF020000000000001L),
                CpuState.of(0x0005, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0090800000000000L),
                CpuState.of(0x0005, 0x0000, 0x0090008000000000L),
                CpuState.of(0x0005, 0x0000, 0x0090000080000000L),
                CpuState.of(0x0005, 0x0000, 0x0090000000800000L),
                CpuState.of(0x0005, 0x0000, 0x0090000000008000L),
                CpuState.of(0x0005, 0x0000, 0x0090000000000080L)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> add = adds.iterator();
            for (int r = 0; r < adds.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                asm.emit(add.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void addAHLRWorks() {
        Assembler asm = new Assembler();
        asm.emit(ADD_A_HLR);
        asm.emit(ADD_A_HLR);
        assertEquals(CpuState.of(0x0002, 0x0000, 0x0C10000000000000L), stateAfter(asm));
    }


    @Test
    void adcAN8Works() {
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 },
            { 0xFF, 0xFF },
            { 0xF, 0xF1 },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0200000000000000L),
                CpuState.of(0x0006, 0x0000, 0x1120000000000000L),
                CpuState.of(0x0006, 0x0000, 0xE020000000000000L),
                CpuState.of(0x0006, 0x0000, 0x8100000000000000L),
                CpuState.of(0x0006, 0x0000, 0xFE30000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0F10000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(ADC_A_N8, lr[0]);
            asm.emit(ADC_A_N8, lr[0]);
            asm.emit(ADC_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void adcAR8Works() {
        List<Opcode> adds =
                List.of(ADC_A_A, ADC_A_B, ADC_A_C, ADC_A_D, ADC_A_E, ADC_A_H, ADC_A_L);
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 }
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0400000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0200010000000000L),
                CpuState.of(0x0006, 0x0000, 0x0200000100000000L),
                CpuState.of(0x0006, 0x0000, 0x0200000001000000L),
                CpuState.of(0x0006, 0x0000, 0x0200000000010000L),
                CpuState.of(0x0006, 0x0000, 0x0200000000000100L),
                CpuState.of(0x0006, 0x0000, 0x0200000000000001L),
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x3C20000000000000L),
                CpuState.of(0x0006, 0x0000, 0x1F000F0000000000L),
                CpuState.of(0x0006, 0x0000, 0x1F00000F00000000L),
                CpuState.of(0x0006, 0x0000, 0x1F0000000F000000L),
                CpuState.of(0x0006, 0x0000, 0x1F000000000F0000L),
                CpuState.of(0x0006, 0x0000, 0x1F00000000000F00L),
                CpuState.of(0x0006, 0x0000, 0x1F0000000000000FL),
                CpuState.of(0x0006, 0x0000, 0x0400000000000000L),
                CpuState.of(0x0006, 0x0000, 0xF100010000000000L),
                CpuState.of(0x0006, 0x0000, 0xF100000100000000L),
                CpuState.of(0x0006, 0x0000, 0xF100000001000000L),
                CpuState.of(0x0006, 0x0000, 0xF100000000010000L),
                CpuState.of(0x0006, 0x0000, 0xF100000000000100L),
                CpuState.of(0x0006, 0x0000, 0xF100000000000001L),
                CpuState.of(0x0006, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0006, 0x0000, 0x8100800000000000L),
                CpuState.of(0x0006, 0x0000, 0x8100008000000000L),
                CpuState.of(0x0006, 0x0000, 0x8100000080000000L),
                CpuState.of(0x0006, 0x0000, 0x8100000000800000L),
                CpuState.of(0x0006, 0x0000, 0x8100000000008000L),
                CpuState.of(0x0006, 0x0000, 0x8100000000000080L)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> add = adds.iterator();
            for (int r = 0; r < adds.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                Opcode a = add.next();
                asm.emit(a);
                asm.emit(a);
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void incR8Works() {
        List<Opcode> incs = List.of(INC_A, INC_B, INC_C, INC_D, INC_E, INC_H, INC_L);
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0010, 0x0000, 0x1020000000000000L),
                CpuState.of(0x0010, 0x0000, 0x0020100000000000L),
                CpuState.of(0x0010, 0x0000, 0x0020001000000000L),
                CpuState.of(0x0010, 0x0000, 0x0020000010000000L),
                CpuState.of(0x0010, 0x0000, 0x0020000000100000L),
                CpuState.of(0x0010, 0x0000, 0x0020000000001000L),
                CpuState.of(0x0010, 0x0000, 0x0020000000000010L)).iterator();
        for (Opcode inc: incs) {
            Assembler asm = new Assembler();
            for (int i = 0; i <= 0xF; ++i)
                asm.emit(inc);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void incHlRWorks() {
        Assembler asm = new Assembler();
        asm.emit(LD_HL_N16, 0xC000);
        for (int i = 0; i <= 0xF; ++i)
            asm.emit(INC_HLR);
        asm.emit(LD_A_HLR);
        assertEquals(CpuState.of(0x0014, 0x0000, 0x102000000000C000L), stateAfter(asm, new WorkRam()));
    }

    @Test
    void addHlR16Works() {
        List<Opcode> adds = List.of(ADD_HL_BC, ADD_HL_DE, ADD_HL_HL, ADD_HL_SP);
        Iterator<Opcode> load = List.of(LD_BC_N16, LD_DE_N16, LD_HL_N16, LD_SP_N16).iterator();
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0006, 0x0000, 0x00A068000000D000L),
                CpuState.of(0x0006, 0x0000, 0x00A000006800D000L),
                CpuState.of(0x0006, 0x0000, 0x009000000000A000L),
                CpuState.of(0x0006, 0x6800, 0x00A000000000D000L)).iterator();

        for (Opcode add: adds) {
            Assembler asm = new Assembler();
            asm.emit(ADD_A_A); // Set Z (to check its preservation)
            asm.emit(load.next(), 0x6800);
            asm.emit(add);
            asm.emit(add);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void incR16Works() {
        Iterator<Opcode> incs = List.of(INC_BC, INC_DE, INC_HL, INC_SP).iterator();
        Iterator<Opcode> load = List.of(LD_BC_N16, LD_DE_N16, LD_HL_N16, LD_SP_N16).iterator();
        while (incs.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(load.next(), 0xFFFF);
            asm.emit(incs.next());
            assertEquals(CpuState.of(0x0004, 0x0000, 0x0000000000000000L), stateAfter(asm));
        }
    }

    @Test
    void addSpWorks() {
        List<Integer> args = List.of(0, 1, -1, 127, -127, -128);
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x007F, 0x0000000000000000L),
                CpuState.of(0x0005, 0x0080, 0x0020000000000000L),
                CpuState.of(0x0005, 0x007E, 0x0030000000000000L),
                CpuState.of(0x0005, 0x00FE, 0x0020000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0030000000000000L),
                CpuState.of(0x0005, 0xFFFF, 0x0000000000000000L)).iterator();
        for (int a: args) {
            Assembler asm = new Assembler();
            asm.emit(LD_SP_N16, 0x7F);
            asm.emit(ADD_SP_N, a & 0xFF);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void ldHlSpWorks() {
        List<Integer> args = List.of(0, 1, -1, 127, -127, -128);
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x007F, 0x000000000000007FL),
                CpuState.of(0x0005, 0x007F, 0x0020000000000080L),
                CpuState.of(0x0005, 0x007F, 0x003000000000007EL),
                CpuState.of(0x0005, 0x007F, 0x00200000000000FEL),
                CpuState.of(0x0005, 0x007F, 0x0030000000000000L),
                CpuState.of(0x0005, 0x007F, 0x000000000000FFFFL)).iterator();
        for (int a: args) {
            Assembler asm = new Assembler();
            asm.emit(LD_SP_N16, 0x7F);
            asm.emit(LD_HL_SP_N8, a & 0xFF);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    // Subtractions and comparisons
    @Test
    void subAN8Works() {
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 },
            { 0xFF, 0xFF },
            { 0xF, 0xF1 },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF70000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF40000000000000L),
                CpuState.of(0x0004, 0x0000, 0xF040000000000000L),
                CpuState.of(0x0004, 0x0000, 0x1040000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0270000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00C0000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(SUB_A_N8, lr[0]);
            asm.emit(SUB_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void subAR8Works() {
        List<Opcode> subs =
                List.of(SUB_A_A, SUB_A_B, SUB_A_C, SUB_A_D, SUB_A_E, SUB_A_H, SUB_A_L);
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 }
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70010000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000100000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000001000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000000010000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000000000100L),
                CpuState.of(0x0005, 0x0000, 0xFF70000000000001L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xF2700F0000000000L),
                CpuState.of(0x0005, 0x0000, 0xF270000F00000000L),
                CpuState.of(0x0005, 0x0000, 0xF27000000F000000L),
                CpuState.of(0x0005, 0x0000, 0xF2700000000F0000L),
                CpuState.of(0x0005, 0x0000, 0xF270000000000F00L),
                CpuState.of(0x0005, 0x0000, 0xF27000000000000FL),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40010000000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000100000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000001000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000000010000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000000000100L),
                CpuState.of(0x0005, 0x0000, 0xEE40000000000001L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0800000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0008000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000080000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000800000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000008000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000080L)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> sub = subs.iterator();
            for (int r = 0; r < subs.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                asm.emit(sub.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void subAHLRWorks() {
        Assembler asm = new Assembler();
        asm.emit(SUB_A_HLR);
        asm.emit(SUB_A_HLR);
        assertEquals(CpuState.of(0x0002, 0x0000, 0xD450000000000000L), stateAfter(asm));
    }

    @Test
    void sbcAN8Works() {
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 },
            { 0xFF, 0xFF },
            { 0xF, 0xF1 },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF70000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFE40000000000000L),
                CpuState.of(0x0004, 0x0000, 0xEF60000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0F60000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF70000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0170000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF70000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(SBC_A_N8, lr[0]);
            asm.emit(SBC_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void sbcAR8Works() {
        List<Opcode> subs =
                List.of(SBC_A_A, SBC_A_B, SBC_A_C, SBC_A_D, SBC_A_E, SBC_A_H, SBC_A_L);
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 }
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70010000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000100000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000001000000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000000010000L),
                CpuState.of(0x0005, 0x0000, 0xFF70000000000100L),
                CpuState.of(0x0005, 0x0000, 0xFF70000000000001L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xF2700F0000000000L),
                CpuState.of(0x0005, 0x0000, 0xF270000F00000000L),
                CpuState.of(0x0005, 0x0000, 0xF27000000F000000L),
                CpuState.of(0x0005, 0x0000, 0xF2700000000F0000L),
                CpuState.of(0x0005, 0x0000, 0xF270000000000F00L),
                CpuState.of(0x0005, 0x0000, 0xF27000000000000FL),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40010000000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000100000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000001000000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000000010000L),
                CpuState.of(0x0005, 0x0000, 0xEE40000000000100L),
                CpuState.of(0x0005, 0x0000, 0xEE40000000000001L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0800000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0008000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000080000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000800000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000008000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000080L)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> sub = subs.iterator();
            for (int r = 0; r < subs.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                asm.emit(sub.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void sbcAHLRWorks() {
        Assembler asm = new Assembler();
        asm.emit(SBC_A_HLR);
        asm.emit(SBC_A_HLR);
        assertEquals(CpuState.of(0x0002, 0x0000, 0xC370000000000000L), stateAfter(asm));
    }

    @Test
    void decR8Works() {
        List<Opcode> decs = List.of(DEC_A, DEC_B, DEC_C, DEC_D, DEC_E, DEC_H, DEC_L);
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0001, 0x0000, 0xFF60000000000000L),
                CpuState.of(0x0001, 0x0000, 0x0060FF0000000000L),
                CpuState.of(0x0001, 0x0000, 0x006000FF00000000L),
                CpuState.of(0x0001, 0x0000, 0x00600000FF000000L),
                CpuState.of(0x0001, 0x0000, 0x0060000000FF0000L),
                CpuState.of(0x0001, 0x0000, 0x006000000000FF00L),
                CpuState.of(0x0001, 0x0000, 0x00600000000000FFL)).iterator();
        for (Opcode dec: decs) {
            Assembler asm = new Assembler();
            asm.emit(dec);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void decHlRWorks() {
        Assembler asm = new Assembler();
        asm.emit(LD_HL_N16, 0xC000);
        asm.emit(DEC_HLR);
        asm.emit(LD_A_HLR);
        assertEquals(CpuState.of(0x0005, 0x0000, 0xFF6000000000C000L), stateAfter(asm, new WorkRam()));
    }


    // Subtractions and comparisons
    @Test
    void cpAN8Works() {
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 },
            { 0xFF, 0xFF },
            { 0xF, 0xF1 },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0070000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0170000000000000L),
                CpuState.of(0x0004, 0x0000, 0xEF40000000000000L),
                CpuState.of(0x0004, 0x0000, 0x80C0000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFFC0000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0F50000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, lr[0]);
            asm.emit(CP_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void cpAR8Works() {
        List<Opcode> cps =
                List.of(CP_A_A, CP_A_B, CP_A_C, CP_A_D, CP_A_E, CP_A_H, CP_A_L);
        int[][] operands = new int[][] {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 0xF },
            { 0xEF, 1 },
            { 0x80, 0x80 }
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x01C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0070010000000000L),
                CpuState.of(0x0005, 0x0000, 0x0070000100000000L),
                CpuState.of(0x0005, 0x0000, 0x0070000001000000L),
                CpuState.of(0x0005, 0x0000, 0x0070000000010000L),
                CpuState.of(0x0005, 0x0000, 0x0070000000000100L),
                CpuState.of(0x0005, 0x0000, 0x0070000000000001L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0140000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0FC0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x01700F0000000000L),
                CpuState.of(0x0005, 0x0000, 0x0170000F00000000L),
                CpuState.of(0x0005, 0x0000, 0x017000000F000000L),
                CpuState.of(0x0005, 0x0000, 0x01700000000F0000L),
                CpuState.of(0x0005, 0x0000, 0x0170000000000F00L),
                CpuState.of(0x0005, 0x0000, 0x017000000000000FL),
                CpuState.of(0x0005, 0x0000, 0x01C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xEF40010000000000L),
                CpuState.of(0x0005, 0x0000, 0xEF40000100000000L),
                CpuState.of(0x0005, 0x0000, 0xEF40000001000000L),
                CpuState.of(0x0005, 0x0000, 0xEF40000000010000L),
                CpuState.of(0x0005, 0x0000, 0xEF40000000000100L),
                CpuState.of(0x0005, 0x0000, 0xEF40000000000001L),
                CpuState.of(0x0005, 0x0000, 0x80C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x80C0800000000000L),
                CpuState.of(0x0005, 0x0000, 0x80C0008000000000L),
                CpuState.of(0x0005, 0x0000, 0x80C0000080000000L),
                CpuState.of(0x0005, 0x0000, 0x80C0000000800000L),
                CpuState.of(0x0005, 0x0000, 0x80C0000000008000L),
                CpuState.of(0x0005, 0x0000, 0x80C0000000000080L)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> cp = cps.iterator();
            for (int r = 0; r < cps.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                asm.emit(cp.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void cpAHLRWorks() {
        Assembler asm = new Assembler();
        asm.emit(CP_A_HLR);
        assertEquals(CpuState.of(0x0001, 0x0000, 0x0070000000000000L), stateAfter(asm));
    }

    @Test
    void decR16Works() {
        Iterator<Opcode> decs = List.of(DEC_BC, DEC_DE, DEC_HL, DEC_SP).iterator();
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0001, 0x0000, 0x0000FFFF00000000L),
                CpuState.of(0x0001, 0x0000, 0x00000000FFFF0000L),
                CpuState.of(0x0001, 0x0000, 0x000000000000FFFFL),
                CpuState.of(0x0001, 0xFFFF, 0x0000000000000000L)).iterator();
        while (decs.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(decs.next());
            assertEquals(exp.next(), stateAfter(asm));
        }
    }


    // Bitwise operators
    @Test
    void andAN8Works() {
        int[][] operands = new int[][] {
            { 0xA5, 0x5A },
            { 0xFF, 0x00 },
            { 0xFF, 0xA5 },
            { 0xBD, 0xB2 },
            { 0xDB, 0x2B },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0004, 0x0000, 0xA520000000000000L),
                CpuState.of(0x0004, 0x0000, 0xB020000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0B20000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, lr[0]);
            asm.emit(AND_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void andAR8Works() {
        List<Opcode> ands =
                List.of(AND_A_A, AND_A_B, AND_A_C, AND_A_D, AND_A_E, AND_A_H, AND_A_L);
        int[][] operands = new int[][] {
            { 0xA5, 0x5A },
            { 0xFF, 0x00 },
            { 0xFF, 0xA5 },
            { 0xBD, 0xB2 },
            { 0xDB, 0x2B },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x5A20000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A05A0000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A0005A00000000L),
                CpuState.of(0x0005, 0x0000, 0x00A000005A000000L),
                CpuState.of(0x0005, 0x0000, 0x00A00000005A0000L),
                CpuState.of(0x0005, 0x0000, 0x00A0000000005A00L),
                CpuState.of(0x0005, 0x0000, 0x00A000000000005AL),
                CpuState.of(0x0005, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0005, 0x0000, 0xA520000000000000L),
                CpuState.of(0x0005, 0x0000, 0xA520A50000000000L),
                CpuState.of(0x0005, 0x0000, 0xA52000A500000000L),
                CpuState.of(0x0005, 0x0000, 0xA5200000A5000000L),
                CpuState.of(0x0005, 0x0000, 0xA520000000A50000L),
                CpuState.of(0x0005, 0x0000, 0xA52000000000A500L),
                CpuState.of(0x0005, 0x0000, 0xA5200000000000A5L),
                CpuState.of(0x0005, 0x0000, 0xB220000000000000L),
                CpuState.of(0x0005, 0x0000, 0xB020B20000000000L),
                CpuState.of(0x0005, 0x0000, 0xB02000B200000000L),
                CpuState.of(0x0005, 0x0000, 0xB0200000B2000000L),
                CpuState.of(0x0005, 0x0000, 0xB020000000B20000L),
                CpuState.of(0x0005, 0x0000, 0xB02000000000B200L),
                CpuState.of(0x0005, 0x0000, 0xB0200000000000B2L),
                CpuState.of(0x0005, 0x0000, 0x2B20000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0B202B0000000000L),
                CpuState.of(0x0005, 0x0000, 0x0B20002B00000000L),
                CpuState.of(0x0005, 0x0000, 0x0B2000002B000000L),
                CpuState.of(0x0005, 0x0000, 0x0B200000002B0000L),
                CpuState.of(0x0005, 0x0000, 0x0B20000000002B00L),
                CpuState.of(0x0005, 0x0000, 0x0B2000000000002BL)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> and = ands.iterator();
            for (int r = 0; r < ands.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                asm.emit(and.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void andAHLRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0003, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0003, 0x0000, 0x3E20000000000000L)).iterator();
        for (int a: List.of(0, 0xFF)) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, a);
            asm.emit(AND_A_HLR);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void orAN8Works() {
        int[][] operands = new int[][] {
            { 0x00, 0x00 },
            { 0xA5, 0x5A },
            { 0xFF, 0x00 },
            { 0xFF, 0xA5 },
            { 0xBD, 0xB2 },
            { 0xDB, 0x2B },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xBF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFB00000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, lr[0]);
            asm.emit(OR_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void orAR8Works() {
        List<Opcode> ors =
                List.of(OR_A_A, OR_A_B, OR_A_C, OR_A_D, OR_A_E, OR_A_H, OR_A_L);
        int[][] operands = new int[][] {
            { 0x00, 0x00 },
            { 0xA5, 0x5A },
            { 0xFF, 0x00 },
            { 0xFF, 0xA5 },
            { 0xBD, 0xB2 },
            { 0xDB, 0x2B },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x5A00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF005A0000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00005A00000000L),
                CpuState.of(0x0005, 0x0000, 0xFF0000005A000000L),
                CpuState.of(0x0005, 0x0000, 0xFF000000005A0000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000005A00L),
                CpuState.of(0x0005, 0x0000, 0xFF0000000000005AL),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xA500000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00A50000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF0000A500000000L),
                CpuState.of(0x0005, 0x0000, 0xFF000000A5000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000A50000L),
                CpuState.of(0x0005, 0x0000, 0xFF0000000000A500L),
                CpuState.of(0x0005, 0x0000, 0xFF000000000000A5L),
                CpuState.of(0x0005, 0x0000, 0xB200000000000000L),
                CpuState.of(0x0005, 0x0000, 0xBF00B20000000000L),
                CpuState.of(0x0005, 0x0000, 0xBF0000B200000000L),
                CpuState.of(0x0005, 0x0000, 0xBF000000B2000000L),
                CpuState.of(0x0005, 0x0000, 0xBF00000000B20000L),
                CpuState.of(0x0005, 0x0000, 0xBF0000000000B200L),
                CpuState.of(0x0005, 0x0000, 0xBF000000000000B2L),
                CpuState.of(0x0005, 0x0000, 0x2B00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFB002B0000000000L),
                CpuState.of(0x0005, 0x0000, 0xFB00002B00000000L),
                CpuState.of(0x0005, 0x0000, 0xFB0000002B000000L),
                CpuState.of(0x0005, 0x0000, 0xFB000000002B0000L),
                CpuState.of(0x0005, 0x0000, 0xFB00000000002B00L),
                CpuState.of(0x0005, 0x0000, 0xFB0000000000002BL)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> or = ors.iterator();
            for (int r = 0; r < ors.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                asm.emit(or.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void orAHLRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x2100000000000000L),
                CpuState.of(0x0004, 0x0000, 0x008000000000C000L)).iterator();
        for (int hl: List.of(0, 0xC000)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, hl);
            asm.emit(OR_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void xorAN8Works() {
        int[][] operands = new int[][] {
            { 0x00, 0x00 },
            { 0xA5, 0x5A },
            { 0xFF, 0x00 },
            { 0xFF, 0xA5 },
            { 0xBD, 0xBD },
            { 0xDB, 0x2B },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0x5A00000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0xF000000000000000L)).iterator();
        for (int[] lr: operands) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, lr[0]);
            asm.emit(XOR_A_N8, lr[1]);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void xorAR8Works() {
        List<Opcode> xors =
                List.of(XOR_A_A, XOR_A_B, XOR_A_C, XOR_A_D, XOR_A_E, XOR_A_H, XOR_A_L);
        int[][] operands = new int[][] {
            { 0x00, 0x00 },
            { 0xA5, 0x5A },
            { 0xFF, 0x00 },
            { 0xFF, 0xA5 },
            { 0xBD, 0xBD },
            { 0xDB, 0x2B },
        };
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF005A0000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00005A00000000L),
                CpuState.of(0x0005, 0x0000, 0xFF0000005A000000L),
                CpuState.of(0x0005, 0x0000, 0xFF000000005A0000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000005A00L),
                CpuState.of(0x0005, 0x0000, 0xFF0000000000005AL),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0xFF00000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x5A00A50000000000L),
                CpuState.of(0x0005, 0x0000, 0x5A0000A500000000L),
                CpuState.of(0x0005, 0x0000, 0x5A000000A5000000L),
                CpuState.of(0x0005, 0x0000, 0x5A00000000A50000L),
                CpuState.of(0x0005, 0x0000, 0x5A0000000000A500L),
                CpuState.of(0x0005, 0x0000, 0x5A000000000000A5L),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0080BD0000000000L),
                CpuState.of(0x0005, 0x0000, 0x008000BD00000000L),
                CpuState.of(0x0005, 0x0000, 0x00800000BD000000L),
                CpuState.of(0x0005, 0x0000, 0x0080000000BD0000L),
                CpuState.of(0x0005, 0x0000, 0x008000000000BD00L),
                CpuState.of(0x0005, 0x0000, 0x00800000000000BDL),
                CpuState.of(0x0005, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0005, 0x0000, 0xF0002B0000000000L),
                CpuState.of(0x0005, 0x0000, 0xF000002B00000000L),
                CpuState.of(0x0005, 0x0000, 0xF00000002B000000L),
                CpuState.of(0x0005, 0x0000, 0xF0000000002B0000L),
                CpuState.of(0x0005, 0x0000, 0xF000000000002B00L),
                CpuState.of(0x0005, 0x0000, 0xF00000000000002BL)).iterator();
        for (int[] lr: operands) {
            Iterator<Opcode> load = r8Loads().iterator();
            Iterator<Opcode> xor = xors.iterator();
            for (int r = 0; r < xors.size(); ++r) {
                Assembler asm = new Assembler();
                asm.emit(LD_A_N8, lr[0]);
                asm.emit(load.next(), lr[1]);
                asm.emit(xor.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void xorAHLRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0006, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0006, 0x0000, 0x3E0000000000C000L)).iterator();
        for (int hl: List.of(0, 0xC000)) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, LD_A_N8.encoding);
            asm.emit(LD_HL_N16, hl);
            asm.emit(XOR_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void cplWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0xFFE0000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0160000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFFF0000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0170000000000000L)).iterator();
        for (int a: List.of(0, 0x7F, 0x80, 0xFF)) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, a);
            asm.emit(ADD_A_A);  // Change Z and C flags, to check their preservation
            asm.emit(CPL);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    // Shifts
    @Test
    void slaR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x4A10000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00104A0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010004A00000000L),
                CpuState.of(0x0004, 0x0000, 0x001000004A000000L),
                CpuState.of(0x0004, 0x0000, 0x00100000004A0000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000004A00L),
                CpuState.of(0x0004, 0x0000, 0x001000000000004AL)).iterator();
        for (int a: List.of(0, 0x80, 0xA5)) {
            Iterator<Opcode> ops = List.of(SLA_A, SLA_B, SLA_C, SLA_D, SLA_E, SLA_H, SLA_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void slaHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x009000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x4A1000000000C000L)).iterator();
        for (int a: List.of(0, 0x80, 0xA5)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(SLA_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void sraR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0xC210000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010C20000000000L),
                CpuState.of(0x0004, 0x0000, 0x001000C200000000L),
                CpuState.of(0x0004, 0x0000, 0x00100000C2000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000C20000L),
                CpuState.of(0x0004, 0x0000, 0x001000000000C200L),
                CpuState.of(0x0004, 0x0000, 0x00100000000000C2L)).iterator();
        for (int a: List.of(0, 1, 0x85)) {
            Iterator<Opcode> ops = List.of(SRA_A, SRA_B, SRA_C, SRA_D, SRA_E, SRA_H, SRA_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void sraHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x009000000000C000L),
                CpuState.of(0x000A, 0x0000, 0xC21000000000C000L)).iterator();
        for (int a: List.of(0, 1, 0x85)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(SRA_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void srlR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x4210000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010420000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010004200000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000042000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000420000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000004200L),
                CpuState.of(0x0004, 0x0000, 0x0010000000000042L)).iterator();
        for (int a: List.of(0, 1, 0x85)) {
            Iterator<Opcode> ops = List.of(SRL_A, SRL_B, SRL_C, SRL_D, SRL_E, SRL_H, SRL_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void srlHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x009000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x421000000000C000L)).iterator();
        for (int a: List.of(0, 1, 0x85)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(SRL_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    // Rotations
    @Test
    void rlcaWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0003, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0003, 0x0000, 0x0110000000000000L),
                CpuState.of(0x0003, 0x0000, 0x4B10000000000000L)).iterator();
        for (int a: List.of(0, 0x80, 0xA5)) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, a);
            asm.emit(RLCA);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void rrcaWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0003, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0003, 0x0000, 0x8010000000000000L),
                CpuState.of(0x0003, 0x0000, 0xD210000000000000L)).iterator();
        for (int a: List.of(0, 1, 0xA5)) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, a);
            asm.emit(RRCA);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void rlaWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0004, 0x0000, 0x9500000000000000L),
                CpuState.of(0x0004, 0x0000, 0x6810000000000000L)).iterator();
        for (int a: List.of(0, 0x40, 0x80, 0xA5, 0x5A)) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, a);
            asm.emit(RLA);
            asm.emit(RLA);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }
    
    @Test
    void rraWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0000000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000000000L),
                CpuState.of(0x0004, 0x0000, 0x8000000000000000L),
                CpuState.of(0x0004, 0x0000, 0xA900000000000000L),
                CpuState.of(0x0004, 0x0000, 0x1610000000000000L)).iterator();
        for (int a: List.of(0, 2, 1, 0xA5, 0x5A)) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, a);
            asm.emit(RRA);
            asm.emit(RRA);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void rlcR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0110000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010010000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000100000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000001000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000010000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000000100L),
                CpuState.of(0x0004, 0x0000, 0x0010000000000001L),
                CpuState.of(0x0004, 0x0000, 0x4B10000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00104B0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010004B00000000L),
                CpuState.of(0x0004, 0x0000, 0x001000004B000000L),
                CpuState.of(0x0004, 0x0000, 0x00100000004B0000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000004B00L),
                CpuState.of(0x0004, 0x0000, 0x001000000000004BL)).iterator();
        for (int a: List.of(0, 0x80, 0xA5)) {
            Iterator<Opcode> ops = List.of(RLC_A, RLC_B, RLC_C, RLC_D, RLC_E, RLC_H, RLC_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }
    
    @Test
    void rlcHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x011000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x4B1000000000C000L)).iterator();
        for (int a: List.of(0, 0x80, 0xA5)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(RLC_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void rrcR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x8010000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010800000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010008000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000080000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000800000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000008000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000000080L),
                CpuState.of(0x0004, 0x0000, 0xD210000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010D20000000000L),
                CpuState.of(0x0004, 0x0000, 0x001000D200000000L),
                CpuState.of(0x0004, 0x0000, 0x00100000D2000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000D20000L),
                CpuState.of(0x0004, 0x0000, 0x001000000000D200L),
                CpuState.of(0x0004, 0x0000, 0x00100000000000D2L)).iterator();
        for (int a: List.of(0, 1, 0xA5)) {
            Iterator<Opcode> ops = List.of(RRC_A, RRC_B, RRC_C, RRC_D, RRC_E, RRC_H, RRC_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void rrcHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x801000000000C000L),
                CpuState.of(0x000A, 0x0000, 0xD21000000000C000L)).iterator();
        for (int a: List.of(0, 1, 0xA5)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(RRC_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void rlR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x8000000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000800000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000008000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000080000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000800000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000008000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000000080L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x4A10000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00104A0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010004A00000000L),
                CpuState.of(0x0004, 0x0000, 0x001000004A000000L),
                CpuState.of(0x0004, 0x0000, 0x00100000004A0000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000004A00L),
                CpuState.of(0x0004, 0x0000, 0x001000000000004AL),
                CpuState.of(0x0004, 0x0000, 0xB400000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000B40000000000L),
                CpuState.of(0x0004, 0x0000, 0x000000B400000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000B4000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000B40000L),
                CpuState.of(0x0004, 0x0000, 0x000000000000B400L),
                CpuState.of(0x0004, 0x0000, 0x00000000000000B4L)).iterator();
        for (int a: List.of(0, 0x40, 0x80, 0xA5, 0x5A)) {
            Iterator<Opcode> ops = List.of(RL_A, RL_B, RL_C, RL_D, RL_E, RL_H, RL_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void rlHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x800000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x009000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x4A1000000000C000L),
                CpuState.of(0x000A, 0x0000, 0xB40000000000C000L)).iterator();
        for (int a: List.of(0, 0x40, 0x80, 0xA5, 0x5A)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(RL_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void rrR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000010000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000100000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000001000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000010000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000000100L),
                CpuState.of(0x0004, 0x0000, 0x0000000000000001L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0004, 0x0000, 0x5210000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010520000000000L),
                CpuState.of(0x0004, 0x0000, 0x0010005200000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000052000000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000520000L),
                CpuState.of(0x0004, 0x0000, 0x0010000000005200L),
                CpuState.of(0x0004, 0x0000, 0x0010000000000052L),
                CpuState.of(0x0004, 0x0000, 0x2D00000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00002D0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000002D00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000002D000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000002D0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000002D00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000002DL)).iterator();
        for (int a: List.of(0, 2, 1, 0xA5, 0x5A)) {
            Iterator<Opcode> ops = List.of(RR_A, RR_B, RR_C, RR_D, RR_E, RR_H, RR_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void rrHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x010000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x009000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x521000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x2D0000000000C000L)).iterator();
        for (int a: List.of(0, 2, 1, 0xA5, 0x5A)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(RR_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void swapR8Works() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0080000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0F00000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00000F0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000F00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000000F000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000000F0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000000F00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000000FL),
                CpuState.of(0x0004, 0x0000, 0xF000000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000F00000000000L),
                CpuState.of(0x0004, 0x0000, 0x000000F000000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000F0000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000F00000L),
                CpuState.of(0x0004, 0x0000, 0x000000000000F000L),
                CpuState.of(0x0004, 0x0000, 0x00000000000000F0L),
                CpuState.of(0x0004, 0x0000, 0x5A00000000000000L),
                CpuState.of(0x0004, 0x0000, 0x00005A0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000005A00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000005A000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000005A0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000005A00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000005AL)).iterator();
        for (int a: List.of(0, 0xF0, 0x0F, 0xA5)) {
            Iterator<Opcode> ops = List.of(SWAP_A, SWAP_B, SWAP_C, SWAP_D, SWAP_E, SWAP_H, SWAP_L).iterator();
            Iterator<Opcode> loads = r8Loads().iterator();
            while (ops.hasNext()) {
                Assembler asm = new Assembler();
                asm.emit(loads.next(), a);
                asm.emit(ops.next());
                assertEquals(exp.next(), stateAfter(asm));
            }
        }
    }

    @Test
    void swapHlRWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x000A, 0x0000, 0x008000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x0F0000000000C000L),
                CpuState.of(0x000A, 0x0000, 0xF00000000000C000L),
                CpuState.of(0x000A, 0x0000, 0x5A0000000000C000L)).iterator();
        for (int a: List.of(0, 0xF0, 0x0F, 0xA5)) {
            Assembler asm = new Assembler();
            asm.emit(LD_HL_N16, 0xC000);
            asm.emit(LD_A_N8, a);
            asm.emit(LD_HLR_A);
            asm.emit(XOR_A_A);
            asm.emit(SWAP_HLR);
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, new WorkRam()));
        }
    }

    @Test
    void bitR8Works() {
        Iterator<Opcode> ops = List.of(
                BIT_0_A, BIT_1_A, BIT_2_A, BIT_3_A, BIT_4_A, BIT_5_A, BIT_6_A, BIT_7_A,
                BIT_0_B, BIT_1_B, BIT_2_B, BIT_3_B, BIT_4_B, BIT_5_B, BIT_6_B, BIT_7_B,
                BIT_0_C, BIT_1_C, BIT_2_C, BIT_3_C, BIT_4_C, BIT_5_C, BIT_6_C, BIT_7_C,
                BIT_0_D, BIT_1_D, BIT_2_D, BIT_3_D, BIT_4_D, BIT_5_D, BIT_6_D, BIT_7_D,
                BIT_0_E, BIT_1_E, BIT_2_E, BIT_3_E, BIT_4_E, BIT_5_E, BIT_6_E, BIT_7_E,
                BIT_0_H, BIT_1_H, BIT_2_H, BIT_3_H, BIT_4_H, BIT_5_H, BIT_6_H, BIT_7_H,
                BIT_0_L, BIT_1_L, BIT_2_L, BIT_3_L, BIT_4_L, BIT_5_L, BIT_6_L, BIT_7_L)
                .iterator();
        CpuState[] exp = new CpuState[] {
                CpuState.of(0x000A, 0x0000, 0x5520555555555555L),
                CpuState.of(0x000A, 0x0000, 0x55A0555555555555L),
        };
        int i = 0;
        while (ops.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, 0x55);
            asm.emit(LD_B_A);
            asm.emit(LD_C_A);
            asm.emit(LD_D_A);
            asm.emit(LD_E_A);
            asm.emit(LD_H_A);
            asm.emit(LD_L_A);
            asm.emit(ops.next());
            assertEquals(exp[i++ % 2], stateAfter(asm));
        }
    }
    
    @Test
    void bitHlRWorks() {
        Iterator<Opcode> ops = List.of(
                BIT_0_HLR, BIT_1_HLR, BIT_2_HLR, BIT_3_HLR, BIT_4_HLR, BIT_5_HLR, BIT_6_HLR, BIT_7_HLR)
                .iterator();
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0002, 0x0000, 0x0020000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0020000000000000L),
                CpuState.of(0x0002, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0020000000000000L),
                CpuState.of(0x0002, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0002, 0x0000, 0x00A0000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0020000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0020000000000000L))
                .iterator();
        while (ops.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(ops.next());
            assertEquals(exp.next(), stateAfter(asm));
        }
    }
    
    @Test
    void setR8Works() {
        Iterator<Opcode> ops = List.of(
                SET_0_A, SET_1_A, SET_2_A, SET_3_A, SET_4_A, SET_5_A, SET_6_A, SET_7_A,
                SET_0_B, SET_1_B, SET_2_B, SET_3_B, SET_4_B, SET_5_B, SET_6_B, SET_7_B,
                SET_0_C, SET_1_C, SET_2_C, SET_3_C, SET_4_C, SET_5_C, SET_6_C, SET_7_C,
                SET_0_D, SET_1_D, SET_2_D, SET_3_D, SET_4_D, SET_5_D, SET_6_D, SET_7_D,
                SET_0_E, SET_1_E, SET_2_E, SET_3_E, SET_4_E, SET_5_E, SET_6_E, SET_7_E,
                SET_0_H, SET_1_H, SET_2_H, SET_3_H, SET_4_H, SET_5_H, SET_6_H, SET_7_H,
                SET_0_L, SET_1_L, SET_2_L, SET_3_L, SET_4_L, SET_5_L, SET_6_L, SET_7_L)
                .iterator();
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0002, 0x0000, 0x0100000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0200000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0400000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0800000000000000L),
                CpuState.of(0x0002, 0x0000, 0x1000000000000000L),
                CpuState.of(0x0002, 0x0000, 0x2000000000000000L),
                CpuState.of(0x0002, 0x0000, 0x4000000000000000L),
                CpuState.of(0x0002, 0x0000, 0x8000000000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000010000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000020000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000040000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000080000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000100000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000200000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000400000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000800000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000100000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000200000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000400000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000800000000L),
                CpuState.of(0x0002, 0x0000, 0x0000001000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000002000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000004000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000008000000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000001000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000002000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000004000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000008000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000010000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000020000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000040000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000080000000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000010000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000020000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000040000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000080000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000100000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000200000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000400000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000800000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000100L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000200L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000400L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000800L),
                CpuState.of(0x0002, 0x0000, 0x0000000000001000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000002000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000004000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000008000L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000001L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000002L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000004L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000008L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000010L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000020L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000040L),
                CpuState.of(0x0002, 0x0000, 0x0000000000000080L))
                .iterator();
        while (ops.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(ops.next());
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void setHlRWorks() {
        Iterator<Opcode> ops = List.of(
                SET_0_HLR, SET_1_HLR, SET_2_HLR, SET_3_HLR, SET_4_HLR, SET_5_HLR, SET_6_HLR, SET_7_HLR)
                .iterator();
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x010000000000C000L),
                CpuState.of(0x0005, 0x0000, 0x030000000000C000L),
                CpuState.of(0x0005, 0x0000, 0x070000000000C000L),
                CpuState.of(0x0005, 0x0000, 0x0F0000000000C000L),
                CpuState.of(0x0005, 0x0000, 0x1F0000000000C000L),
                CpuState.of(0x0005, 0x0000, 0x3F0000000000C000L),
                CpuState.of(0x0005, 0x0000, 0x7F0000000000C000L),
                CpuState.of(0x0005, 0x0000, 0xFF0000000000C000L))
                .iterator();
        Component wr = new WorkRam();
        while (ops.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(LD_H_N8, 0xC0);
            asm.emit(ops.next());
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, wr));
        }
    }

    @Test
    void resR8Works() {
        Iterator<Opcode> ops = List.of(
                RES_0_A, RES_1_A, RES_2_A, RES_3_A, RES_4_A, RES_5_A, RES_6_A, RES_7_A,
                RES_0_B, RES_1_B, RES_2_B, RES_3_B, RES_4_B, RES_5_B, RES_6_B, RES_7_B,
                RES_0_C, RES_1_C, RES_2_C, RES_3_C, RES_4_C, RES_5_C, RES_6_C, RES_7_C,
                RES_0_D, RES_1_D, RES_2_D, RES_3_D, RES_4_D, RES_5_D, RES_6_D, RES_7_D,
                RES_0_E, RES_1_E, RES_2_E, RES_3_E, RES_4_E, RES_5_E, RES_6_E, RES_7_E,
                RES_0_H, RES_1_H, RES_2_H, RES_3_H, RES_4_H, RES_5_H, RES_6_H, RES_7_H,
                RES_0_L, RES_1_L, RES_2_L, RES_3_L, RES_4_L, RES_5_L, RES_6_L, RES_7_L)
                .iterator();
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0004, 0x0000, 0xFE00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFD00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xFB00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xF700000000000000L),
                CpuState.of(0x0004, 0x0000, 0xEF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xDF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0xBF00000000000000L),
                CpuState.of(0x0004, 0x0000, 0x7F00000000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000FE0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000FD0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000FB0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000F70000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000EF0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000DF0000000000L),
                CpuState.of(0x0004, 0x0000, 0x0000BF0000000000L),
                CpuState.of(0x0004, 0x0000, 0x00007F0000000000L),
                CpuState.of(0x0004, 0x0000, 0x000000FE00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000FD00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000FB00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000F700000000L),
                CpuState.of(0x0004, 0x0000, 0x000000EF00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000DF00000000L),
                CpuState.of(0x0004, 0x0000, 0x000000BF00000000L),
                CpuState.of(0x0004, 0x0000, 0x0000007F00000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000FE000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000FD000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000FB000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000F7000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000EF000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000DF000000L),
                CpuState.of(0x0004, 0x0000, 0x00000000BF000000L),
                CpuState.of(0x0004, 0x0000, 0x000000007F000000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000FE0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000FD0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000FB0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000F70000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000EF0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000DF0000L),
                CpuState.of(0x0004, 0x0000, 0x0000000000BF0000L),
                CpuState.of(0x0004, 0x0000, 0x00000000007F0000L),
                CpuState.of(0x0004, 0x0000, 0x000000000000FE00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000FD00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000FB00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000F700L),
                CpuState.of(0x0004, 0x0000, 0x000000000000EF00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000DF00L),
                CpuState.of(0x0004, 0x0000, 0x000000000000BF00L),
                CpuState.of(0x0004, 0x0000, 0x0000000000007F00L),
                CpuState.of(0x0004, 0x0000, 0x00000000000000FEL),
                CpuState.of(0x0004, 0x0000, 0x00000000000000FDL),
                CpuState.of(0x0004, 0x0000, 0x00000000000000FBL),
                CpuState.of(0x0004, 0x0000, 0x00000000000000F7L),
                CpuState.of(0x0004, 0x0000, 0x00000000000000EFL),
                CpuState.of(0x0004, 0x0000, 0x00000000000000DFL),
                CpuState.of(0x0004, 0x0000, 0x00000000000000BFL),
                CpuState.of(0x0004, 0x0000, 0x000000000000007FL))
                .iterator();
        List<Opcode> loads = r8Loads();
        int i = 0;
        while (ops.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(loads.get(i++ >> 3), 0xFF);
            asm.emit(ops.next());
            assertEquals(exp.next(), stateAfter(asm));
        }
    }

    @Test
    void resHlRWorks() {
        Iterator<Opcode> ops = List.of(
                RES_0_HLR, RES_1_HLR, RES_2_HLR, RES_3_HLR, RES_4_HLR, RES_5_HLR, RES_6_HLR, RES_7_HLR)
                .iterator();
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0007, 0x0000, 0xFE0000000000C000L),
                CpuState.of(0x0007, 0x0000, 0xFD0000000000C000L),
                CpuState.of(0x0007, 0x0000, 0xFB0000000000C000L),
                CpuState.of(0x0007, 0x0000, 0xF70000000000C000L),
                CpuState.of(0x0007, 0x0000, 0xEF0000000000C000L),
                CpuState.of(0x0007, 0x0000, 0xDF0000000000C000L),
                CpuState.of(0x0007, 0x0000, 0xBF0000000000C000L),
                CpuState.of(0x0007, 0x0000, 0x7F0000000000C000L))
                .iterator();
        Component wr = new WorkRam();
        while (ops.hasNext()) {
            Assembler asm = new Assembler();
            asm.emit(LD_H_N8, 0xC0);
            asm.emit(LD_HLR_N8, 0xFF);
            asm.emit(ops.next());
            asm.emit(LD_A_HLR);
            assertEquals(exp.next(), stateAfter(asm, wr));
        }
    }

    @Test
    void daaWorks() {
        Iterator<CpuState> exp = List.of(
                CpuState.of(0x0005, 0x0000, 0x1000000000000000L),
                CpuState.of(0x0005, 0x0000, 0x1600000000000000L),
                CpuState.of(0x0005, 0x0000, 0x7610000000000000L),
                CpuState.of(0x0005, 0x0000, 0x6610000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0090000000000000L),
                CpuState.of(0x0005, 0x0000, 0x1040000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x00C0000000000000L),
                CpuState.of(0x0005, 0x0000, 0x1040000000000000L),
                CpuState.of(0x0005, 0x0000, 0x9950000000000000L),
                CpuState.of(0x0005, 0x0000, 0x0250000000000000L))
                .iterator();

        int[][] addOperands = new int[][] {
            { 0x10, 0x00 },
            { 0x08, 0x08 },
            { 0x88, 0x88 },
            { 0x88, 0x78 },
            { 0x50, 0x50 },
            { 0x99, 0x01 },
        };
        for (int[] ops: addOperands) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, ops[0]);
            asm.emit(Opcode.ADD_A_N8, ops[1]);
            asm.emit(Opcode.DAA);
            assertEquals(exp.next(), stateAfter(asm));
        }
        
        int[][] subOperands = new int[][] {
            { 0x10, 0x00 },
            { 0x08, 0x08 },
            { 0x88, 0x88 },
            { 0x88, 0x78 },
            { 0x50, 0x51 },
            { 0x01, 0x99 },
        };
        for (int[] ops: subOperands) {
            Assembler asm = new Assembler();
            asm.emit(LD_A_N8, ops[0]);
            asm.emit(Opcode.SUB_A_N8, ops[1]);
            asm.emit(Opcode.DAA);
            assertEquals(exp.next(), stateAfter(asm));
        }
    }
    
    @Test
    void scfWorks() {
        Assembler asm = new Assembler();
        asm.emit(Opcode.XOR_A_A); // Set Z, to check preservation
        asm.emit(SCF);
        assertEquals(CpuState.of(0x0002, 0x0000, 0x0090000000000000L), stateAfter(asm));
    }
    
    @Test
    void ccfWorks() {
        Assembler asm = new Assembler();
        asm.emit(Opcode.XOR_A_A); // Set Z, to check preservation
        asm.emit(CCF);
        assertEquals(CpuState.of(0x0002, 0x0000, 0x0090000000000000L), stateAfter(asm));

        Assembler asm2 = new Assembler();
        asm2.emit(SUB_A_N8, 1);
        asm2.emit(CCF);
        assertEquals(CpuState.of(0x0003, 0x0000, 0xFF00000000000000L), stateAfter(asm2));
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
        Program prog = asm.program();
        Component rom = prog.rom();
        Cpu cpu = new Cpu();
        Bus bus = new Bus();
        rom.attachTo(bus);
        cpu.attachTo(bus);
        for (Component c2: components)
            c2.attachTo(bus);
        for (int i = 0; i < prog.cycles(); ++i)
            cpu.cycle(i);
        return CpuState.ofArray(cpu._testGetPcSpAFBCDEHL());
    }

    private static List<Opcode> r8Loads() {
        return List.of(LD_A_N8, LD_B_N8, LD_C_N8, LD_D_N8, LD_E_N8, LD_H_N8, LD_L_N8);
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
