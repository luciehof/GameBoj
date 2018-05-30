// Gameboj stage 6

package ch.epfl.gameboj.component.cartridge;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

public final class MBC0Test {
    @Test
    void constructorFailsWithNullRom() {
        assertThrows(NullPointerException.class, () -> {
            new MBC0(null);
        });
    }

    @Test
    void constructorFailsWithRomOfInvalidSize() {
        for (int err: new int[] { -1, 1 }) {
            assertThrows(IllegalArgumentException.class, () -> {
                new MBC0(new Rom(new byte[0x8000 + err]));
            });
        }
    }
    
    @Test
    void readCanReadWholeRom() {
        byte[] romData = randomRomData();
        Component mbc = new MBC0(new Rom(romData));
        for (int a = 0; a < 0x8000; ++a)
            assertEquals(Byte.toUnsignedInt(romData[a]), mbc.read(a));
    }

    @Test
    void readOutsideOfRomReturnNoData() {
        Component mbc = new MBC0(randomRom());
        for (int a = 0x8000; a <= 0xFFFF; ++a)
            assertEquals(0x100, mbc.read(a));
    }
    
    @Test
    void readFailsForInvalidAddress() {
        Random rng = newRandom();
        Component c = new MBC0(randomRom());
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int a0 = rng.nextInt();
            while (0 <= a0 && a0 <= 0xFFFF)
                a0 += 0xFFFF;
            int a = a0;
            assertThrows(IllegalArgumentException.class,
                    () -> c.read(a));
        }
    }

    @Test
    void arbitraryWritesDoNotFail() {
        Component mbc = new MBC0(randomRom());
        for (int a = 0; a <= 0xFFFF; ++a)
            mbc.write(a, a & 0xFF);
    }
    
    static Rom randomRom() {
        return new Rom(randomRomData());
    }

    private static byte[] randomRomData() {
        Random rng = newRandom();
        byte[] romData = new byte[0x8000];
        rng.nextBytes(romData);
        return romData;
    }
}
