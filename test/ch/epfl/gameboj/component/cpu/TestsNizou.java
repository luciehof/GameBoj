package ch.epfl.gameboj.component.cpu;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.RandomGenerator;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Alu.RotDir;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

    public class TestsNizou {
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

        @Test
        void nopDoesNothing() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.NOP.encoding);
            cycleCpu(c, Opcode.NOP.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_R16SP_N16WorksB() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF - 1 );
            Bus bus = connect(c, r);


            bus.write(0, Opcode.LD_SP_N16.encoding);
            bus.write(1, 42);
            bus.write(2, 0);
            bus.write(3, Opcode.LD_DE_N16.encoding);
            bus.write(4, 37);
            bus.write(5, 13);

            cycleCpu(c,6);

            assertArrayEquals(new int[] {6,42,0,0,0,0,13,37,0,0},c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_R8_HLRWorksB() {
            Cpu c= new Cpu();
            Ram r= new Ram(0xFFFF-1);
            Bus bus= connect(c,r);


            bus.write(0, Opcode.LD_H_N8.encoding);
            bus.write(1, 0);
            bus.write(2,Opcode.LD_L_N8.encoding);
            bus.write(3, 20);
            bus.write(4, Opcode.LD_D_HLR.encoding);
            bus.write(20,2);
            cycleCpu(c,6);

            assertArrayEquals(new int[] {5,0,0,0,0,0,2,0,0,20},c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_A_HLRUWorksB() {
            Cpu c= new Cpu();
            Ram r= new Ram(0xFFFF-1);
            Bus bus= connect(c,r);

            bus.write(0, Opcode.LD_H_N8.encoding);
            bus.write(1,0);
            bus.write(2,Opcode.LD_L_N8.encoding);
            bus.write(3,11);
            bus.write(4, Opcode.LD_D_HLR.encoding);
            bus.write(11, 3);
            cycleCpu(c,6);

            assertArrayEquals(new int [] {5,0,0,0,0,0,3,0,0,11},c._testGetPcSpAFBCDEHL());
        }




        @Test
        void LD_A_N8RWokrsB() {
            Cpu c= new Cpu();
            Ram r= new Ram(0xFFFF-1);
            Bus bus= connect(c,r);

            bus.write(11+0xFF00,42);
            bus.write(0, Opcode.LD_A_N8R.encoding);
            bus.write(1, 11);
            cycleCpu(c,2);

            assertArrayEquals(new int[] {2,0,42,0,0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_A_CRWorksB() {
            Cpu c= new Cpu();
            Ram r= new Ram(0xFFFF-1);
            Bus bus= connect(c,r);


            bus.write(11+0xFF00,42);
            bus.write(0, Opcode.LD_C_N8.encoding);
            bus.write(1, 11);
            bus.write(2, Opcode.LD_A_CR.encoding);
            cycleCpu(c,4);



            assertArrayEquals(new int[] {3,0,42,0,0,11,0,0,0,0},c._testGetPcSpAFBCDEHL());
        }


        @Test
        void LD_A_N16RWorksB() {
            Cpu c=new Cpu();
            Ram r= new Ram(0xFFFF-1);
            Bus bus=connect(c,r);

            bus.write(0b1111111111110111,33);
            bus.write(0, Opcode.LD_A_N16R.encoding);
            bus.write(1, 0b11110111);
            bus.write(2,0b11111111);
            cycleCpu(c,2);

            assertArrayEquals(new int[] {3,0,33,0,0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_A_BCRWorksB() {
            Cpu c= new Cpu();
            Ram r= new Ram(0xFFFF-1);
            Bus bus=connect(c,r);

            bus.write(0,Opcode.LD_B_N8.encoding);
            bus.write(1, 0b00000001);
            bus.write(2, Opcode.LD_C_N8.encoding);
            bus.write(3, 0b00000001);
            bus.write(4, Opcode.LD_A_BCR.encoding);
            bus.write(0b0000000100000001, 33);

            cycleCpu(c,6);

            assertArrayEquals(new int[] {5,0,33,0,0b1,0b1,0,0,0,0},c._testGetPcSpAFBCDEHL());


        }



        @Test
        void LD_A_DERWorksB() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF - 1 );
            Bus bus = connect(c, r);

            bus.write(0,Opcode.LD_D_N8.encoding);
            bus.write(1, 0b00000001);
            bus.write(2, Opcode.LD_E_N8.encoding);
            bus.write(3, 0b00000001);
            bus.write(4, Opcode.LD_A_DER.encoding);
            bus.write(0b0000000100000001, 33);

            cycleCpu(c,6);

            assertArrayEquals(new int[] {5,0,33,0,0,0,0b1,0b1,0,0},c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_R8_N8WorksB() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF - 1 );
            Bus bus = connect(c, r);


            bus.write(0,Opcode.LD_A_N8.encoding);
            bus.write(1,1);
            bus.write(2,Opcode.LD_B_N8.encoding);
            bus.write(3,2);
            bus.write(4,Opcode.LD_C_N8.encoding);
            bus.write(5,3);
            bus.write(6,Opcode.LD_D_N8.encoding);
            bus.write(7,4);
            bus.write(8,Opcode.LD_E_N8.encoding);
            bus.write(9,5);
            bus.write(10,Opcode.LD_H_N8.encoding);
            bus.write(11,6);
            bus.write(12,Opcode.LD_L_N8.encoding);
            bus.write(13,7);

            cycleCpu(c,14);

            assertArrayEquals(new int[] {14,0,1,0,2,3,4,5,6,7},c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_POP_R16Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF - 1 );
            Bus bus = connect(c, r);

            bus.write(0, Opcode.LD_SP_N16.encoding);
            bus.write(1, 0b100);
            bus.write(2, 0b0);
            bus.write(3, Opcode.POP_BC.encoding);
            bus.write(4, 42);
            cycleCpu(c,5);


            assertArrayEquals(new int[] {4,0b110,0,0,0,42,0,0,0,0},c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_N8R_AWorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xffff - 1);
            Bus b = connect(c, r);
            b.write(0,  Opcode.LD_A_N8R.encoding);
            b.write(1,  15);
            b.write(2,  Opcode.LD_N8R_A.encoding);
            b.write(3,  0x0f -1);
            b.write(0xff0f, 5);
            cycleCpu(c, 4);
            assertArrayEquals(new int [] {4, 0, 5, 0, 0, 0, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
            assertEquals(5, b.read(0xff0f), 0.1);
        }

        @Test
        void LD_HL_R8WorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF - 1 );
            Bus b = connect(c, r);
            b.write(0,  Opcode.LD_A_N8.encoding);
            b.write(1, 45);
            b.write(2, Opcode.LD_H_N8.encoding);
            b.write(3, 0);
            b.write(4, Opcode.LD_L_N8.encoding);
            b.write(5, 28);
            b.write(6, Opcode.LD_HLR_A.encoding);
            cycleCpu(c, 9);
            assertArrayEquals(new int [] {8, 0, 45, 0, 0, 0, 0, 0, 0, 28}, c._testGetPcSpAFBCDEHL());
            assertEquals(45, b.read(28), 0.1);
        }
        @Test
        void LD_HLRU_AWorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xffff -1);
            Bus b = connect(c, r);
            b.write(0,  Opcode.LD_A_N8.encoding);
            b.write(1,  55);
            b.write(2,  Opcode.LD_L_N8.encoding);
            b.write(3, 120);
            b.write(4, Opcode.LD_HLRI_A.encoding); // incremente
            cycleCpu(c, 6);
            assertArrayEquals(new int [] {5, 0, 55, 0, 0, 0, 0, 0, 0, 121}, c._testGetPcSpAFBCDEHL());
            assertEquals(55, b.read(120), 0.1);
        }

        @Test
        void LD_HLRU_AWorks2E() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xffff -1);
            Bus b = connect(c, r);
            b.write(0,  Opcode.LD_A_N8.encoding);
            b.write(1,  55);
            b.write(2,  Opcode.LD_L_N8.encoding);
            b.write(3, 120);
            b.write(4, Opcode.LD_HLRD_A.encoding); // decremente
            cycleCpu(c, 6);
            assertArrayEquals(new int [] {5, 0, 55, 0, 0, 0, 0, 0, 0, 119}, c._testGetPcSpAFBCDEHL());
            assertEquals(55, b.read(120), 0.1);
        }
        @Test
        void LD_DER_AWorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xffff - 1);
            Bus b = connect(c, r);
            b.write(0,  Opcode.LD_A_N8.encoding);
            b.write(1,  29);
            b.write(2, Opcode.LD_E_N8.encoding);
            b.write(3,  43);
            b.write(43,  33);
            b.write(4,  Opcode.LD_DER_A.encoding);
            cycleCpu(c, 7);
            assertArrayEquals(new int [] {6, 0, 29, 0, 0, 0, 0, 43, 0, 0}, c._testGetPcSpAFBCDEHL());
            assertEquals(29, b.read(43), 0.1);
        }

        @Test
        void LD_BCR_AWorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xffff - 1);
            Bus b = connect(c, r);
            b.write(0,  Opcode.LD_A_N8.encoding);
            b.write(1,  29);
            b.write(2, Opcode.LD_C_N8.encoding);
            b.write(3,  43);
            b.write(43,  33);
            b.write(4,  Opcode.LD_BCR_A.encoding);
            cycleCpu(c, 7);
            assertArrayEquals(new int [] {6, 0, 29, 0, 0, 43, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
            assertEquals(29, b.read(43), 0.1);
        }

        @Test
        public void nopDoesNothing2() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_B_B.encoding);
            b.write(1, Opcode.LD_C_C.encoding);
            b.write(2, Opcode.LD_D_D.encoding);
            b.write(3, Opcode.LD_E_E.encoding);
            b.write(4, Opcode.LD_H_H.encoding);
            b.write(5, Opcode.LD_L_L.encoding);
            b.write(6, Opcode.LD_A_A.encoding);
            cycleCpu(c, 7 * Opcode.LD_A_A.cycles);
            assertArrayEquals(new int[] {7,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldR8HlrWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_B_HLR.encoding);
            cycleCpu(c, Opcode.LD_B_HLR.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0X46,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldAHlIRWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_HLRI.encoding);
            cycleCpu(c, Opcode.LD_A_HLRI.cycles);
            assertArrayEquals(new int[] {1,0,0x2A,0,0,0,0,0,0,1}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldAHlDRWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_HLRD.encoding);
            cycleCpu(c, Opcode.LD_A_HLRD.cycles);
            assertArrayEquals(new int[] {1,0,0x3A,0,0,0,0,0,0xFF,0xFF}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldAN8RWorksOutsideRange() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_N8R.encoding);
            cycleCpu(c, Opcode.LD_A_N8R.cycles);
            assertArrayEquals(new int[] {2,0,255,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldAN8RWorksInRange() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFF01);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_N8R.encoding);
            cycleCpu(c, Opcode.LD_A_N8R.cycles);
            assertArrayEquals(new int[] {2,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldACRWorksOutsideRange() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_CR.encoding);
            cycleCpu(c, Opcode.LD_A_CR.cycles);
            assertArrayEquals(new int[] {1,0,255,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldACRWorksInRange() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFF01);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_CR.encoding);
            cycleCpu(c, Opcode.LD_A_CR.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldAN16RWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_N16R.encoding);
            cycleCpu(c, Opcode.LD_A_N16R.cycles);
            assertArrayEquals(new int[] {3,0,0xFA,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldABcRWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_BCR.encoding);
            cycleCpu(c, Opcode.LD_A_BCR.cycles);
            assertArrayEquals(new int[] {1,0,0xA,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldADeRWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_DER.encoding);
            cycleCpu(c, Opcode.LD_A_DER.cycles);
            assertArrayEquals(new int[] {1,0,0x1A,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldR8N8Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_B_N8.encoding);
            b.write(1, 0x42);
            cycleCpu(c, Opcode.LD_B_N8.cycles);
            assertArrayEquals(new int[] {2,0,0,0,0x42,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldR16N16Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_SP_N16.encoding);
            b.write(1, 0x42);
            b.write(2, 0x78);
            cycleCpu(c, Opcode.LD_SP_N16.cycles);
            assertArrayEquals(new int[] {3,0x7842,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void popR16Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.POP_BC.encoding);
            cycleCpu(c, Opcode.POP_BC.cycles);
            assertArrayEquals(new int[] {1,2,0,0,0,0xC1,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldR8R8Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_B_C.encoding);
            cycleCpu(c, Opcode.LD_B_C.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void ldSpHlWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_SP_HL.encoding);
            cycleCpu(c, Opcode.LD_SP_HL.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void multipleInstructions() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xF000);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_HL_N16.encoding);
            b.write(1, 0x34);
            b.write(2, 0x12);
            b.write(3, Opcode.LD_C_HLR.encoding);
            b.write(0x1234, 0x42);
            cycleCpu(c, Opcode.LD_HL_N16.cycles + Opcode.LD_C_HLR.cycles);
            assertArrayEquals(new int[] {4,0,0,0,0,0x42,0,0,0x12,0x34}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void multipleInstructions2() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xF000);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_L_N8.encoding);
            b.write(1, 0x36);
            b.write(2, Opcode.LD_C_L.encoding);
            b.write(3, Opcode.LD_C_C.encoding);
            cycleCpu(c, Opcode.LD_L_N8.cycles + Opcode.LD_C_L.cycles+ Opcode.LD_C_C.cycles);
            assertArrayEquals(new int[] {4,0,0,0,0,0x36,0,0,0,0x36}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        public void multipleInstructions3() {
            Cpu c = new Cpu();
            Ram r = new Ram(0x2500);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_HL_N16.encoding);
            b.write(1, 0x01);
            b.write(2, 0x23);
            b.write(3, Opcode.LD_C_N8.encoding);
            b.write(4, 0x25);
            b.write(5, Opcode.LD_HLR_C.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] {6,0,0,0,0,0x25,0,0,0x23,0x1}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0x2301), 0x25);
        }

        ////////////////////////// MES TESTS //////////////////////////

        @Test
        public void ldHLRR8Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_HLR_B.encoding);
            cycleCpu(c, Opcode.LD_HLR_B.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0), 0);
        }
        @Test
        public void ldHlIRAWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_HLRI_A.encoding);
            cycleCpu(c, Opcode.LD_HLRI_A.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,1}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0), 0);
        }

        @Test
        public void ldHlDRAWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_HLRD_A.encoding);
            cycleCpu(c, Opcode.LD_HLRD_A.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0xFF,0xFF}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0), 0);
        }

        @Test
        public void ldN8RAWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFF0);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_N8R_A.encoding);
            cycleCpu(c, Opcode.LD_N8R_A.cycles);
            assertArrayEquals(new int[] {2,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(AddressMap.REGS_START + b.read(1)), 0);
        }

        @Test
        public void ldCRAWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFF0);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_CR_A.encoding);
            cycleCpu(c, Opcode.LD_CR_A.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(AddressMap.REGS_START), 0);
        }

        @Test
        public void ldN16RAWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_N16R_A.encoding);
            b.write(1, Opcode.CP_A_HLR.encoding);
            b.write(2, Opcode.XOR_A_HLR.encoding);
            cycleCpu(c, Opcode.LD_N16R_A.cycles);
            int value = Bits.make16(b.read(2), b.read(1));
            assertArrayEquals(new int[] {3,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(value), 0);
        }

        @Test
        public void ldBCRAWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_BCR_A.encoding);
            cycleCpu(c, Opcode.LD_BCR_A.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0), 0);
        }

        @Test
        public void ldDERAWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_DER_A.encoding);
            cycleCpu(c, Opcode.LD_DER_A.cycles);
            assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0), 0);
        }

        @Test
        public void ldHLRn8Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_HLR_N8.encoding);
            b.write(1, Opcode.LD_DER_A.encoding);
            cycleCpu(c, Opcode.LD_HLR_N8.cycles);
            assertArrayEquals(new int[] {2,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0), 0x12);
            //assertEquals(b.read(0), b.read(1));
        }

        @Test
        public void ldN16R_SPWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_N16R_SP.encoding);
            cycleCpu(c, Opcode.LD_N16R_SP.cycles);
            int value = Bits.make16(b.read(2), b.read(1));
            assertArrayEquals(new int[] {3,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(value), 0);
        }

        @Test
        public void ldPUSH_BCWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c, r);
            b.write(0, Opcode.PUSH_BC.encoding);
            cycleCpu(c, Opcode.PUSH_BC.cycles);
            assertArrayEquals(new int[] {1,0xFFFE,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0xFFFE), 0);
        }

        //Bizarre comme test ...

        @Test

        void loadFromAdress() {

            Cpu c = new Cpu();

            Ram r = new Ram(0xFFFF);

            Bus b = connect(c, r);

            b.write(1, 0xFA);

            b.write(2, 0x30);

            b.write(3, 0x15);

            b.write(0x1530, 0x30);





            c.cycle(0);

            assertArrayEquals(new int[] {1,0, 0x0,0x00,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

            c.cycle(1);

            c.cycle(2);

            c.cycle(3);

            c.cycle(4);

            assertArrayEquals(new int[] {4, 0, 0x30,0x0,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

            c.cycle(5);

            assertArrayEquals(new int[] {5, 0, 0x30,0x0,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

            c.cycle(6);

            assertArrayEquals(new int[] {6, 0, 0x30,0x0,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_R8_HLRworks() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c,r);


            Opcode opcode0 = Opcode.LD_B_N8;
            Opcode opcode1 = Opcode.LD_HL_N16;
            Opcode opcode2 = Opcode.LD_L_HLR;

            b.write(0, opcode0.encoding);
            b.write(1, 0x10);
            b.write(2, opcode1.encoding);
            b.write(3, 0x06);
            b.write(4, 0x00);
            b.write(5, opcode2.encoding);
            b.write(0x06, 0x07);
            cycleCpu(c, opcode0.cycles + opcode1.cycles + opcode2.cycles);
            assertArrayEquals(new int[] {6,0,0,0,0x10,0,0,0, 0, 0x07}, c._testGetPcSpAFBCDEHL());

        }
        @Test
        void LD_R8_N8_Workplease() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c,r);

            Opcode opcode = Opcode.LD_B_N8;
            b.write(0,  opcode.encoding);
            b.write(1, 0x62);
            cycleCpu(c, opcode.cycles);
            assertArrayEquals(new int[] {2,0,0,0,0x62,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());

        }
        @Test
        void LD__A_N8R() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c,r);

            Opcode opcode = Opcode.LD_A_N8R;

            b.write(0,  opcode.encoding);
            b.write(1, 32);
            b.write(0xFF20,0x20);
            cycleCpu(c, opcode.cycles);
            assertArrayEquals(new int[] {opcode.totalBytes,0,0x20,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }
        @Test
        void LD_A_CR() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c,r);


            Opcode opcode1 = Opcode.LD_A_CR;
            Opcode opcode = Opcode.LD_C_N8;
            b.write(0,  opcode.encoding);
            b.write(1, 0x20);
            b.write(2, opcode1.encoding);
            b.write(0xFF00+32, 32);
            cycleCpu(c, opcode1.cycles+ opcode.cycles);
            assertArrayEquals(new int[] {opcode1.totalBytes+opcode.totalBytes,0,0x20,0,0,0x20,0,0,0,0}, c._testGetPcSpAFBCDEHL());

        }
        @Test
        void LD_HLRUworksOnKnownValue() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c,r);

            Opcode opcode0 = Opcode.LD_HL_N16;
            Opcode opcode1 = Opcode.LD_A_HLRI;

            b.write(0, opcode0.encoding);
            b.write(1, 0x06);
            b.write(2, 0x00);
            b.write(0x6, 0x32);
            b.write(3,opcode1.encoding);
            cycleCpu(c, opcode1.cycles+ opcode0.cycles);
            assertArrayEquals(new int[] {opcode1.totalBytes+opcode0.totalBytes,0,0x32,0,0,0,0,0,0,0x7}, c._testGetPcSpAFBCDEHL());
        }
        @Test
        void LD_A_N16R() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c,r);
            Opcode opcode = Opcode.LD_A_N16R;
            b.write(1, 16);
            b.write(2, 0);

            b.write(0, opcode.encoding);
            b.write(16, 16);

            cycleCpu(c, opcode.cycles);
            assertArrayEquals(new int[] {opcode.totalBytes,0,16,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_A_BCR() {
            Cpu c = new Cpu();
            Ram r = new Ram(0xFFFF);
            Bus b = connect(c,r);

            Opcode opcode0 = Opcode.LD_BC_N16;
            Opcode opcode1 = Opcode.LD_A_BCR;

            b.write(0, opcode0.encoding);
            b.write(1, 0x12);
            b.write(2, 0);
            b.write(0x12, 0x2);
            b.write(3, opcode1.encoding);

            cycleCpu(c, opcode0.cycles + opcode1.cycles);
            assertArrayEquals(new int[] {opcode0.totalBytes + opcode1.totalBytes,0,0x2,0,0,0x12,0,0,0,0}, c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_R8_HLR_Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_HLR.encoding);
            cycleCpu(c, Opcode.LD_A_HLR.cycles);
            assertArrayEquals(new int[] { 1, 0, 126, 0, 0, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_R8_N8_Works_and_LD_A_B() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_N8.encoding);
            b.write(1, 0x11);
            b.write(2, Opcode.LD_B_A.encoding);
            cycleCpu(c, Opcode.LD_A_N8.cycles + Opcode.LD_B_A.cycles);
            assertArrayEquals(new int[] { 3, 0, 0x11, 0, 0x11, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_A_HLRU_Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_HLRI.encoding);
            cycleCpu(c, Opcode.LD_A_HLRI.cycles);// increment HL
            assertArrayEquals(new int[] { 1, 0, 42, 0, 0, 0, 0, 0, 0, 1 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void POP_16_Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_B_A.encoding);
            b.write(1, Opcode.LD_A_B.encoding);
            b.write(2, Opcode.POP_BC.encoding);
            cycleCpu(c, Opcode.LD_A_B.cycles + Opcode.LD_SP_N16.cycles
                    + Opcode.LD_B_A.cycles);
            assertArrayEquals(new int[] { 3, 2, 0, 0, 0x78, 0x47, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        //Test De Oscar
        @Test
        void addAR8WorksExceptA() {
            Opcode[] opcodesAdd = new Opcode[] {
                    Opcode.ADD_A_B, Opcode.ADD_A_C,
                    Opcode.ADD_A_D, Opcode.ADD_A_E,
                    Opcode.ADD_A_H, Opcode.ADD_A_L,
                    Opcode.ADC_A_B, Opcode.ADC_A_C,
                    Opcode.ADC_A_D, Opcode.ADC_A_E,
                    Opcode.ADC_A_H, Opcode.ADC_A_L
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8,
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8,
            };

            for(int i = 0; i < opcodesAdd.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueReg = RandomGenerator.randomBit(8);
                    int valueACarry = RandomGenerator.randomBit(8);
                    int valueRegCarry = RandomGenerator.randomBit(8);
                    boolean carry = Bits.test(Alu.unpackFlags(Alu.add(valueReg, valueA)), 4);
                    if(i<6) {
                        bus.write(0, opcodesLoad[i].encoding);
                        bus.write(1, valueReg);
                        bus.write(2, Opcode.LD_A_N8.encoding);
                        bus.write(3, valueA);
                        bus.write(4, opcodesAdd[i].encoding);
                        cycleCpu(cpu, opcodesAdd[i].cycles + opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles);
                        assertEquals(Alu.unpackValue(Alu.add(valueA, valueReg)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(cpu._testGetPcSpAFBCDEHL()[0], 5);
                        assertEquals(cpu._testGetPcSpAFBCDEHL()[3], Alu.unpackFlags(Alu.add(valueA, valueReg)));
                    } else {
                        bus.write(0, opcodesLoad[i].encoding);
                        bus.write(1, valueReg);
                        bus.write(2, Opcode.LD_A_N8.encoding);
                        bus.write(3, valueA);
                        bus.write(4, opcodesAdd[i - 6].encoding);
                        bus.write(5, opcodesLoad[i].encoding);
                        bus.write(6, valueRegCarry);
                        bus.write(7, Opcode.LD_A_N8.encoding);
                        bus.write(8, valueACarry);
                        bus.write(9, opcodesAdd[i].encoding);
                        cycleCpu(cpu, opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles
                                + opcodesAdd[i-6].cycles + opcodesLoad[i].cycles
                                + Opcode.LD_A_N8.cycles + opcodesAdd[i].cycles);
                        assertEquals( Alu.unpackValue(Alu.add(valueACarry, valueRegCarry, carry)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals( Alu.unpackFlags(Alu.add(valueACarry, valueRegCarry, carry)), cpu._testGetPcSpAFBCDEHL()[3]);

                    }
                }
            }
        }

        @Test
        void addAAWorks() {
            Opcode[] opcodesAdd = new Opcode[] {
                    Opcode.ADD_A_A, Opcode.ADC_A_A
            };
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.ADD_A_A.encoding);
                cycleCpu(cpu, Opcode.ADD_A_A.cycles + Opcode.LD_A_N8.cycles);
                assertEquals(Alu.unpackValue(Alu.add(valueA, valueA)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(cpu._testGetPcSpAFBCDEHL()[3], Alu.unpackFlags(Alu.add(valueA, valueA)));


            }
        }

        @Test
        void addAN8Works() {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueN8 = RandomGenerator.randomBit(8);
                    int valueACarry = RandomGenerator.randomBit(8);
                    int valueN8Carry = RandomGenerator.randomBit(8);
                    boolean carry = Bits.test(Alu.unpackFlags(Alu.add(valueA, valueN8)), 4);
                    if(i == 0) {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.ADD_A_N8.encoding);
                        bus.write(3, valueN8);
                        cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.ADD_A_N8.cycles);
                        assertEquals(Alu.unpackValue(Alu.add(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(4, cpu._testGetPcSpAFBCDEHL()[0]);
                        assertEquals(Alu.unpackFlags(Alu.add(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[3]);
                    }else {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.ADD_A_N8.encoding);
                        bus.write(3, valueN8);
                        bus.write(4, Opcode.LD_A_N8.encoding);
                        bus.write(5, valueACarry);
                        bus.write(6, Opcode.ADC_A_N8.encoding);
                        bus.write(7, valueN8Carry);
                        cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.ADD_A_N8.cycles
                                +Opcode.LD_A_N8.cycles + Opcode.ADC_A_N8.cycles);
                        assertEquals(Alu.unpackValue(Alu.add(valueACarry, valueN8Carry, carry)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(Alu.unpackFlags(Alu.add(valueACarry, valueN8Carry, carry)), cpu._testGetPcSpAFBCDEHL()[3]);


                    }
                }
            }
        }

        @Test
        void addAHLRworks() {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(0xFFFF);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int addressHL  = RandomGenerator.randomBit(15) + 30;
                    int valueHL = RandomGenerator.randomBit(8);
                    int valueACarry = RandomGenerator.randomBit(8);
                    int addressHLCarry = RandomGenerator.randomBit(15) + 30;
                    int valueHLCarry = RandomGenerator.randomBit(8);
                    boolean carry = Bits.test(Alu.unpackFlags(Alu.add(valueHL, valueA)), 4);

                    if(i == 0) {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.LD_HL_N16.encoding);
                        bus.write(3, Bits.clip(8, addressHL));
                        bus.write(4, Bits.extract(addressHL, 8, 8));
                        bus.write(addressHL, valueHL);
                        bus.write(5, Opcode.ADD_A_HLR.encoding);
                        cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.LD_HL_N16.cycles + Opcode.ADD_A_HLR.cycles );
                        assertEquals(Alu.unpackValue(Alu.add(valueA, valueHL)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(Alu.unpackFlags(Alu.add(valueHL, valueA)), cpu._testGetPcSpAFBCDEHL()[3]);
                    }else {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.LD_HL_N16.encoding);
                        bus.write(3, Bits.clip(8, addressHL));
                        bus.write(4, Bits.extract(addressHL, 8, 8));
                        bus.write(addressHL, valueHL);
                        bus.write(5, Opcode.ADD_A_HLR.encoding);
                        bus.write(6, Opcode.LD_A_N8.encoding);
                        bus.write(7, valueACarry);
                        bus.write(8, Opcode.LD_HL_N16.encoding);
                        bus.write(9, Bits.clip(8, addressHLCarry));
                        bus.write(10, Bits.extract(addressHLCarry, 8, 8));
                        bus.write(addressHLCarry, valueHLCarry);
                        bus.write(11, Opcode.ADC_A_HLR.encoding);
                        cycleCpu(cpu, 2* Opcode.LD_A_N8.cycles + 2*Opcode.LD_HL_N16.cycles
                                +Opcode.ADD_A_HLR.cycles + Opcode.ADC_A_HLR.cycles);
                        assertEquals(Alu.unpackValue(Alu.add(valueACarry, valueHLCarry, carry)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(Alu.unpackFlags(Alu.add(valueACarry, valueHLCarry, carry)), cpu._testGetPcSpAFBCDEHL()[3]);

                    }
                }

            }
        }

        @Test
        void incR8Works() {
            Opcode[] opcodesInc = new Opcode[] {
                    Opcode.INC_A, Opcode.INC_B,
                    Opcode.INC_C, Opcode.INC_D,
                    Opcode.INC_E, Opcode.INC_H,
                    Opcode.INC_L
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_A_N8,
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8
            };

            for(int i = 0; i < opcodesInc.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(5);
                    Bus bus = connect(cpu, ram);
                    int randomIncremented = RandomGenerator.randomBit(8);
                    bus.write(0, opcodesLoad[i].encoding);
                    bus.write(1, randomIncremented);
                    bus.write(2, opcodesInc[i].encoding);
                    cycleCpu(cpu, opcodesLoad[i].cycles + opcodesInc[i].cycles);
                    if(i == 0)
                        assertEquals(Alu.unpackValue(Alu.add(randomIncremented, 1)), cpu._testGetPcSpAFBCDEHL()[2]);
                    else
                        assertEquals(Alu.unpackValue(Alu.add(randomIncremented, 1)), cpu._testGetPcSpAFBCDEHL()[3+i]);
                    assertEquals(Bits.set(Alu.unpackFlags(Alu.add(randomIncremented, 1)),4 , Bits.test(cpu._testGetPcSpAFBCDEHL()[3], 4)), cpu._testGetPcSpAFBCDEHL()[3]);
                    assertEquals(opcodesLoad[i].totalBytes + opcodesInc[i].totalBytes, cpu._testGetPcSpAFBCDEHL()[0]);


                }
            }
        }

        @Test
        void incHLR() {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(0xFFFF);
                Bus bus = connect(cpu, ram);
                int addressHL  = RandomGenerator.randomBit(15) + 6;
                int valueHL = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_HL_N16.encoding);
                bus.write(1, Bits.clip(8, addressHL));
                bus.write(2, Bits.extract(addressHL, 8, 8));
                bus.write(addressHL, valueHL);
                bus.write(3, Opcode.INC_HLR.encoding);
                bus.write(4, Opcode.LD_A_HLR.encoding);
                cycleCpu(cpu, Opcode.LD_HL_N16.cycles + Opcode.INC_HLR.cycles + Opcode.LD_A_HLR.cycles);
                assertEquals(Alu.unpackValue(Alu.add(1, valueHL)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(Bits.set(Alu.unpackFlags(Alu.add(valueHL, 1)),4 , Bits.test(cpu._testGetPcSpAFBCDEHL()[3], 4)), cpu._testGetPcSpAFBCDEHL()[3]);
                assertEquals(Opcode.LD_HL_N16.totalBytes + Opcode.INC_HLR.totalBytes + Opcode.LD_A_HLR.totalBytes, cpu._testGetPcSpAFBCDEHL()[0]);
            }
        }
        @Test
        void incR16SPWorks() {
            Opcode[] opcodesLoadR16 =  new Opcode[] {
                    Opcode.LD_BC_N16, Opcode.LD_DE_N16, Opcode.LD_HL_N16,
                    Opcode.LD_SP_N16
            };
            Opcode[] opcodesIncR16 = new Opcode[] {
                    Opcode.INC_BC, Opcode.INC_DE, Opcode.INC_HL,
                    Opcode.INC_SP
            };
            for(int i = 0; i < opcodesLoadR16.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(5);
                    Bus bus = connect(cpu, ram);
                    int randomIncremented = RandomGenerator.randomBit(15) + 5;
                    bus.write(0, opcodesLoadR16[i].encoding);
                    bus.write(1, Bits.clip(8, randomIncremented));
                    bus.write(2, Bits.extract(randomIncremented, 8, 8));
                    bus.write(3, opcodesIncR16[i].encoding);
                    cycleCpu(cpu, opcodesLoadR16[i].cycles + opcodesIncR16[i].cycles);
                    assertEquals(opcodesLoadR16[i].totalBytes + opcodesIncR16[i].totalBytes, cpu._testGetPcSpAFBCDEHL()[0]);
                    switch(opcodesIncR16[i]) {
                    case INC_BC:
                        assertEquals(Alu.unpackValue(Alu.add16H(randomIncremented, 1)), Bits.make16(cpu._testGetPcSpAFBCDEHL()[4], cpu._testGetPcSpAFBCDEHL()[5]));
                        break;
                    case INC_DE:
                        assertEquals(Alu.unpackValue(Alu.add16H(randomIncremented, 1)), Bits.make16(cpu._testGetPcSpAFBCDEHL()[6], cpu._testGetPcSpAFBCDEHL()[7]));
                        break;
                    case INC_HL:
                        assertEquals(Alu.unpackValue(Alu.add16H(randomIncremented, 1)), Bits.make16(cpu._testGetPcSpAFBCDEHL()[8], cpu._testGetPcSpAFBCDEHL()[9]));
                        break;
                    case INC_SP:
                        assertEquals(Alu.unpackValue(Alu.add16H(randomIncremented, 1)), cpu._testGetPcSpAFBCDEHL()[1]);
                        break;
                    }
                }
            }
        }

        @Test
        void andAN8Works () {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                int valueN8 = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.AND_A_N8.encoding);
                bus.write(3, valueN8);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.ADD_A_N8.cycles);
                assertEquals(Alu.unpackValue(Alu.and(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(4, cpu._testGetPcSpAFBCDEHL()[0]);
                assertEquals(Alu.unpackFlags(Alu.and(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void andAR8Works () {
            Opcode[] opcodesAnd = new Opcode[] {
                    Opcode.AND_A_B,
                    Opcode.AND_A_C,
                    Opcode.AND_A_D,
                    Opcode.AND_A_E,
                    Opcode.AND_A_H,
                    Opcode.AND_A_L,
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_B_N8,
                    Opcode.LD_C_N8,
                    Opcode.LD_D_N8,
                    Opcode.LD_E_N8,
                    Opcode.LD_H_N8,
                    Opcode.LD_L_N8
            };
            for(int i = 0; i < opcodesAnd.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(5);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueReg = RandomGenerator.randomBit(8);
                    bus.write(0, opcodesLoad[i].encoding);
                    bus.write(1, valueReg);
                    bus.write(2, Opcode.LD_A_N8.encoding);
                    bus.write(3, valueA);
                    bus.write(4, opcodesAnd[i].encoding);
                    cycleCpu(cpu, opcodesAnd[i].cycles + opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles);
                    assertEquals(Alu.unpackValue(Alu.and(valueA, valueReg)), cpu._testGetPcSpAFBCDEHL()[2]);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[0], 5);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[3], Alu.unpackFlags(Alu.and(valueA, valueReg)));

                }
            }

        }
        @Test
        void orAHLRworks() {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(0xFFFF);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                int addressHL  = RandomGenerator.randomBit(15)+6;
                int valueHL = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.LD_HL_N16.encoding);
                bus.write(3, Bits.clip(8, addressHL));
                bus.write(4, Bits.extract(addressHL, 8, 8));
                bus.write(addressHL, valueHL);
                bus.write(5, Opcode.OR_A_HLR.encoding);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.LD_HL_N16.cycles + Opcode.OR_A_HLR.cycles );
                assertEquals(Alu.unpackValue(Alu.or(valueA, valueHL)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(Alu.unpackFlags(Alu.or(valueHL, valueA)), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void orAN8Works () {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                int valueN8 = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.OR_A_N8.encoding);
                bus.write(3, valueN8);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.OR_A_N8.cycles);
                assertEquals(Alu.unpackValue(Alu.or(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(4, cpu._testGetPcSpAFBCDEHL()[0]);
                assertEquals(Alu.unpackFlags(Alu.or(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void orAR8Works () {
            Opcode[] opcodesAnd = new Opcode[] {
                    Opcode.OR_A_B,
                    Opcode.OR_A_C,
                    Opcode.OR_A_D,
                    Opcode.OR_A_E,
                    Opcode.OR_A_H,
                    Opcode.OR_A_L,
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_B_N8,
                    Opcode.LD_C_N8,
                    Opcode.LD_D_N8,
                    Opcode.LD_E_N8,
                    Opcode.LD_H_N8,
                    Opcode.LD_L_N8
            };
            for(int i = 0; i < opcodesAnd.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(5);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueReg = RandomGenerator.randomBit(8);
                    bus.write(0, opcodesLoad[i].encoding);
                    bus.write(1, valueReg);
                    bus.write(2, Opcode.LD_A_N8.encoding);
                    bus.write(3, valueA);
                    bus.write(4, opcodesAnd[i].encoding);
                    cycleCpu(cpu, opcodesAnd[i].cycles + opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles);
                    assertEquals(Alu.unpackValue(Alu.or(valueA, valueReg)), cpu._testGetPcSpAFBCDEHL()[2]);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[0], 5);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[3], Alu.unpackFlags(Alu.or(valueA, valueReg)));

                }
            }

        }
        @Test
        void andAHLRworks() {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(0xFFFF);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                int addressHL  = RandomGenerator.randomBit(15)+6;
                int valueHL = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.LD_HL_N16.encoding);
                bus.write(3, Bits.clip(8, addressHL));
                bus.write(4, Bits.extract(addressHL, 8, 8));
                bus.write(addressHL, valueHL);
                bus.write(5, Opcode.AND_A_HLR.encoding);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.LD_HL_N16.cycles + Opcode.AND_A_HLR.cycles );
                assertEquals(Alu.unpackValue(Alu.and(valueA, valueHL)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(Alu.unpackFlags(Alu.and(valueHL, valueA)), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void xorAHLRworks() {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(0xFFFF);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                int addressHL  = RandomGenerator.randomBit(15)+20;
                int valueHL = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.LD_HL_N16.encoding);
                bus.write(3, Bits.clip(8, addressHL));
                bus.write(4, Bits.extract(addressHL, 8, 8));
                bus.write(addressHL, valueHL);
                bus.write(5, Opcode.XOR_A_HLR.encoding);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.LD_HL_N16.cycles + Opcode.XOR_A_HLR.cycles );
                assertEquals(Alu.unpackValue(Alu.xor(valueA, valueHL)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(Alu.unpackFlags(Alu.xor(valueHL, valueA)), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void xorAN8Works () {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                int valueN8 = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.XOR_A_N8.encoding);
                bus.write(3, valueN8);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.XOR_A_N8.cycles);
                assertEquals(Alu.unpackValue(Alu.xor(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(4, cpu._testGetPcSpAFBCDEHL()[0]);
                assertEquals(Alu.unpackFlags(Alu.xor(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void xorAR8Works () {
            Opcode[] opcodesAnd = new Opcode[] {
                    Opcode.XOR_A_B,
                    Opcode.XOR_A_C,
                    Opcode.XOR_A_D,
                    Opcode.XOR_A_E,
                    Opcode.XOR_A_H,
                    Opcode.XOR_A_L,
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_B_N8,
                    Opcode.LD_C_N8,
                    Opcode.LD_D_N8,
                    Opcode.LD_E_N8,
                    Opcode.LD_H_N8,
                    Opcode.LD_L_N8
            };
            for(int i = 0; i < opcodesAnd.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(5);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueReg = RandomGenerator.randomBit(8);
                    bus.write(0, opcodesLoad[i].encoding);
                    bus.write(1, valueReg);
                    bus.write(2, Opcode.LD_A_N8.encoding);
                    bus.write(3, valueA);
                    bus.write(4, opcodesAnd[i].encoding);
                    cycleCpu(cpu, opcodesAnd[i].cycles + opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles);
                    assertEquals(Alu.unpackValue(Alu.xor(valueA, valueReg)), cpu._testGetPcSpAFBCDEHL()[2]);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[0], 5);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[3], Alu.unpackFlags(Alu.xor(valueA, valueReg)));

                }
            }

        }

        @Test
        void cplWorks () {
            Cpu cpu = new Cpu();
            Ram ram = new Ram(5);
            Bus bus = connect(cpu, ram);
            int valueA = RandomGenerator.randomBit(8);
            bus.write(0, Opcode.LD_A_N8.encoding);
            bus.write(1, valueA);
            bus.write(2, Opcode.CPL.encoding);
            int fanions = cpu._testGetPcSpAFBCDEHL()[3];
            cycleCpu(cpu,  Opcode.CPL.cycles + Opcode.LD_A_N8.cycles);
            assertEquals(Bits.clip(8, ~valueA), cpu._testGetPcSpAFBCDEHL()[2]);
            assertEquals(cpu._testGetPcSpAFBCDEHL()[0], 3);
            assertEquals(cpu._testGetPcSpAFBCDEHL()[3], fanions | (1 << 6) | (1<<5) );
        }

        @Test
        void addHlr16SPWorks() {
            Opcode[] opcodesAdd = new Opcode[] {
                    Opcode.ADD_HL_BC, Opcode.ADD_HL_DE,
                    Opcode.ADD_HL_SP, Opcode.ADD_HL_HL
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_BC_N16, Opcode.LD_DE_N16,
                    Opcode.LD_SP_N16, Opcode.LD_HL_N16,
            };
            for(int i = 0; i < opcodesAdd.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueHL = RandomGenerator.randomBit(16);
                    int valueR16 = RandomGenerator.randomBit(16);
                    bus.write(0, opcodesLoad[i].encoding);
                    bus.write(1, Bits.clip(8, valueR16));
                    bus.write(2, Bits.extract(valueR16, 8, 8));
                    bus.write(3, Opcode.LD_HL_N16.encoding);
                    bus.write(4, Bits.clip(8, valueHL));
                    bus.write(5, Bits.extract(valueHL, 8, 8));
                    bus.write(6, opcodesAdd[i].encoding);
                    cycleCpu(cpu, opcodesLoad[i].cycles + Opcode.LD_HL_N16.cycles + opcodesAdd[i].cycles);
                    assertEquals(opcodesLoad[i].totalBytes + Opcode.LD_HL_N16.totalBytes + opcodesAdd[i].totalBytes, cpu._testGetPcSpAFBCDEHL()[0]);
                    if(opcodesLoad[i]  == Opcode.LD_HL_N16) {
                        assertEquals(Alu.unpackValue(Alu.add16H(valueHL, valueHL)), Bits.make16(cpu._testGetPcSpAFBCDEHL()[8], cpu._testGetPcSpAFBCDEHL()[9]));
                        assertEquals(Alu.unpackFlags(Alu.add16H(valueHL, valueHL)), cpu._testGetPcSpAFBCDEHL()[3]);
                    } else {
                        assertEquals(Alu.unpackValue(Alu.add16H(valueHL, valueR16)), Bits.make16(cpu._testGetPcSpAFBCDEHL()[8], cpu._testGetPcSpAFBCDEHL()[9]));
                        assertEquals(Alu.unpackFlags(Alu.add16H(valueHL, valueR16)), cpu._testGetPcSpAFBCDEHL()[3]);
                    } // pc (0), sp (1), a (2), f (3), b (4), c (5), d (6), e (7), h (8), l (9)
                }
            }
        }

        @Test
        void ldHLSPS8Works() {
            Opcode[] opcodesTested = new Opcode[] {
                    Opcode.LD_HL_SP_N8, Opcode. ADD_SP_N
            };
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueSP = RandomGenerator.randomBit(16);
                    int valueE8 = RandomGenerator.randomBit(8);
                    if(i == 0) {
                        bus.write(0, Opcode.LD_SP_N16.encoding);
                        bus.write(1, Bits.clip(8, valueSP));
                        bus.write(2, Bits.extract(valueSP, 8, 8));
                        bus.write(3, Opcode.LD_HL_SP_N8.encoding);
                        bus.write(4, valueE8);
                        cycleCpu(cpu, Opcode.LD_SP_N16.cycles + Opcode.LD_HL_SP_N8.cycles);
                        assertEquals(Bits.clip(16, valueSP + Bits.signExtend8(valueE8)), Bits.make16(cpu._testGetPcSpAFBCDEHL()[8], cpu._testGetPcSpAFBCDEHL()[9]));
                        assertEquals(Alu.unpackFlags(Alu.add16L(valueSP, valueE8)), cpu._testGetPcSpAFBCDEHL()[3]);
                    } else {
                        bus.write(0, Opcode.LD_SP_N16.encoding);
                        bus.write(1, Bits.clip(8, valueSP));
                        bus.write(2, Bits.extract(valueSP, 8, 8));
                        bus.write(3, Opcode.ADD_SP_N.encoding);
                        bus.write(4, valueE8);
                        cycleCpu(cpu, Opcode.LD_SP_N16.cycles + Opcode.ADD_SP_N.cycles);
                        assertEquals(Bits.clip(16, valueSP + Bits.signExtend8(valueE8)), cpu._testGetPcSpAFBCDEHL()[1]);
                        assertEquals(Alu.unpackFlags(Alu.add16L(valueSP, valueE8)), cpu._testGetPcSpAFBCDEHL()[3]);

                    }
                }
            }
        }

        @Test
        void subAR8WorksExceptA() {
            Opcode[] opcodesSub = new Opcode[] {
                    Opcode.SUB_A_B, Opcode.SUB_A_C,
                    Opcode.SUB_A_D, Opcode.SUB_A_E,
                    Opcode.SUB_A_H, Opcode.SUB_A_L,
                    Opcode.SBC_A_B, Opcode.SBC_A_C,
                    Opcode.SBC_A_D, Opcode.SBC_A_E,
                    Opcode.SBC_A_H, Opcode.SBC_A_L
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8,
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8,
            };

            for(int i = 0; i < opcodesSub.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueReg = RandomGenerator.randomBit(8);
                    int valueACarry = RandomGenerator.randomBit(8);
                    int valueRegCarry = RandomGenerator.randomBit(8);
                    boolean carry = Bits.test(Alu.unpackFlags(Alu.sub(valueA, valueReg)), 4);
                    if(i<6) {
                        bus.write(0, opcodesLoad[i].encoding);
                        bus.write(1, valueReg);
                        bus.write(2, Opcode.LD_A_N8.encoding);
                        bus.write(3, valueA);
                        bus.write(4, opcodesSub[i].encoding);
                        cycleCpu(cpu, opcodesSub[i].cycles + opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles);
                        assertEquals(Alu.unpackValue(Alu.sub(valueA, valueReg)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(cpu._testGetPcSpAFBCDEHL()[0], 5);
                        assertEquals(cpu._testGetPcSpAFBCDEHL()[3], Alu.unpackFlags(Alu.sub(valueA, valueReg)));
                    } else {
                        bus.write(0, opcodesLoad[i].encoding);
                        bus.write(1, valueReg);
                        bus.write(2, Opcode.LD_A_N8.encoding);
                        bus.write(3, valueA);
                        bus.write(4, opcodesSub[i - 6].encoding);
                        bus.write(5, opcodesLoad[i].encoding);
                        bus.write(6, valueRegCarry);
                        bus.write(7, Opcode.LD_A_N8.encoding);
                        bus.write(8, valueACarry);
                        bus.write(9, opcodesSub[i].encoding);
                        cycleCpu(cpu, opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles
                                + opcodesSub[i-6].cycles + opcodesLoad[i].cycles
                                + Opcode.LD_A_N8.cycles + opcodesSub[i].cycles);
                        assertEquals( Alu.unpackValue(Alu.sub(valueACarry, valueRegCarry, carry)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals( Alu.unpackFlags(Alu.sub(valueACarry, valueRegCarry, carry)), cpu._testGetPcSpAFBCDEHL()[3]);

                    }
                }
            }
        }


        @Test
        void subAN8Works() {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueN8 = RandomGenerator.randomBit(8);
                    int valueACarry = RandomGenerator.randomBit(8);
                    int valueN8Carry = RandomGenerator.randomBit(8);
                    boolean carry = Bits.test(Alu.unpackFlags(Alu.sub(valueA, valueN8)), 4);
                    if(i == 0) {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.SUB_A_N8.encoding);
                        bus.write(3, valueN8);
                        cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.SUB_A_N8.cycles);
                        assertEquals(Alu.unpackValue(Alu.sub(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(4, cpu._testGetPcSpAFBCDEHL()[0]);
                        assertEquals(Alu.unpackFlags(Alu.sub(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[3]);
                    }else {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.SUB_A_N8.encoding);
                        bus.write(3, valueN8);
                        bus.write(4, Opcode.LD_A_N8.encoding);
                        bus.write(5, valueACarry);
                        bus.write(6, Opcode.SBC_A_N8.encoding);
                        bus.write(7, valueN8Carry);
                        cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.SUB_A_N8.cycles
                                +Opcode.LD_A_N8.cycles + Opcode.SBC_A_N8.cycles);
                        assertEquals(Alu.unpackValue(Alu.sub(valueACarry, valueN8Carry, carry)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(Alu.unpackFlags(Alu.sub(valueACarry, valueN8Carry, carry)), cpu._testGetPcSpAFBCDEHL()[3]);


                    }
                }
            }
        }

        @Test
        void subAAWorks() {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.SUB_A_A.encoding);
                cycleCpu(cpu, Opcode.SUB_A_A.cycles + Opcode.LD_A_N8.cycles);
                assertEquals(0, cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(0b11000000, cpu._testGetPcSpAFBCDEHL()[3]);

            }
        }


        @Test
        void subAHLRworks() {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(0xFFFF);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int addressHL  = RandomGenerator.randomBit(15) + 30;
                    int valueHL = RandomGenerator.randomBit(8);
                    int valueACarry = RandomGenerator.randomBit(8);
                    int addressHLCarry = RandomGenerator.randomBit(15) + 30;
                    int valueHLCarry = RandomGenerator.randomBit(8);
                    boolean carry = Bits.test(Alu.unpackFlags(Alu.sub(valueA, valueHL)), 4);

                    if(i == 0) {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.LD_HL_N16.encoding);
                        bus.write(3, Bits.clip(8, addressHL));
                        bus.write(4, Bits.extract(addressHL, 8, 8));
                        bus.write(addressHL, valueHL);
                        bus.write(5, Opcode.SUB_A_HLR.encoding);
                        cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.LD_HL_N16.cycles + Opcode.SUB_A_HLR.cycles );
                        assertEquals(Alu.unpackValue(Alu.sub(valueA, valueHL)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(Alu.unpackFlags(Alu.sub(valueA, valueHL)), cpu._testGetPcSpAFBCDEHL()[3]);
                    }else {
                        bus.write(0, Opcode.LD_A_N8.encoding);
                        bus.write(1, valueA);
                        bus.write(2, Opcode.LD_HL_N16.encoding);
                        bus.write(3, Bits.clip(8, addressHL));
                        bus.write(4, Bits.extract(addressHL, 8, 8));
                        bus.write(addressHL, valueHL);
                        bus.write(5, Opcode.SUB_A_HLR.encoding);
                        bus.write(6, Opcode.LD_A_N8.encoding);
                        bus.write(7, valueACarry);
                        bus.write(8, Opcode.LD_HL_N16.encoding);
                        bus.write(9, Bits.clip(8, addressHLCarry));
                        bus.write(10, Bits.extract(addressHLCarry, 8, 8));
                        bus.write(addressHLCarry, valueHLCarry);
                        bus.write(11, Opcode.SBC_A_HLR.encoding);
                        cycleCpu(cpu, 2* Opcode.LD_A_N8.cycles + 2*Opcode.LD_HL_N16.cycles
                                +Opcode.SUB_A_HLR.cycles + Opcode.SBC_A_HLR.cycles);
                        assertEquals(Alu.unpackValue(Alu.sub(valueACarry, valueHLCarry, carry)), cpu._testGetPcSpAFBCDEHL()[2]);
                        assertEquals(Alu.unpackFlags(Alu.sub(valueACarry, valueHLCarry, carry)), cpu._testGetPcSpAFBCDEHL()[3]);

                    }
                }

            }
        }

        @Test
        void decR8Works() {
            Opcode[] opcodesDec = new Opcode[] {
                    Opcode.DEC_A, Opcode.DEC_B,
                    Opcode.DEC_C, Opcode.DEC_D,
                    Opcode.DEC_E, Opcode.DEC_H,
                    Opcode.DEC_L
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_A_N8,
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8
            };

            for(int i = 0; i < opcodesDec.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(5);
                    Bus bus = connect(cpu, ram);
                    int randomIncremented = RandomGenerator.randomBit(8);
                    bus.write(0, opcodesLoad[i].encoding);
                    bus.write(1, randomIncremented);
                    bus.write(2, opcodesDec[i].encoding);
                    cycleCpu(cpu, opcodesLoad[i].cycles + opcodesDec[i].cycles);
                    if(i == 0)
                        assertEquals(Alu.unpackValue(Alu.sub(randomIncremented, 1)), cpu._testGetPcSpAFBCDEHL()[2]);
                    else
                        assertEquals(Alu.unpackValue(Alu.sub(randomIncremented, 1)), cpu._testGetPcSpAFBCDEHL()[3+i]);
                    assertEquals(Bits.set(Alu.unpackFlags(Alu.sub(randomIncremented, 1)),4 , false), cpu._testGetPcSpAFBCDEHL()[3]);
                    assertEquals(opcodesLoad[i].totalBytes + opcodesDec[i].totalBytes, cpu._testGetPcSpAFBCDEHL()[0]);

                }
            }
        }

        @Test
        void decHLR() {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(0xFFFF);
                Bus bus = connect(cpu, ram);
                int addressHL  = RandomGenerator.randomBit(15) + 6;
                int valueHL = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_HL_N16.encoding);
                bus.write(1, Bits.clip(8, addressHL));
                bus.write(2, Bits.extract(addressHL, 8, 8));
                bus.write(addressHL, valueHL);
                bus.write(3, Opcode.DEC_HLR.encoding);
                bus.write(4, Opcode.LD_A_HLR.encoding);
                cycleCpu(cpu, Opcode.LD_HL_N16.cycles + Opcode.DEC_HLR.cycles + Opcode.LD_A_HLR.cycles);
                assertEquals(Alu.unpackValue(Alu.sub(valueHL, 1)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(Bits.set(Alu.unpackFlags(Alu.sub(valueHL, 1)),4 , false), cpu._testGetPcSpAFBCDEHL()[3]);
                assertEquals(Opcode.LD_HL_N16.totalBytes + Opcode.DEC_HLR.totalBytes + Opcode.LD_A_HLR.totalBytes, cpu._testGetPcSpAFBCDEHL()[0]);
            }
        }

        @Test
        void decR16SPWorks() {
            Opcode[] opcodesLoadR16 =  new Opcode[] {
                    Opcode.LD_BC_N16, Opcode.LD_DE_N16, Opcode.LD_HL_N16,
                    Opcode.LD_SP_N16
            };
            Opcode[] opcodesDecR16 = new Opcode[] {
                    Opcode.DEC_BC, Opcode.DEC_DE, Opcode.DEC_HL,
                    Opcode.DEC_SP
            };
            for(int i = 0; i < opcodesLoadR16.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(5);
                    Bus bus = connect(cpu, ram);
                    int randomDecremented = RandomGenerator.randomBit(15) + 5;
                    bus.write(0, opcodesLoadR16[i].encoding);
                    bus.write(1, Bits.clip(8, randomDecremented));
                    bus.write(2, Bits.extract(randomDecremented, 8, 8));
                    bus.write(3, opcodesDecR16[i].encoding);
                    cycleCpu(cpu, opcodesLoadR16[i].cycles + opcodesDecR16[i].cycles);
                    assertEquals(opcodesLoadR16[i].totalBytes + opcodesDecR16[i].totalBytes, cpu._testGetPcSpAFBCDEHL()[0]);
                    switch(opcodesDecR16[i]) {
                    case DEC_BC:
                        assertEquals(Bits.clip(16, randomDecremented - 1), Bits.make16(cpu._testGetPcSpAFBCDEHL()[4], cpu._testGetPcSpAFBCDEHL()[5]));
                        break;
                    case DEC_DE:
                        assertEquals(Bits.clip(16 , randomDecremented - 1), Bits.make16(cpu._testGetPcSpAFBCDEHL()[6], cpu._testGetPcSpAFBCDEHL()[7]));
                        break;
                    case DEC_HL:
                        assertEquals(Bits.clip(16, randomDecremented - 1), Bits.make16(cpu._testGetPcSpAFBCDEHL()[8], cpu._testGetPcSpAFBCDEHL()[9]));
                        break;
                    case DEC_SP:
                        assertEquals(Bits.clip(16, randomDecremented -  1), cpu._testGetPcSpAFBCDEHL()[1]);
                        break;
                    }
                }
            }
        }

        @Test
        void cpAR8WorksExceptA() {
            Opcode[] opcodesSub = new Opcode[] {
                    Opcode.CP_A_B, Opcode.CP_A_C,
                    Opcode.CP_A_D, Opcode.CP_A_E,
                    Opcode.CP_A_H, Opcode.CP_A_L,
            };
            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8,
            };

            for(int i = 0; i < opcodesSub.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueReg = RandomGenerator.randomBit(8);
                    bus.write(0, opcodesLoad[i].encoding);
                    bus.write(1, valueReg);
                    bus.write(2, Opcode.LD_A_N8.encoding);
                    bus.write(3, valueA);
                    bus.write(4, opcodesSub[i].encoding);
                    cycleCpu(cpu, opcodesSub[i].cycles + opcodesLoad[i].cycles + Opcode.LD_A_N8.cycles);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[0], 5);
                    assertEquals(cpu._testGetPcSpAFBCDEHL()[3], Alu.unpackFlags(Alu.sub(valueA, valueReg)));
                }
            }
        }

        @Test
        void cpAAWorks() {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.CP_A_A.encoding);
                cycleCpu(cpu, Opcode.CP_A_A.cycles + Opcode.LD_A_N8.cycles);
                assertEquals(0b11000000, cpu._testGetPcSpAFBCDEHL()[3]);

            }
        }

        @Test
        void cpAN8Works() {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(10);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int valueN8 = RandomGenerator.randomBit(8);
                    bus.write(0, Opcode.LD_A_N8.encoding);
                    bus.write(1, valueA);
                    bus.write(2, Opcode.SUB_A_N8.encoding);
                    bus.write(3, valueN8);
                    cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.SUB_A_N8.cycles);
                    assertEquals(4, cpu._testGetPcSpAFBCDEHL()[0]);
                    assertEquals(Alu.unpackFlags(Alu.sub(valueA, valueN8)), cpu._testGetPcSpAFBCDEHL()[3]);

                }
            }
        }

        @Test
        void cpAHLRworks() {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    Cpu cpu = new Cpu();
                    Ram ram = new Ram(0xFFFF);
                    Bus bus = connect(cpu, ram);
                    int valueA = RandomGenerator.randomBit(8);
                    int addressHL  = RandomGenerator.randomBit(15) + 30;
                    int valueHL = RandomGenerator.randomBit(8);
                    bus.write(0, Opcode.LD_A_N8.encoding);
                    bus.write(1, valueA);
                    bus.write(2, Opcode.LD_HL_N16.encoding);
                    bus.write(3, Bits.clip(8, addressHL));
                    bus.write(4, Bits.extract(addressHL, 8, 8));
                    bus.write(addressHL, valueHL);
                    bus.write(5, Opcode.CP_A_HLR.encoding);
                    cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.LD_HL_N16.cycles + Opcode.CP_A_HLR.cycles );
                    assertEquals(Alu.unpackFlags(Alu.sub(valueA, valueHL)), cpu._testGetPcSpAFBCDEHL()[3]);
                }

            }
        }

        @Test
        void RotCALeft () {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.RLCA.encoding);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.RLCA.cycles);
                assertEquals(Alu.unpackValue(Alu.rotate(RotDir.LEFT, valueA)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(3, cpu._testGetPcSpAFBCDEHL()[0]);
                assertEquals(Alu.unpackFlags(Alu.rotate(RotDir.LEFT, valueA)) & (1 << 4), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void RotCARight () {
            for(int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
                Cpu cpu = new Cpu();
                Ram ram = new Ram(5);
                Bus bus = connect(cpu, ram);
                int valueA = RandomGenerator.randomBit(8);
                bus.write(0, Opcode.LD_A_N8.encoding);
                bus.write(1, valueA);
                bus.write(2, Opcode.RRCA.encoding);
                cycleCpu(cpu, Opcode.LD_A_N8.cycles + Opcode.RRCA.cycles);
                assertEquals(Alu.unpackValue(Alu.rotate(RotDir.RIGHT, valueA)), cpu._testGetPcSpAFBCDEHL()[2]);
                assertEquals(3, cpu._testGetPcSpAFBCDEHL()[0]);
                assertEquals(Alu.unpackFlags(Alu.rotate(RotDir.RIGHT, valueA)) & (1 << 4), cpu._testGetPcSpAFBCDEHL()[3]);
            }
        }

        @Test
        void bitU3R8works() {
            Opcode[] opcodesTest = new Opcode[] {
                    Opcode.BIT_0_A, Opcode.BIT_0_B,
                    Opcode.BIT_0_C, Opcode.BIT_0_D,
                    Opcode.BIT_0_E, Opcode.BIT_0_H,
                    Opcode.BIT_0_L
            };

            Opcode[] opcodesLoad = new Opcode[] {
                    Opcode.LD_A_N8,
                    Opcode.LD_B_N8, Opcode.LD_C_N8,
                    Opcode.LD_D_N8, Opcode.LD_E_N8,
                    Opcode.LD_H_N8, Opcode.LD_L_N8
            };
            for(int i = 0; i < opcodesTest.length; i++) {
                for(int j = 0; j < RandomGenerator.RANDOM_ITERATIONS; j++) {
                    for(int k = 0; k < 8; k++) {
                        Cpu cpu = new Cpu();
                        Ram ram = new Ram(10);
                        Bus bus = connect(cpu, ram);
                        int valueTested = RandomGenerator.randomBit(8);
                    }
                }
            }
        }

        @Test
        void SCF_WorksOnKnowValues(){
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);

            b.write(0, Opcode.SCF.encoding);
            cycleCpu(c, Opcode.SCF.cycles);
            assertArrayEquals(new int[] { 1,0,0,16,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        }

}
