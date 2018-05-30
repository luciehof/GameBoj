package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.RandomGenerator;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;

import static ch.epfl.gameboj.component.cpu.Opcode.*;
import static ch.epfl.gameboj.component.cpu.Opcode.ADC_A_L;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MiniCpuTest {

    private Bus bus;
    private Cpu cpu;
    private int pc;

    private final int RAM_SIZE = 0xFFFF ;

    @BeforeEach
    public void initialize() {
        /* Create a new Cpu and a new Ram of 20 bytes
         * Creates a simple RamController for given Ram and attaches Cpu and
         * RamController to Bus.
         */
        cpu = new Cpu();
        bus = new Bus();
        Ram ram = new Ram(RAM_SIZE);
        RamController ramController = new RamController(ram, 0);
        cpu.attachTo(bus);
        ramController.attachTo(bus);
    }

    private void cycleCpu(long cycles) {
        for(long i = 0; i < cycles; i++) {
            cpu.cycle(i);
        }
    }

    private void cycleCpu(long cdep, long cfin) {
        for(long i = cdep; i <= cfin; ++i) {
            cpu.cycle(i);
        }
    }

    private void writeAllBytes(int ... bytes) {
        if(bytes.length > RAM_SIZE)
            throw new IllegalArgumentException("not enough ram for that");
        for(int i = 0; i<bytes.length; i++) {
            int instr = bytes[i];
            Preconditions.checkBits8(instr);
            bus.write(i, instr);
        }
    }

    /*private int[] cpuState(int pc, int sp, int a, int f, int b, int ch.epfl.gameboj.RandomGenerator, int d, int e, int h, int l) {
        return new int[] { pc, sp, a, f, b, ch.epfl.gameboj.RandomGenerator, d, e, h, l };
    }*/
    /*private int[] initiateRegs(int pc, int sp, int a, int f, int b, int c, int d, int e, int h, int l) {
        cpu.setAllRegs(pc, sp, a, f, b, c, d, e, h, l);
        return new int[] { pc, sp, a, f, b, c, d, e, h, l };
    }

    /*private Bus connect(Cpu cpu, Ram ram) {
        RamController rc = new RamController(ram, 0);
        Bus b = new Bus();
        cpu.attachTo(b);
        rc.attachTo(b);
        return b;
    }*/

    @Test void nopDoesNothing() {
        bus.write(0, Opcode.NOP.encoding);// initiates PC to NOP encoding
        cycleCpu(Opcode.NOP.cycles); // execute le nb de cylcle necessaire à l'instruction
        assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, cpu._testGetPcSpAFBCDEHL());
    }

    // NOS TESTS POUR ETAPE 4 //

    @Test void addAN8Works() throws IOException {
        bus.write(0, Opcode.ADD_A_N8.encoding); // initialise pc to ADD_A_N8
        int value = RandomGenerator.randomInt(0b11111111, 0b0);
        // check if value = 0 for flags
        int f = 0;
        if(value == 0)
            f = 0x80;
        bus.write(1, value); // initialise N8 a value
        cycleCpu(Opcode.ADD_A_N8.cycles); // execute le nb de cylcle necessaire a l'instruction
        assertArrayEquals(new int[] { 2, 0, value, f, 0, 0, 0, 0, 0, 0 }, cpu._testGetPcSpAFBCDEHL());
    }

    @Test void RLCAWorks() throws IOException {
       bus.write(0, Opcode.LD_A_N8.encoding);
       int value = RandomGenerator.randomInt(0b11111111, 0b0);

       System.out.println(Integer.toBinaryString(value));
       System.out.println(value);

       bus.write(1, value);
       //cycleCpu(LD_A_N8.cycles);
       bus.write(2, Opcode.RLCA.encoding);
       int packed = Alu.rotate(Alu.RotDir.LEFT, value);
       int exp = Alu.unpackValue(packed);
       int F = Bits.extract(Alu.unpackFlags(packed), 4, 1);

       System.out.println(Integer.toBinaryString(exp));
       System.out.println(exp);
       cycleCpu(Opcode.RLCA.cycles + LD_A_N8.cycles);
       assertArrayEquals(new int[] { cpu._testGetPcSpAFBCDEHL()[0], 0, exp , F << 4, 0, 0, 0, 0, 0, 0 }, cpu._testGetPcSpAFBCDEHL());
    }



}