package ch.epfl.gameboj.bits;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class S7BitsVectorTest {

    @Test
    void constructorFailsForInvalidSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new BitVector(-1, true));
        assertThrows(IllegalArgumentException.class,
                () -> new BitVector(30, false));
        assertThrows(IllegalArgumentException.class,
                () -> new BitVector(-231, true));
    }

    @Test
    void constructorWorksOnNonTrivialBitVector() {
        int[] vector1 = new int[] { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF };
        BitVector v1 = new BitVector(96, true);
        assertArrayEquals(vector1, v1.getVector());

        int[] vector2 = new int[] {0x0, 0x0};
        BitVector v2 = new BitVector(64);
        assertArrayEquals(vector2, v2.getVector());
    }

    @Test
    void sizeWorksOnNonTrivialBitVector() {
        BitVector v1 = new BitVector(128, false);
        assertEquals(128, v1.size());
        BitVector v2 = new BitVector(96, true);
        assertEquals(96, v2.size());
        BitVector v3 = new BitVector(32);
        assertEquals(32, v3.size());
    }

    @Test
    void setByteWorksOnNonTrivialBitVector() {
        BitVector v1 = new BitVector.Builder(32)
                .setByte(0, 0b1111_0000)
                .setByte(1, 0b1010_1010)
                .setByte(3, 0b1100_1100)
                .build();
        int[] vector1 = new int[] { 0b1100_1100_0000_0000_1010_1010_1111_0000 };
        assertArrayEquals(vector1, v1.getVector());

        BitVector v2 = new BitVector.Builder(64)
                .setByte(0, 0b1101_0011)
                .setByte(2, 0b0110_1110)
                .setByte(3, 0b0100_1111)
                .setByte(5, 0b1010_1010)
                .setByte(6, 0b0001_0111)
                .setByte(7, 0b1100_0011)
                .build();
        int[] vector2 = new int[] { 0b0100_1111_0110_1110_0000_0000_1101_0011,
                                    0b1100_0011_0001_0111_1010_1010_0000_0000 };
        assertArrayEquals(vector2, v2.getVector());
    }

    @Test
    void testBitWorksOnNonTrivialBitVector() {
        BitVector v = new BitVector.Builder(64)
                .setByte(1, 0b010_0000)
                .setByte(4, 0b1101_1010)
                .build();
        assertTrue(v.testBit(13));
        assertFalse(v.testBit(4));
        assertTrue(v.testBit(33));
        assertFalse(v.testBit(37));
    }

    @Test
    void testBitFailsForInvalidIndex() {
        BitVector v = new BitVector(64);
        assertThrows(IllegalArgumentException.class, () -> v.testBit(64));
        assertThrows(IllegalArgumentException.class, () -> v.testBit(-5));
        assertThrows(IllegalArgumentException.class, () -> v.testBit(78));
        assertThrows(IllegalArgumentException.class, () -> v.testBit(-32));
    }

    @Test
    void extractZeroExtendedWorks() {
        BitVector v1 = new BitVector(32, true);
        BitVector v2 = v1.extractZeroExtended(-17, 32);
        int[] vector1 = new int[] { 0b1111_1111_1111_1110_0000_0000_0000_0000 };
        assertArrayEquals(vector1, v2.getVector());

        BitVector v3 = new BitVector(64, true);
        BitVector v4 = v3.extractZeroExtended(11, 64);
        int[] vector2 = new int[] { 0b1111_1111_1111_1111_1111_1111_1111_1111,
                                    0b0000_0000_0001_1111_1111_1111_1111_1111 };
        assertArrayEquals(vector2, v4.getVector());
    }

    @Test
    void extractZeroExtendedWorksWithIndexMultipleOf32() {
        BitVector v1 = new BitVector(32, true);
        BitVector v2 = v1.extractZeroExtended(32, 32);
        int[] vector1 = new int[] { 0b0 };
        assertArrayEquals(vector1, v2.getVector());

        BitVector v3 = new BitVector(96, true);
        BitVector v4 = v3.extractZeroExtended(-64, 96);
        int[] vector2 = new int[] { 0b0, 0b0, 0b1111_1111_1111_1111_1111_1111_1111_1111 };
        assertArrayEquals(vector2, v4.getVector());
    }

    @Test
    void extractWrappedWorks() {
        BitVector v1 = new BitVector(32, true);
        BitVector v2 = v1.extractZeroExtended(-17, 32);
        BitVector v3 = v2.extractWrapped(-6, 32);
        int[] vector1 = new int[] { 0b1111_1111_1000_0000_0000_0000_0011_1111 };
        assertArrayEquals(vector1, v3.getVector());

        BitVector v4 = new BitVector(128, true);
        BitVector v5 = v4.extractZeroExtended(93, 96);
        BitVector v6 = v5.extractWrapped(33, 64);
        int[] vector2 = new int[] { 0b0000_0000_0000_0000_0000_0000_0000_0011,
                                    0b1000_0000_0000_0000_0000_0000_0000_0000 };
        assertArrayEquals(vector2, v6.getVector());
    }

    @Test
    void extractWrappedWorksWithIndexMultipleOf32() {
        BitVector v1 = new BitVector(32, true);
        BitVector v2 = v1.extractWrapped(-32, 32);
        int[] vector1 = new int[] { 0b1111_1111_1111_1111_1111_1111_1111_1111 };
        assertArrayEquals(vector1, v2.getVector());

        BitVector v3 = new BitVector(96, true);
        BitVector v4 = v3.extractWrapped(64, 64);
        int[] vector2 = new int[] { 0b1111_1111_1111_1111_1111_1111_1111_1111,
                                    0b1111_1111_1111_1111_1111_1111_1111_1111 };
        assertArrayEquals(vector2, v4.getVector());
    }

    @Test void shiftLeftWorks() {
        BitVector v = new BitVector.Builder(32)
                .setByte(0, 0b1111_0000)
                .setByte(1, 0b1010_1010)
                .setByte(3, 0b1100_1100)
                .build();
        int[] vector = new int[] { 0b10000000000101010101111000000000 };
        assertArrayEquals(vector, (v.shift(5)).getVector());
    }

    @Test void shiftRightWorks() {
        BitVector v = new BitVector.Builder(64)
                .setByte(0, 0b1111_0000)
                .setByte(1, 0b1010_1010)
                .setByte(3, 0b1100_1100)
                .build();
        int[] vector = new int[] { 0b1100_1100_0000_0000_1010_1010_1111, 0b0 };
        assertArrayEquals(vector, (v.shift(-4)).getVector());
    }

    @Test
    void notWorks() {
        BitVector v1 = new BitVector.Builder(32)
                .setByte(0, 0b1100_0111)
                .setByte(2, 0b1010_1100)
                .setByte(3, 0b0001_0101)
                .build();
        int[] vector1 = new int[] { 0b1110_1010_0101_0011_1111_1111_0011_1000 };
        assertArrayEquals(vector1, v1.not().getVector());

        BitVector v2 = new BitVector.Builder(64)
                .setByte(1, 0b1010_0101)
                .setByte(3, 0b1111_0001)
                .setByte(5, 0b1100_0010)
                .setByte(6, 0b0100_1101)
                .build();
        int[] vector2 = new int[] { 0b0000_1110_1111_1111_0101_1010_1111_1111,
                                    0b1111_1111_1011_0010_0011_1101_1111_1111 };
        assertArrayEquals(vector2, v2.not().getVector());
    }

    @Test
    void andWorks() {
        BitVector v1 = new BitVector.Builder(64)
                .setByte(0, 0b1011_1000)
                .setByte(2, 0b1001_0100)
                .setByte(4, 0b1110_1110)
                .setByte(6, 0b0001_0010)
                .build();
        BitVector v2 = new BitVector.Builder(64)
                .setByte(1, 0b0011_1101)
                .setByte(3, 0b0011_1010)
                .setByte(4, 0b0101_1101)
                .setByte(6, 0b1100_0010)
                .build();
        int[] vector = new int[] { 0b0000_0000_0000_0000_0000_0000_0000_0000,
                                    0b0000_0000_0000_0010_0000_0000_0100_1100};
        assertArrayEquals(vector, v1.and(v2).getVector());
    }

    @Test
    void orWorks() {
        BitVector v1 = new BitVector.Builder(64)
                .setByte(0, 0b1011_1000)
                .setByte(2, 0b1001_0100)
                .setByte(4, 0b1110_1110)
                .setByte(6, 0b0001_0010)
                .build();
        BitVector v2 = new BitVector.Builder(64)
                .setByte(1, 0b0011_1101)
                .setByte(3, 0b0011_1010)
                .setByte(4, 0b0101_1101)
                .setByte(6, 0b1100_0010)
                .build();
        int[] vector = new int[] { 0b0011_1010_1001_0100_0011_1101_1011_1000,
                                    0b0000_0000_1101_0010_0000_0000_1111_1111 };
        assertArrayEquals(vector, v1.or(v2).getVector());
    }

    @Test
    void equalsWorks() {
        BitVector v1 = new BitVector.Builder(32)
                .setByte(0, 0b10101010)
                .setByte(1, 0b00011101)
                .setByte(2, 0b01010101)
                .setByte(3, 0b01101001)
                .build();

        BitVector v2 = new BitVector.Builder(32)
                .setByte(0, 0b10101010)
                .setByte(1, 0b00011101)
                .setByte(2, 0b01010101)
                .setByte(3, 0b01101001)
                .build();

        assertTrue(v1.equals(v2));
    }


    //=========================================================================
    // Test Matthieu et Andre

    @Test
    void extractZeroExtendedWorks2() {
        BitVector v1 = new BitVector(32, true);
        BitVector v2 = v1.extractZeroExtended(-17, 32);
        int[] vector = new int[] { 0b11111111111111100000000000000000 };
        assertArrayEquals(vector, v2.getVector());

        BitVector.Builder test1 = new BitVector.Builder(Integer.SIZE * 3)
                .setByte(0, 0b10101010)
                .setByte(1, 0b10101010)
                .setByte(2, 0b10101010)
                .setByte(3, 0b10101010)
                .setByte(4, 0b11111111)
                .setByte(5, 0b11111111)
                .setByte(6, 0b11111111)
                .setByte(7, 0b11111111);
        BitVector test = test1.build();

        int[] exp = {0xFFFFFAAA, 0x00000FFF};
        BitVector act = test.extractZeroExtended(20, Integer.SIZE * 2);
        System.out.println(test.toString());
        System.out.println(act.toString());
        assertArrayEquals(exp, act.getVector());

        int[] exp1 = {0, 0, 0, 0, 0, 0, 0, 0};
        BitVector act1 = new BitVector(Integer.SIZE * 8, false);
        assertArrayEquals(exp1, act1.extractZeroExtended(100, Integer.SIZE * 8).getVector());
    }

    @Test
    void extractWrappedWorks2() {
        BitVector.Builder test1 = new BitVector.Builder(Integer.SIZE * 3)
                .setByte(0, 0b10101010)
                .setByte(1, 0b10101010)
                .setByte(2, 0b10101010)
                .setByte(3, 0b10101010)
                .setByte(4, 0b11111111)
                .setByte(5, 0b11111111)
                .setByte(6, 0b11111111)
                .setByte(7, 0b11111111)
                .setByte(8, 0b11110000)
                .setByte(9, 0b11110000)
                .setByte(10, 0b11110000)
                .setByte(11, 0b11110000);
        BitVector test = test1.build();
        BitVector v3 = test.extractWrapped(-16, 32);
        int[] vector = new int[] { 0b10101010101010101111000011110000};
        assertArrayEquals(vector, v3.getVector());

        /*int[] exp1 = {0xAAAF0F0F, 0xFFFAAAAA, 0x0F0FFFFF};
        BitVector act1 = test.extractWrapped(20, Integer.SIZE * 3);
        assertArrayEquals(exp1, act1.getVector());*/
    }

    /*@Test
    void shiftWorks() {
        BitVector.Builder test1 = new BitVector.Builder(Integer.SIZE * 3);
        test1.set32(0, 0xAAAAAAAA);
        test1.set32(1, 0xFFFFFFFF);
        test1.set32(2, 0);
        BitVector test = test1.build();

        int[] exp = {0xAAA00000,0xFFFAAAAA,0x000FFFFF};
        BitVector act = test.shift(20);
        System.out.println(act.toString());

        int[] exp1 = {0xFFFFFAAA, 0x00000FFF, 0x0};
        BitVector act1 = test.shift(-20);
        System.out.println(act1.toString());

        assertArrayEquals(exp, act.getVector());
        assertArrayEquals(exp1, act1.getVector());
    }*/


    @Test
    void andWorksOnKnownValues() {
        BitVector.Builder test1 = new BitVector.Builder(Integer.SIZE * 3)
                .setByte(0, 0b10101010)
                .setByte(1, 0b10101010)
                .setByte(2, 0b10101010)
                .setByte(3, 0b10101010)
                .setByte(4, 0b11111111)
                .setByte(5, 0b11111111)
                .setByte(6, 0b11111111)
                .setByte(7, 0b11111111);;
        BitVector test2 = new BitVector(Integer.SIZE * 3, true);
        BitVector actual = test1.build().and(test2);
        int[] expected1 = {0xAAAAAAAA, 0xFFFFFFFF, 0};
        assertArrayEquals(expected1, actual.getVector());

        BitVector zero = new BitVector(Integer.SIZE * 8, true);
        BitVector one = new BitVector(Integer.SIZE * 8, false);
        int[] none = {0,0,0,0,0,0,0,0};
        assertArrayEquals(none, zero.and(one).getVector());

        assertThrows(IllegalArgumentException.class, () -> zero.and(test2));
    }

    @Test
    void orWorksOnKnownValues() {
        BitVector.Builder test1 = new BitVector.Builder(Integer.SIZE * 3)
                .setByte(0, 0b10101010)
                .setByte(1, 0b10101010)
                .setByte(2, 0b10101010)
                .setByte(3, 0b10101010)
                .setByte(4, 0b11111111)
                .setByte(5, 0b11111111)
                .setByte(6, 0b11111111)
                .setByte(7, 0b11111111);
        BitVector test2 = new BitVector(Integer.SIZE * 3, false);

        BitVector zero = new BitVector(Integer.SIZE * 8, true);
        BitVector one = new BitVector(Integer.SIZE * 8, false);

        int[] expected1 = {0xAAAAAAAA, 0xFFFFFFFF, 0x0};
        assertArrayEquals(expected1, test1.build().or(test2).getVector());

        int[] all = {0xFFFFFFFF,0xFFFFFFFF ,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF};
        assertArrayEquals(all, zero.or(one).getVector());

        assertThrows(IllegalArgumentException.class, () -> zero.or(test2));
    }

    @Test
    void notWorksOnKnownValues() {
        BitVector.Builder test1 = new BitVector.Builder(Integer.SIZE * 3)
                .setByte(0, 0b10101010)
                .setByte(1, 0b10101010)
                .setByte(2, 0b10101010)
                .setByte(3, 0b10101010)
                .setByte(4, 0b11111111)
                .setByte(5, 0b11111111)
                .setByte(6, 0b11111111)
                .setByte(7, 0b11111111);
        BitVector test = test1.build();
        System.out.println(test.toString());
        System.out.println(test.not());
        System.out.println(Integer.toBinaryString(test.not().getVector()[2]));

        BitVector zero = new BitVector(Integer.SIZE * 8, false);
        BitVector one = new BitVector(Integer.SIZE * 8, true);

        int[] expected1 = {0b01010101010101010101010101010101, 0x0, 0xFFFFFFFF};
        assertEquals(expected1[0], test.not().getVector()[0]);
        assertEquals(expected1[1], test.not().getVector()[1]);
        assertEquals(expected1[2], test.not().getVector()[2]);

        int[] all = {0xFFFFFFFF,0xFFFFFFFF ,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF};
        assertArrayEquals(all, zero.not().getVector());


        int[] none = {0,0,0,0,0,0,0,0};
        assertArrayEquals(one.not().getVector(), none);
    }

    @Test
    void bitVectorBuildsWell() {
        BitVector test = new BitVector(256);
        BitVector test2 = new BitVector(256, true);

        int[] expected0 = {0,0,0,0,0,0,0,0};
        int[] expected1 = {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF};

        assertArrayEquals(expected0, test.getVector());
        assertArrayEquals(expected1, test2.getVector());
    }
}
