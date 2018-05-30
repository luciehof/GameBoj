// Gameboj stage 6

package ch.epfl.gameboj.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import static ch.epfl.test.TestRandomizer.*;
import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.component.cpu.Cpu;

public final class TimerTest implements ComponentTest {
    @Override
    public Timer newComponent() {
        return new Timer(new Cpu());
    }

    @Test
    void constructorFailsWhenCpuIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Timer(null);
        });
    }
    
    @Test
    void timerRegistersAreInitially0() {
        Timer t = newComponent();
        assertEquals(0, t.read(0xFF04));
        assertEquals(0, t.read(0xFF05));
        assertEquals(0, t.read(0xFF06));
        assertEquals(0, t.read(0xFF07));
    }
    
    @Test
    void timaCanBeWrittenAndRead() {
        Timer t = newComponent();
        for (int tima = 0; tima <= 0xFF; ++tima) {
            t.write(0xFF05, tima);
            assertEquals(tima, t.read(0xFF05));
        }
    }
    
    @Test
    void tmaCanBeWrittenAndRead() {
        Timer t = newComponent();
        for (int tma = 0; tma <= 0xFF; ++tma) {
            t.write(0xFF06, tma);
            assertEquals(tma, t.read(0xFF06));
        }
    }
    
    @Test
    void tacCanBeWrittenAndRead() {
        Timer t = newComponent();
        for (int tac = 0; tac <= 0b111; ++tac) {
            t.write(0xFF07, tac);
            assertEquals(tac, t.read(0xFF07));
        }
    }
    
    @Test
    void cycleProperlyIncrementsMainCounter() {
        Timer t = newComponent();
        for (int c = 0; c <= 0xFFFF; ++c) {
            assertEquals((c >> 6) & 0xFF, t.read(0xFF04));
            t.cycle(c);
        }
    }
    
    @Test
    void mainCounterGetsResetByAnyWriteToDIV() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            Timer t = newComponent();
            t.write(0xFF04, 0);
            for (int c = 0; c <= 0x3F; ++c)
                t.cycle(c);
            assertEquals(1, t.read(0xFF04));
            t.write(0xFF04, rng.nextInt(0x100));
            assertEquals(0, t.read(0xFF04));
        }
    }
    
    @Test
    void secondaryCounterDoesNotChangeWhenDisabled() {
        Timer t = newComponent();
        t.write(0xFF07, 0);
        for (int c = 0; c < 2027; ++c) {
            assertEquals(0, t.read(0xFF05));
            t.cycle(c);
        }
    }
    
    @Test
    void secondaryCounterIncrementsProperlyWhenTacIs0() {
        Timer t = newComponent();
        t.write(0xFF07, 0b100);
        for (int c = 0; c < 2027; ++c) {
            assertEquals((c >> 8) & 0xFF, t.read(0xFF05));
            t.cycle(c);
        }
    }
    
    @Test
    void secondaryCounterIncrementsProperlyWhenTacIs1() {
        Timer t = newComponent();
        t.write(0xFF07, 0b101);
        for (int c = 0; c < 2027; ++c) {
            assertEquals((c >> 2) & 0xFF, t.read(0xFF05));
            t.cycle(c);
        }
    }
    
    @Test
    void secondaryCounterIncrementsProperlyWhenTacIs2() {
        Timer t = newComponent();
        t.write(0xFF07, 0b110);
        for (int c = 0; c < 2027; ++c) {
            assertEquals((c >> 4) & 0xFF, t.read(0xFF05));
            t.cycle(c);
        }
    }
    
    @Test
    void secondaryCounterIncrementsProperlyWhenTacIs3() {
        Timer t = newComponent();
        t.write(0xFF07, 0b111);
        for (int c = 0; c < 2027; ++c) {
            assertEquals((c >> 6) & 0xFF, t.read(0xFF05));
            t.cycle(c);
        }
    }
    
    @Test
    void secondaryCounterIncrementsWhenGettingDisabledAndStateIs1() {
        Timer t = newComponent();
        t.write(0xFF07, 0b101);
        t.cycle(0); // counter == 4, state == 0
        t.cycle(1); // counter == 4, state == 1
        assertEquals(0, t.read(0xFF05));
        t.write(0xFF07, 0b000); // state == 0, so TIMA is incremented
        assertEquals(1, t.read(0xFF05));
    }
    
    @Test
    void secondaryCounterGetsResetToTMAOnOverflow() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int tma = rng.nextInt(0x100);
            Timer t = newComponent();
            t.write(0xFF07, 0b101);
            t.write(0xFF06, tma);
            for (int c = 0; c <= 0x400; ++c)
                t.cycle(c);
            assertEquals(t.read(0xFF05), tma);
        }
    }
}
