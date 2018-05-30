package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NosCpuTest5 {

    private Bus connect(Cpu cpu, Ram ram) {
        RamController rc = new RamController(ram, 0);
        Bus b = new Bus();
        cpu.attachTo(b);
        rc.attachTo(b);
        return b;
    }

    private void cycleCpu(Cpu cpu, long cycles) {
        for (long c = 0; c < cycles; ++c)
            cpu.cycle(c);
    }

    // code tiré des tests du prof, permet de remplir une nouvelle ram d'un tableau (donné en arg)
    // puis associe un ramcontroller à cette ram à partir de startAddress
    private Component ramAt(int startAddress, byte[] initialContents) {
        Ram r = new Ram(0xFFFF);
        for (int i = 0; i < initialContents.length; ++i)
            r.write(i, Byte.toUnsignedInt(initialContents[i]));
        return new RamController(r, startAddress);
    }

    // exemple de test de nizou
    @Test
    void SCF_WorksOnKnowValues(){
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.SCF.encoding);
        cycleCpu(c, Opcode.SCF.cycles);
        assertArrayEquals(new int[] { 1,0,0,16,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
    }// pc (0), sp (1), a (2), f (3), b (4), c (5), d (6), e (7), h (8), l (9)


    // Teste RET, CALL, HALT ?? (si j'ai bien compris...)
    @Test void fibonacci() throws IOException {
        Cpu c = new Cpu();
        Bus b = new Bus();
        //Ram r = new Ram(28);    // taille du tableau donné = 4*7
        // tableau d'instruction à exécuter
        byte[] ramData = new byte[] {
                (byte)0x31, (byte)0xFF, (byte)0xFF, (byte)0x3E,
                (byte)0x0B, (byte)0xCD, (byte)0x0A, (byte)0x00,
                (byte)0x76, (byte)0x00, (byte)0xFE, (byte)0x02,
                (byte)0xD8, (byte)0xC5, (byte)0x3D, (byte)0x47,
                (byte)0xCD, (byte)0x0A, (byte)0x00, (byte)0x4F,
                (byte)0x78, (byte)0x3D, (byte)0xCD, (byte)0x0A,
                (byte)0x00, (byte)0x81, (byte)0xC1, (byte)0xC9,
        };
        Component ramController = ramAt(0, ramData);
        c.attachTo(b);
        ramController.attachTo(b);
        for (int i = 0; i < ramData.length; ++i) {
            b.write(i, Byte.toUnsignedInt(ramData[i]));
        }
        //c.cycle(60000);
        long l = 0;
        while (c._testGetPcSpAFBCDEHL()[0] != 8) {
            c.cycle(l);
            /*assert(0 <= c._testGetPcSpAFBCDEHL()[0] &&
                    c._testGetPcSpAFBCDEHL()[0] < ramData[(int)l]);
            System.out.println(Integer.toHexString(ramData[(int)l]));*/
            l += 1;
        }
        /*long l = 0;
        do {
            c.cycle(l);
            ++l;
        } while (c._testGetPcSpAFBCDEHL()[0] != 8);*/
        assertEquals(89, c._testGetPcSpAFBCDEHL()[2]);

    }


    @Test void CALL_N16_Works() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);    // peut etre changer par une autre instruction moins triviale
        b.write(1, Opcode.NOP.encoding);

        int left = Preconditions.checkBits8(b.read(1));
        int right = Preconditions.checkBits8(b.read(0));
        int PC = Bits.make16(left, right);  //PC = nn (read16afterOp)

        b.write(3, Opcode.CALL_N16.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.CALL_N16.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    }// pc (0), sp (1), a (2), f (3), b (4), c (5), d (6), e (7), h (8), l (9)

    @Test void CALL_C_N16_Works() { // call only if C is true in F
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);    // peut etre changer par une autre instruction moins triviale
        b.write(1, Opcode.NOP.encoding);

        int PC = 2 * Opcode.NOP.totalBytes + Opcode.CALL_C_N16.totalBytes;

        if (extractCondition(Opcode.CALL_C_N16)) {
            int left = Preconditions.checkBits8(b.read(1));
            int right = Preconditions.checkBits8(b.read(0));
            PC = Bits.make16(left, right);  //PC = nn (read16afterOp)
        }

        b.write(2, Opcode.CALL_C_N16.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.CALL_C_N16.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    } // pc (0), sp (1), a (2), f (3), b (4), c (5), d (6), e (7), h (8), l (9)

    //extractContidion copiée de Cpu pour obtenir cc : meilleure facon de faire ?
    private boolean extractCondition(Opcode opcode) {
        switch (Bits.extract(opcode.encoding, 3, 2)) {
        case 0b00:
            return true; //Z = 0
        case 0b01:
            return false; // Z = 1
        case 0b10:
            return true; // C = 0
        case 0b11:
            return false; // C = 1
        default:
            return false;
        }
    }

    @Test void CALL_NC_N16_Works() { // call only if C is false in F
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);    // peut etre changer par une autre instruction moins triviale
        b.write(1, Opcode.NOP.encoding);

        int PC = 2 * Opcode.NOP.totalBytes + Opcode.CALL_NC_N16.totalBytes;

        if (extractCondition(Opcode.CALL_NC_N16)) {
            int left = Preconditions.checkBits8(b.read(1));
            int right = Preconditions.checkBits8(b.read(0));
            PC = Bits.make16(left, right);  //PC = nn (read16afterOp)
        }

        b.write(2, Opcode.CALL_NC_N16.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.CALL_NC_N16.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    } // pc (0), sp (1), a (2), f (3), b (4), c (5), d (6), e (7), h (8), l (9)

    @Test void CALL_Z_N16_Works() { // call only if Z is true in F
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);    // peut etre changer par une autre instruction moins triviale
        b.write(1, Opcode.NOP.encoding);

        int PC = 2 * Opcode.NOP.totalBytes + Opcode.CALL_Z_N16.totalBytes;

        if (extractCondition(Opcode.CALL_Z_N16)) {
            int left = Preconditions.checkBits8(b.read(1));
            int right = Preconditions.checkBits8(b.read(0));
            PC = Bits.make16(left, right);  //PC = nn (read16afterOp)
        }

        b.write(2, Opcode.CALL_Z_N16.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.CALL_Z_N16.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    } // pc (0), sp (1), a (2), f (3), b (4), c (5), d (6), e (7), h (8), l (9)

    @Test void CALL_NZ_N16_Works() { // call only if Z is false in F
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);    // peut etre changer par une autre instruction moins triviale
        b.write(1, Opcode.NOP.encoding);

        int PC = 2 * Opcode.NOP.totalBytes + Opcode.CALL_NZ_N16.totalBytes;

        if (extractCondition(Opcode.CALL_NZ_N16)) {
            int left = Preconditions.checkBits8(b.read(1));
            int right = Preconditions.checkBits8(b.read(0));
            PC = Bits.make16(left, right);  //PC = nn (read16afterOp)
        }

        b.write(2, Opcode.CALL_NZ_N16.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.CALL_NZ_N16.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    } // pc (0), sp (1), a (2), f (3), b (4), c (5), d (6), e (7), h (8), l (9)

    @Test void RST_U3_Works() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);    // peut etre changer par une autre instruction moins triviale
        b.write(1, Opcode.NOP.encoding);

        int PC = 8 * Bits.extract(Opcode.RST_3.encoding, 3, 3);
        b.write(2, Opcode.RST_3.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.RST_3.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    }

    @Test void JP_N16_Works() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);
        b.write(1, Opcode.NOP.encoding);

        int left = Preconditions.checkBits8(b.read(1));
        int right = Preconditions.checkBits8(b.read(0));
        int PC = Bits.make16(left, right);  //PC = nn (read16afterOp)

        b.write(3, Opcode.JP_N16.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.JP_N16.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    }

    @Test void JP_CC_N16_Works() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);
        b.write(1, Opcode.NOP.encoding);

        int PC = 2 * Opcode.NOP.totalBytes + Opcode.JP_C_N16.totalBytes;

        if (extractCondition(Opcode.JP_C_N16)) {
            int left = Preconditions.checkBits8(b.read(1));
            int right = Preconditions.checkBits8(b.read(0));
            PC = Bits.make16(left, right);  //PC = nn (read16afterOp)
        }

        b.write(2, Opcode.JP_C_N16.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.JP_C_N16.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    }

    @Test void JP_HL_Works() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);

        b.write(0, Opcode.NOP.encoding);
        b.write(1, Opcode.NOP.encoding);
        int PC = 2 * Opcode.NOP.totalBytes + Opcode.JP_HL.totalBytes;

        b.write(3, Opcode.JP_HL.encoding);
        cycleCpu(c, 2 * Opcode.NOP.cycles + Opcode.JP_HL.cycles);
        assertEquals(PC, c._testGetPcSpAFBCDEHL()[0]);
    }

    @Test void reallyCycleWorks() {
        Cpu c = new Cpu();
        Ram r = new Ram(0xFFFF);
        Bus b = connect(c, r);

    }
}
