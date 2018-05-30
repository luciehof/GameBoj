// Gameboj stage 2

package ch.epfl.gameboj;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Opcode;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GameBoyTest {

    private Bus connect(Cpu cpu, Ram ram) {
        RamController rc = new RamController(ram, 0);
        Bus b = new Bus();
        cpu.attachTo(b);
        rc.attachTo(b);
        return b;
    }

    @Disabled
    @Test
    void workRamIsProperlyMapped() {
        Bus b = new GameBoy(null).bus();
        for (int a = 0; a <= 0xFFFF; ++a) {
            boolean inWorkRamOrEcho = (0xC000 <= a && a < 0xFE00);
            assertEquals(inWorkRamOrEcho ? 0 : 0xFF, b.read(a), String.format("at address 0x%04x", a));
        }
    }

    @Disabled
    @Test
    void workRamCanBeReadAndWritten() {
        Bus b = new GameBoy(null).bus();
        for (int a = 0xC000; a < 0xE000; ++a)
            b.write(a, (a ^ 0xA5) & 0xFF);
        for (int a = 0xC000; a < 0xE000; ++a)
            assertEquals((a ^ 0xA5) & 0xFF, b.read(a));
    }

    @Disabled
    @Test
    void echoAreaReflectsWorkRam() {
        Bus b = new GameBoy(null).bus();
        for (int a = 0xC000; a < 0xE000; ++a)
            b.write(a, (a ^ 0xA5) & 0xFF);
        for (int a = 0xE000; a < 0xFE00; ++a)
            assertEquals(((a - 0x2000) ^ 0xA5) & 0xFF, b.read(a));

        for (int a = 0xE000; a < 0xFE00; ++a)
            b.write(a, (a ^ 0xA5) & 0xFF);
        for (int a = 0xC000; a < 0xDE00; ++a)
            assertEquals(((a + 0x2000) ^ 0xA5) & 0xFF, b.read(a));
    }

    // Tests étape 5
    @Disabled
    @Test void runUntilWorks() {
        GameBoy g = new GameBoy(null);
        Cpu c = g.cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        b.write(0, Opcode.NOP.encoding);
        b.write(1, Opcode.NOP.encoding);
        g.runUntil(2);
        assertEquals(2, g.cycles());
    }

}
