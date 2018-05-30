package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.bits.BitVector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8LcdImageTest {

    @Test
    void getWorks() {
        BitVector msb1 = new BitVector(32);
        BitVector lsb1 = new BitVector(32, true).shift(16);
        BitVector opacity = new BitVector(32, true);
        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity);

        BitVector msb2 = new BitVector(32, true).shift(-16);
        BitVector lsb2 = new BitVector(32);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity);

        List<LcdImageLine> list = new ArrayList<>();
        list.add(l1);
        list.add(l2);

        LcdImage lcdImage = new LcdImage(32, 2, list);

        assertEquals(1, lcdImage.get(16, 0));
    }

    @Test
    void equalsAndHashCodeWorks() {
        BitVector msb1 = new BitVector(32);
        BitVector lsb1 = new BitVector(32, true).shift(16);
        BitVector opacity1 = new BitVector(32, true);
        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity1);
        BitVector msb2 = new BitVector(32, true).shift(-16);
        BitVector lsb2 = new BitVector(32);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity1);
        List<LcdImageLine> list1 = new ArrayList<>();
        list1.add(l1);
        list1.add(l2);

        LcdImage lcdImage1 = new LcdImage(32, 2, list1);


        BitVector msb3 = new BitVector(32);
        BitVector lsb3 = new BitVector(32, true).shift(16);
        BitVector opacity2 = new BitVector(32, true);
        LcdImageLine l3 = new LcdImageLine(msb3, lsb3, opacity2);
        BitVector msb4 = new BitVector(32, true).shift(-16);
        BitVector lsb4 = new BitVector(32);
        LcdImageLine l4 = new LcdImageLine(msb4, lsb4, opacity2);
        List<LcdImageLine> list2 = new ArrayList<>();
        list2.add(l3);
        list2.add(l4);

        LcdImage lcdImage2 = new LcdImage(32, 2, list2);

        assertTrue(lcdImage1.equals(lcdImage2));
        assertTrue(lcdImage1.hashCode() == lcdImage2.hashCode());
    }

    @Test
    void setLineWorks() {

    }

    /*@Test
    void mapColorWorks() {
        int palette1 = 0b11_01_10_00;
        BitVector msb = new BitVector.Builder(32)
                .setByte(0, 0b00101110)
                .build();
        BitVector lsb = new BitVector.Builder(32)
                .setByte(0, 0b01101101)
                .build();
        BitVector opacity = new BitVector(32, true);
        LcdImageLine l1 = new LcdImageLine(msb, lsb, opacity);

        BitVector expMsb = new BitVector.Builder(32)
                .setByte(0, 0b01101101)
                .build();
        BitVector expLsb = new BitVector.Builder(32)
                .setByte(0, 0b00101110)
                .build();
        //LcdImageLine expected = new LcdImageLine(expMsb, expLsb, opacity);

        LcdImageLine newL1 = l1.mapColors(palette1);
        assertArrayEquals(expMsb.getVector(), newL1.getMsb().getVector());
        //assertTrue(expMsb.equals(newL1.getMsb()));
        //assertTrue(expLsb.equals(newL1.getLsb()));
        //assertTrue(expected.equals(l1.mapColors(palette)));

        int palette2 = 0b11_10_00_01;
        BitVector msb = new BitVector.Builder(32)
                .setByte(0, 0b00101110)
                .build();
        BitVector lsb = new BitVector.Builder(32)
                .setByte(0, 0b01101101)
                .build();
        BitVector opacity = new BitVector(32, true);
        LcdImageLine l1 = new LcdImageLine(msb, lsb, opacity);

        BitVector expMsb = new BitVector.Builder(32)
                .setByte(0, 0b00101110)
                .build();
        BitVector expLsb = new BitVector.Builder(32)
                .setByte(0, 0b10111100)
                .build();

        LcdImageLine newL1 = l1.mapColors(palette2);
        assertArrayEquals(expMsb.getVector(), newL1.getMsb().getVector());
        assertArrayEquals(expLsb.getVector(), newL1.getLsb().getVector());
    }
    */

    @Test
    void equalsWorks() {
        BitVector msb1 = new BitVector(32);
        BitVector lsb1 = new BitVector(32, true);
        BitVector opacity1 = new BitVector(32, true);
        BitVector msb2 = new BitVector(32);
        BitVector lsb2 = new BitVector(32, true);
        BitVector opacity2 = new BitVector(32, true);

        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity1);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity2);

        assertTrue(l1.equals(l2));
    }

    //==========================================================================
    //==========================================================================
    // Tests Nizou

    public static BitVector v0 = new BitVector(32, false);
    public static BitVector v1 = new BitVector(32, true);
    // 11001100000000001010101011110000
    public static BitVector v3 = new BitVector.Builder(32).setByte(0, 0b1111_0000).setByte(1, 0b1010_1010).setByte(3, 0b1100_1100).build();
    // 00000000110101011111111000000000
    public static BitVector v4 = new BitVector.Builder(32).setByte(0, 0b0000_0000).setByte(1, 0b1111_1110).setByte(2, 0b1101_0101).build();
    public static BitVector v5 = new BitVector(64);


    // TESTS CONSTRUCTEUR / GETSIZE / GETMSB / GETLSB / GETOPACITY
    // Cqs Normal
    @Test
    public void constructeurGetTestNormal() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        // GETSIZE
        assertEquals(32, l0.size());
        assertEquals(32, l1.size());
        assertEquals(32, l2.size());
        assertEquals(32, l3.size());
        assertEquals(32, l4.size());
        assertEquals(32, l5.size());
        assertEquals(32, l6.size());
        assertEquals(32, l7.size());

        // GETMSB
        assertEquals(v0, l0.getMsb());
        assertEquals(v1, l1.getMsb());
        assertEquals(v1, l2.getMsb());
        assertEquals(v0, l3.getMsb());
        assertEquals(v3, l4.getMsb());
        assertEquals(v4, l5.getMsb());
        assertEquals(v3, l6.getMsb());
        assertEquals(v4, l7.getMsb());

        // GETLSB
        assertEquals(v0, l0.getLsb());
        assertEquals(v1, l1.getLsb());
        assertEquals(v0, l2.getLsb());
        assertEquals(v1, l3.getLsb());
        assertEquals(v3, l4.getLsb());
        assertEquals(v4, l5.getLsb());
        assertEquals(v4, l6.getLsb());
        assertEquals(v3, l7.getLsb());

        // GETOPACITY
        assertEquals(v0, l0.getOpacity());
        assertEquals(v1, l1.getOpacity());
        assertEquals(v1, l2.getOpacity());
        assertEquals(v0, l3.getOpacity());
        assertEquals(v3, l4.getOpacity());
        assertEquals(v4, l5.getOpacity());
        assertEquals(v3, l6.getOpacity());
        assertEquals(v4, l7.getOpacity());
    }

    // Cas d'erreur
    @Test
    public void constructeurTestError() {
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0 = new LcdImageLine(v5, v0, v1);});
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0 = new LcdImageLine(v4, v5, v3);});
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0 = new LcdImageLine(v4, v0, v5);});
    }


    // TESTS SHIFT
    @Test
    public void shiftTestNormal() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        l0 = l0.shift(4);
        l1 = l1.shift(-6);
        l2 = l2.shift(10);
        l3 = l3.shift(-9);
        l4 = l4.shift(1);
        l5 = l5.shift(-1);
        l6 = l6.shift(32);
        l7 = l7.shift(-32);

        assertEquals("00000000000000000000000000000000", l0.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l0.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l0.getOpacity().toString());

        assertEquals("00000011111111111111111111111111", l1.getMsb().toString());
        assertEquals("00000011111111111111111111111111", l1.getLsb().toString());
        assertEquals("00000011111111111111111111111111", l1.getOpacity().toString());

        assertEquals("11111111111111111111110000000000", l2.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l2.getLsb().toString());
        assertEquals("11111111111111111111110000000000", l2.getOpacity().toString());

        assertEquals("00000000000000000000000000000000", l3.getMsb().toString());
        assertEquals("00000000011111111111111111111111", l3.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l3.getOpacity().toString());

        assertEquals("10011000000000010101010111100000", l4.getMsb().toString());
        assertEquals("10011000000000010101010111100000", l4.getLsb().toString());
        assertEquals("10011000000000010101010111100000", l4.getOpacity().toString());

        assertEquals("00000000011010101111111100000000", l5.getMsb().toString());
        assertEquals("00000000011010101111111100000000", l5.getLsb().toString());
        assertEquals("00000000011010101111111100000000", l5.getOpacity().toString());

        assertEquals("00000000000000000000000000000000", l6.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l6.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l6.getOpacity().toString());

        assertEquals("00000000000000000000000000000000", l7.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l7.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l7.getOpacity().toString());
    }

    // TESTS EXTRACTWRAPPED
    // Cas normal
    @Test
    public void extractWrappedNormalTest() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        LcdImageLine l0a = l0.extractWrapped(-20, 64);
        LcdImageLine l1a = l1.extractWrapped(-54, 32);
        l0 = l0.extractWrapped(7, 32);
        l1 = l1.extractWrapped(9, 64);
        l2 = l2.extractWrapped(-3, 32);
        l3 = l3.extractWrapped(3, 32);
        l4 = l4.extractWrapped(-5, 32);
        l5 = l5.extractWrapped(5, 32);
        l6 = l6.extractWrapped(-32, 32);
        l7 = l7.extractWrapped(0, 32);

        assertEquals("00000000000000000000000000000000", l0.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l0.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l0.getOpacity().toString());

        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", l0a.getMsb().toString());
        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", l0a.getLsb().toString());
        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", l0a.getOpacity().toString());

        assertEquals("1111111111111111111111111111111111111111111111111111111111111111", l1.getMsb().toString());
        assertEquals("1111111111111111111111111111111111111111111111111111111111111111", l1.getLsb().toString());
        assertEquals("1111111111111111111111111111111111111111111111111111111111111111", l1.getOpacity().toString());

        assertEquals("11111111111111111111111111111111", l1a.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l1a.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l1a.getOpacity().toString());

        assertEquals("11111111111111111111111111111111", l2.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l2.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l2.getOpacity().toString());

        assertEquals("00000000000000000000000000000000", l3.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l3.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l3.getOpacity().toString());

        assertEquals("10000000000101010101111000011001", l4.getMsb().toString());
        assertEquals("10000000000101010101111000011001", l4.getLsb().toString());
        assertEquals("10000000000101010101111000011001", l4.getOpacity().toString());

        assertEquals("00000000000001101010111111110000", l5.getMsb().toString());
        assertEquals("00000000000001101010111111110000", l5.getLsb().toString());
        assertEquals("00000000000001101010111111110000", l5.getOpacity().toString());

        assertEquals("11001100000000001010101011110000", l6.getMsb().toString());
        assertEquals("00000000110101011111111000000000", l6.getLsb().toString());
        assertEquals("11001100000000001010101011110000", l6.getOpacity().toString());

        assertEquals("00000000110101011111111000000000", l7.getMsb().toString());
        assertEquals("11001100000000001010101011110000", l7.getLsb().toString());
        assertEquals("00000000110101011111111000000000", l7.getOpacity().toString());
    }

    // Cas d'erreur
    @Test
    public void extractWrappedErrorTest() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0a = l0.extractWrapped(7, 34);});
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0a = l0.extractWrapped(7, 31);});
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0a = l0.extractWrapped(7, 33);});
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0a = l0.extractWrapped(7, -1);});
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0a = l0.extractWrapped(7, 1);});
        assertThrows(IllegalArgumentException.class,() -> {LcdImageLine l0a = l0.extractWrapped(7, 438);});
    }


    // TESTS MAPCOLORS
    // Cas normal
    @Test
    public void mapColorsNormalTest() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        // 0b11100100

        l0 = l0.mapColors(0b00110110);
        l1 = l1.mapColors(0b01101100);
        l2 = l2.mapColors(0b11000110);
        l3 = l3.mapColors(0b10001110);
        l4 = l4.mapColors(0b10110001);
        l5 = l5.mapColors(0b00011011);
        l6 = l6.mapColors(0b11011000);
        l7 = l7.mapColors(0b11001001);

        // v3 : 11001100000000001010101011110000
        // v4 : 00000000110101011111111000000000

        assertEquals("11111111111111111111111111111111", l0.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l0.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l0.getOpacity().toString());

        assertEquals("00000000000000000000000000000000", l1.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l1.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l1.getOpacity().toString());

        assertEquals("00000000000000000000000000000000", l2.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l2.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l2.getOpacity().toString());

        assertEquals("11111111111111111111111111111111", l3.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l3.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l3.getOpacity().toString());

        assertEquals("11001100000000001010101011110000", l4.getMsb().toString());
        assertEquals("00110011111111110101010100001111", l4.getLsb().toString());
        assertEquals("11001100000000001010101011110000", l4.getOpacity().toString());

        assertEquals("11111111001010100000000111111111", l5.getMsb().toString());
        assertEquals("11111111001010100000000111111111", l5.getLsb().toString());
        assertEquals("00000000110101011111111000000000", l5.getOpacity().toString());

        assertEquals("00000000110101011111111000000000", l6.getMsb().toString());
        assertEquals("11001100000000001010101011110000", l6.getLsb().toString());
        assertEquals("11001100000000001010101011110000", l6.getOpacity().toString());

        assertEquals("11001100000000001010101011110000", l7.getMsb().toString());
        assertEquals("00110011001010101010101100001111", l7.getLsb().toString());
        assertEquals("00000000110101011111111000000000", l7.getOpacity().toString());
    }

    // Cas d'erreur
    @Test
    public void builderErrorTest() {
        LcdImageLine.Builder b0 = new LcdImageLine.Builder(32);
        LcdImageLine.Builder b1 = new LcdImageLine.Builder(32);
        LcdImageLine.Builder b2 = new LcdImageLine.Builder(32);
        LcdImageLine.Builder b3 = new LcdImageLine.Builder(32);
        LcdImageLine.Builder b4 = new LcdImageLine.Builder(32);
        LcdImageLine.Builder b5 = new LcdImageLine.Builder(32);
        LcdImageLine.Builder b6 = new LcdImageLine.Builder(32);
        LcdImageLine.Builder b7 = new LcdImageLine.Builder(32);

        LcdImageLine l0a = b0.setBytes(0, 0, 0).setBytes(1, 0, 0).setBytes(2, 0, 0).setBytes(3, 0, 0).build();
        LcdImageLine l1a = b1.setBytes(0, 0b11111111, 0b11111111).setBytes(1, 0b11111111, 0b11111111).setBytes(2, 0b11111111, 0b11111111).setBytes(3, 0b11111111, 0b11111111).build();
        LcdImageLine l2a = b2.setBytes(0, 0b11111111, 0).setBytes(1, 0b11111111, 0).setBytes(2, 0b11111111, 0).setBytes(3, 0b11111111, 0).build();
        LcdImageLine l3a = b3.setBytes(0, 0, 0b11111111).setBytes(1, 0, 0b11111111).setBytes(2, 0, 0b11111111).setBytes(3, 0, 0b11111111).build();
        LcdImageLine l4a = b4.setBytes(0, 0b11110000, 0b11110000).setBytes(1, 0b10101010, 0b10101010).setBytes(2, 0b00000000, 0b00000000).setBytes(3, 0b11001100, 0b11001100).build();
        LcdImageLine l5a = b5.setBytes(0, 0, 0).setBytes(1, 0b11111110, 0b11111110).setBytes(2, 0b11010101, 0b11010101).setBytes(3, 0, 0).build();
        LcdImageLine l6a = b6.setBytes(0, 0b11110000, 0).setBytes(1, 0b10101010, 0b11111110).setBytes(2, 0, 0b11010101).setBytes(3, 0b11001100, 0).build();
        LcdImageLine l7a = b7.setBytes(0, 0, 0b11110000).setBytes(1, 0b11111110, 0b10101010).setBytes(2, 0b11010101, 0).setBytes(3, 0, 0b11001100).build();

        assertThrows(IllegalStateException.class, () -> b0.build());
        assertThrows(IllegalStateException.class, () -> b1.build());
        assertThrows(IllegalStateException.class, () -> b2.build());
        assertThrows(IllegalStateException.class, () -> b3.build());
        assertThrows(IllegalStateException.class, () -> b4.build());
        assertThrows(IllegalStateException.class, () -> b5.build());
        assertThrows(IllegalStateException.class, () -> b6.build());
        assertThrows(IllegalStateException.class, () -> b7.build());


        assertThrows(IllegalStateException.class, () -> b0.setBytes(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> b1.setBytes(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> b2.setBytes(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> b3.setBytes(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> b4.setBytes(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> b5.setBytes(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> b6.setBytes(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> b7.setBytes(1, 0, 0));

    }


    // TESTS BELOW1
    // Cas normal
    @Test
    public void below1NormalTest() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        // 11001100000000001010101011110000
        // 00000000110101011111111000000000

        LcdImageLine l0a = l0.below(l1, v0);
        LcdImageLine l0b = l0.below(l1, v1);
        LcdImageLine l0c = l0.below(l1, v3);
        LcdImageLine l0d = l0.below(l1, v4);

        LcdImageLine l1a = l1.below(l2, v0);
        LcdImageLine l1b = l1.below(l2, v1);
        LcdImageLine l1c = l1.below(l2, v3);
        LcdImageLine l1d = l1.below(l2, v4);

        LcdImageLine l2a = l2.below(l3, v0);
        LcdImageLine l2b = l2.below(l3, v1);
        LcdImageLine l2c = l2.below(l3, v3);
        LcdImageLine l2d = l2.below(l3, v4);

        LcdImageLine l3a = l3.below(l4, v0);
        LcdImageLine l3b = l3.below(l4, v1);
        LcdImageLine l3c = l3.below(l4, v3);
        LcdImageLine l3d = l3.below(l4, v4);

        LcdImageLine l4a = l4.below(l5, v0);
        LcdImageLine l4b = l4.below(l5, v1);
        LcdImageLine l4c = l4.below(l5, v3);
        LcdImageLine l4d = l4.below(l5, v4);

        LcdImageLine l5a = l5.below(l6, v0);
        LcdImageLine l5b = l5.below(l6, v1);
        LcdImageLine l5c = l5.below(l6, v3);
        LcdImageLine l5d = l5.below(l6, v4);

        LcdImageLine l6a = l6.below(l7, v0);
        LcdImageLine l6b = l6.below(l7, v1);
        LcdImageLine l6c = l6.below(l7, v3);
        LcdImageLine l6d = l6.below(l7, v4);

        assertEquals("00000000000000000000000000000000", l0a.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l0a.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l0a.getOpacity().toString());
        assertEquals("11111111111111111111111111111111", l0b.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l0b.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l0b.getOpacity().toString());
        assertEquals("11001100000000001010101011110000", l0c.getMsb().toString());
        assertEquals("11001100000000001010101011110000", l0c.getLsb().toString());
        assertEquals("00000000110101011111111000000000", l0d.getMsb().toString());
        assertEquals("00000000110101011111111000000000", l0d.getLsb().toString());
        assertEquals("00000000110101011111111000000000", l0d.getOpacity().toString());

        assertEquals("11111111111111111111111111111111", l1a.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l1a.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l1a.getOpacity().toString());
        assertEquals("11111111111111111111111111111111", l1b.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l1b.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l1b.getOpacity().toString());
        assertEquals("11111111111111111111111111111111", l1c.getMsb().toString());
        assertEquals("00110011111111110101010100001111", l1c.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l1c.getOpacity().toString());
        assertEquals("11111111111111111111111111111111", l1d.getMsb().toString());
        assertEquals("11111111001010100000000111111111", l1d.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l1d.getOpacity().toString());

        assertEquals("11111111111111111111111111111111", l2a.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l2a.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l2a.getOpacity().toString());
        assertEquals("00000000000000000000000000000000", l2b.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l2b.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l2b.getOpacity().toString());
        assertEquals("00110011111111110101010100001111", l2c.getMsb().toString());
        assertEquals("11001100000000001010101011110000", l2c.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l2c.getOpacity().toString());
        assertEquals("11111111001010100000000111111111", l2d.getMsb().toString());
        assertEquals("00000000110101011111111000000000", l2d.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l2d.getOpacity().toString());

        assertEquals("00000000000000000000000000000000", l3a.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l3a.getLsb().toString());
        assertEquals("00000000000000000000000000000000", l3a.getOpacity().toString());
        assertEquals("11001100000000001010101011110000", l3b.getMsb().toString());
        assertEquals("11001100000000001010101011110000", l3b.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l3b.getOpacity().toString());
        assertEquals("11001100000000001010101011110000", l3c.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l3c.getLsb().toString());
        assertEquals("11001100000000001010101011110000", l3c.getOpacity().toString());
        assertEquals("00000000000000001010101000000000", l3d.getMsb().toString());
        assertEquals("11111111001010101010101111111111", l3d.getLsb().toString());
        assertEquals("00000000110101011111111000000000", l3d.getOpacity().toString());

        assertEquals("11001100000000001010101011110000", l4a.getMsb().toString());
        assertEquals("11001100000000001010101011110000", l4a.getLsb().toString());
        assertEquals("11001100000000001010101011110000", l4a.getOpacity().toString());
        assertEquals("00000000110101011111111000000000", l4b.getMsb().toString());
        assertEquals("00000000110101011111111000000000", l4b.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l4b.getOpacity().toString());
        assertEquals("00000000000000001010101000000000", l4c.getMsb().toString());
        assertEquals("00000000000000001010101000000000", l4c.getLsb().toString());
        assertEquals("11001100000000001010101011110000", l4c.getOpacity().toString());
        assertEquals("11001100110101011111111011110000", l4d.getMsb().toString());
        assertEquals("11001100110101011111111011110000", l4d.getLsb().toString());
        assertEquals(v3.or(v4).toString(), l4d.getOpacity().toString());
    }


    // TESTS BELOW2
    // Cas normal
    @Test
    public void below2NormalTest() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        l0 = l0.below(l1);
        l1 = l1.below(l2);
        l2 = l2.below(l3);
        l3 = l3.below(l4);
        l4 = l4.below(l5);
        l5 = l5.below(l6);
        l6 = l6.below(l7);

        // 11001100000000001010101011110000
        // 00000000110101011111111000000000

        assertEquals("11111111111111111111111111111111", l0.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l0.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l0.getOpacity().toString());

        assertEquals("11111111111111111111111111111111", l1.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l1.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l1.getOpacity().toString());

        assertEquals("11111111111111111111111111111111", l2.getMsb().toString());
        assertEquals("00000000000000000000000000000000", l2.getLsb().toString());
        assertEquals("11111111111111111111111111111111", l2.getOpacity().toString());

        assertEquals("11001100000000001010101011110000", l3.getMsb().toString());
        assertEquals("11111111111111111111111111111111", l3.getLsb().toString());
        assertEquals(v3.toString(), l3.getOpacity().toString());

        assertEquals("11001100110101011111111011110000", l4.getMsb().toString());
        assertEquals("11001100110101011111111011110000", l4.getLsb().toString());
        assertEquals(v3.or(v4).toString(), l4.getOpacity().toString());

        assertEquals("11001100110101011111111011110000", l5.getMsb().toString());
        assertEquals("00000000110101011111111000000000", l5.getLsb().toString());
        assertEquals(v3.or(v4).toString(), l5.getOpacity().toString());

        assertEquals("11001100110101011111111011110000", l6.getMsb().toString());
        assertEquals("00000000000000001010101000000000", l6.getLsb().toString());
        assertEquals(v3.or(v4).toString(), l6.getOpacity().toString());
    }





    BitVector msb = new BitVector(32, true);
    BitVector lsb = new BitVector(32, false);
    BitVector opacity = new BitVector(32, true);
    LcdImageLine a = new LcdImageLine(msb, lsb, opacity);

    BitVector msbb = new BitVector(32, false);
    BitVector lsbb = new BitVector(32, false);
    BitVector opacityy = new BitVector.Builder(32).setByte(0, 0b1111_0000)
            .setByte(1, 0b1010_1010).setByte(3, 0b1100_1100).build();
    // 11001100000000001010101011110000
    LcdImageLine b = new LcdImageLine(msbb, lsbb, opacityy);

    BitVector msbbb = new BitVector.Builder(32).setByte(0, 0b1111_0000)
            .setByte(1, 0b1010_1010).setByte(3, 0b1100_1100).build();
    // 11001100000000001010101011110000
    BitVector lsbbb = new BitVector.Builder(32).setByte(0, 0b0000_0110)
            .setByte(1, 0b1010_1110).setByte(3, 0b1100_0000).build();
    // 11000000000000001010111000000110
    BitVector opacityyy = new BitVector(32, true);
    LcdImageLine c = new LcdImageLine(msbbb, lsbbb, opacityyy);

    BitVector msbbbb = new BitVector(32, false);
    BitVector lsbbbb = new BitVector(32, true);
    BitVector opacityyyy = new BitVector(32, false);
    LcdImageLine d = new LcdImageLine(msbbbb, lsbbbb, opacityyyy);

    @Test
    void shiftTest() {

        LcdImageLine c = a.shift(3);
        LcdImageLine d = a.shift(-5);
        assertEquals("11111111111111111111111111111000", c.getMsb().toString());
        assertEquals("00000000000000000000000000000000", c.getLsb().toString());
        assertEquals("11111111111111111111111111111000",
                c.getOpacity().toString());
        assertEquals("00000111111111111111111111111111", d.getMsb().toString());
        assertEquals("00000000000000000000000000000000", d.getLsb().toString());
        assertEquals("00000111111111111111111111111111",
                d.getOpacity().toString());

    }

    @Test
    void colorTest() {

        LcdImageLine v1 = c.mapColors(0b11100100);
        assertEquals(v1, c);
        LcdImageLine v2 = c.mapColors(0b11100001);
        System.out.println("msb " + v2.getMsb().toString());
        System.out.println("lsb " +v2.getLsb().toString());
        //            11001100000000001010101011110000
        assertEquals("11001100000000001010101011110000", v2.getMsb().toString());
        //            11000000000000001010111000000110
        assertEquals("11110011111111111111101100001001", v2.getLsb().toString());

    }


    @Test
    void colorTest2() {

        LcdImageLine v1 = c.mapColors(0b11100100);
        assertEquals(v1, c);
        LcdImageLine v2 = c.mapColors(0b01001110);
        //            11001100000000001010101011110000
        assertEquals("00110011111111110101010100001111", v2.getMsb().toString());
        //            11000000000000001010111000000110
        assertEquals("11000000000000001010111000000110", v2.getLsb().toString());

    }


    @Test
    void below1() {

        LcdImageLine v1 = a.below(b);

        assertEquals("00110011111111110101010100001111", v1.getMsb().toString());
        assertEquals("00000000000000000000000000000000", v1.getLsb().toString());
        assertEquals("11111111111111111111111111111111",
                v1.getOpacity().toString());
        // 11001100000000001010101011110000

    }

    @Test
    void below2() {
        BitVector z = new BitVector.Builder(32).setByte(0, 0b0000_0110)
                .setByte(1, 0b1010_1110).setByte(3, 0b1100_0000).build();
        // 11000000000000001010111000000110
        LcdImageLine v1 = a.below(b, z);

        assertEquals("00111111111111110101000111111001", v1.getMsb().toString());
        assertEquals("00000000000000000000000000000000", v1.getLsb().toString());
        assertEquals("11111111111111111111111111111111",
                v1.getOpacity().toString());
        // 11000000000000001010111000000110

    }

    @Test

    void join() {

        LcdImageLine v1 = a.join( d, 4);

        //System.out.println(a.opacity().toString());
        //System.out.print(d.opacity().toString());

        assertEquals("00000000000000000000000000001111", v1.getMsb().toString());
        assertEquals("11111111111111111111111111110000", v1.getLsb().toString());
        assertEquals("00000000000000000000000000001111", v1.getOpacity().toString());

    }


    @Test
    public void builder() {

        // 00000000110101011111111000000000 // lsb
        LcdImageLine v1 = new LcdImageLine.Builder(32)
                .setBytes(0, 0b0000_0000, 0b0000_0000)
                .setBytes(1, 0b0000_0000, 0b1111_1110)
                .setBytes(2, 0b0000_0000, 0b1101_0101).build();

        assertEquals("00000000110101011111111000000000", v1.getLsb().toString());
        assertEquals("00000000000000000000000000000000", v1.getMsb().toString());
        assertEquals("00000000110101011111111000000000",
                v1.getOpacity().toString());

        // 00000000000000000000000011000000 // lsb
        // 00000000110101011111111000000000 // msb
        LcdImageLine v2 = new LcdImageLine.Builder(32)
                .setBytes(0, 0b0000_0000,0b1100_0000).setBytes(1, 0b1111_1110,0b0000_0000)
                .setBytes(2, 0b1101_0101,0b0000_0000).build();

        assertEquals("00000000000000000000000011000000", v2.getLsb().toString());
        assertEquals("00000000110101011111111000000000", v2.getMsb().toString());
        assertEquals("00000000110101011111111011000000",
                v2.getOpacity().toString());

    }

    // TESTS EQUALS
    @Test
    public void equalsTest() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        LcdImageLine l0a = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1a = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2a = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3a = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4a = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5a = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6a = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7a = new LcdImageLine(v4, v3, v4);

        // 11001100000000001010101011110000
        // 00000000110101011111111000000000

        assertTrue(l0.equals(l0));
        assertTrue(l1.equals(l1));
        assertTrue(l2.equals(l2));
        assertTrue(l3.equals(l3));
        assertTrue(l4.equals(l4));
        assertTrue(l5.equals(l5));
        assertTrue(l6.equals(l6));
        assertTrue(l7.equals(l7));

        assertFalse(l0.equals(l7));
        assertFalse(l1.equals(l6));
        assertFalse(l2.equals(l5));
        assertFalse(l3.equals(l4));
        assertFalse(l4.equals(l3));
        assertFalse(l5.equals(l2));
        assertFalse(l6.equals(l1));
        assertFalse(l7.equals(l0));

        assertTrue(l0.equals(l0a));
        assertTrue(l1.equals(l1a));
        assertTrue(l2.equals(l2a));
        assertTrue(l3.equals(l3a));
        assertTrue(l4.equals(l4a));
        assertTrue(l5.equals(l5a));
        assertTrue(l6.equals(l6a));
        assertTrue(l7.equals(l7a));
    }

    // TESTS HASHCODE
    @Test
    public void hashCodeTest() {
        LcdImageLine l0 = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1 = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2 = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3 = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4 = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5 = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6 = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7 = new LcdImageLine(v4, v3, v4);

        LcdImageLine l0a = new LcdImageLine(v0, v0, v0);
        LcdImageLine l1a = new LcdImageLine(v1, v1, v1);
        LcdImageLine l2a = new LcdImageLine(v1, v0, v1);
        LcdImageLine l3a = new LcdImageLine(v0, v1, v0);
        LcdImageLine l4a = new LcdImageLine(v3, v3, v3);
        LcdImageLine l5a = new LcdImageLine(v4, v4, v4);
        LcdImageLine l6a = new LcdImageLine(v3, v4, v3);
        LcdImageLine l7a = new LcdImageLine(v4, v3, v4);

        assertEquals(l0.hashCode(), l0a.hashCode());
        assertEquals(l1.hashCode(), l1a.hashCode());
        assertEquals(l2.hashCode(), l2a.hashCode());
        assertEquals(l3.hashCode(), l3a.hashCode());
        assertEquals(l4.hashCode(), l4a.hashCode());
        assertEquals(l5.hashCode(), l5a.hashCode());
        assertEquals(l6.hashCode(), l6a.hashCode());
        assertEquals(l7.hashCode(), l7a.hashCode());

        assertFalse(l0.hashCode() == l7.hashCode());
        assertFalse(l1.hashCode() == l6.hashCode());
        assertFalse(l2.hashCode() == l5.hashCode());
        assertFalse(l3.hashCode() == l4.hashCode());
        assertFalse(l4.hashCode() == l3.hashCode());
        assertFalse(l5.hashCode() == l2.hashCode());
        assertFalse(l6.hashCode() == l1.hashCode());
        assertFalse(l7.hashCode() == l0.hashCode());

    }
    @Test
    void getWorksNizou() {
        BitVector msb1 = new BitVector(32);
        BitVector lsb1 = new BitVector(32, true).shift(16);
        BitVector opacity = new BitVector(32, true);
        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity);

        BitVector msb2 = new BitVector(32, true).shift(-16);
        BitVector lsb2 = new BitVector(32);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity);

        List<LcdImageLine> list = new ArrayList<>();
        list.add(l1);
        list.add(l2);

        LcdImage lcdImage = new LcdImage(2, 32, list);

        assertEquals(1, lcdImage.get(16, 0));
    }

    @Test
    void equalsAndHashCodeWorksNizou() {
        BitVector msb1 = new BitVector(32);
        BitVector lsb1 = new BitVector(32, true).shift(16);
        BitVector opacity1 = new BitVector(32, true);
        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity1);
        BitVector msb2 = new BitVector(32, true).shift(-16);
        BitVector lsb2 = new BitVector(32);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity1);
        List<LcdImageLine> list1 = new ArrayList<>();
        list1.add(l1);
        list1.add(l2);

        LcdImage lcdImage1 = new LcdImage(2, 32, list1);

        BitVector msb3 = new BitVector(32);
        BitVector lsb3 = new BitVector(32, true).shift(16);
        BitVector opacity2 = new BitVector(32, true);
        LcdImageLine l3 = new LcdImageLine(msb3, lsb3, opacity2);
        BitVector msb4 = new BitVector(32, true).shift(-16);
        BitVector lsb4 = new BitVector(32);
        LcdImageLine l4 = new LcdImageLine(msb4, lsb4, opacity2);
        List<LcdImageLine> list2 = new ArrayList<>();
        list2.add(l3);
        list2.add(l4);

        LcdImage lcdImage2 = new LcdImage(2, 32, list2);

        assertTrue(lcdImage1.equals(lcdImage2));
        assertTrue(lcdImage1.hashCode() == lcdImage2.hashCode());
    }

    @Test
    void joinWorksNizou() {
        BitVector msb1 = new BitVector(32);
        BitVector lsb1 = new BitVector(32, true);
        BitVector opacity = new BitVector(32, true);
        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity);

        BitVector msb2 = new BitVector(32, true);
        BitVector lsb2 = new BitVector(32);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity);

        LcdImageLine l3 = l1.join(l2, 12);

        BitVector expectedMsb = new BitVector.Builder(32).setByte(0, 0b0).setByte(1, 0b11110000).setByte(2, 0b11111111)
                .setByte(3, 0b11111111).build();
        BitVector expectedLsb = new BitVector.Builder(32).setByte(0, 0b11111111).setByte(1, 0b00001111)
                .setByte(2, 0b0).setByte(3, 0b0).build();

        LcdImageLine expected = new LcdImageLine(expectedMsb, expectedLsb, opacity);
        System.out.println("expected msb " + expectedMsb.toString());
        System.out.println("expected lsb " + expectedLsb.toString());
        System.out.println("actual msb   " + l3.getMsb());
        System.out.println("actual lsb   " + l3.getLsb());

        assertTrue(expected.equals(l3));
        //assertEquals(expected, l3);
    }

}
