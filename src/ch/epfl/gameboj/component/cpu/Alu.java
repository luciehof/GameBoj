package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;

import static ch.epfl.gameboj.bits.Bits.*;

/**
 * arithmetic logic unit of a processor
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public final class Alu {

    private static final int MAX_4_BITS = 0xF;
    private static final int MAX_8_BITS = 0xFF;
    private static final int MAX_12_BITS = 0xFFF;
    private static final int MAX_16_BITS = 0xFFFF;

    private Alu() {
    }

    /**
     * Flags corresponding to an arithmetic operation
     */
    public enum Flag implements Bit {
        UNUSED_0, UNUSED_1, UNUSED_2, UNUSED_3, C, H, N, Z
    }

    /**
     * Rotation's direction
     */
    public enum RotDir {
        LEFT(1), RIGHT(-1);

        private final int d;

        RotDir(int i) {
            d = i;
        }

        public int getD() {
            return d;
        }
    }

    /**
     * Returns the combination of value v and flags z, n, h and c.
     *
     * @param v 8-bits or 16-bits integer
     * @param z boolean, true if result is 0, false otherwise
     * @param n boolean, true if the operation is a subtraction, false
     *          otherwise
     * @param h boolean, 'halfcarry', true if there is a half carry from
     *          the addition of the 4 less significant bits, false otherwise
     * @param c boolean, 'carry', true if there is a carry from
     *          the addition of the total 8 bits, false otherwise
     * @return a 16-bits or 32-bits value contained in given v
     */
    private static int packValueZNHC(int v, boolean z, boolean n, boolean h,
            boolean c) {
        return (v << 8) + maskZNHC(z, n, h, c);
    }

    /**
     * Checks if there is a 'halfcarry' (or carry) in the sum of the less or
     * most (depending on C) significant bit of l and r.
     *
     * @param l  8-bits or 16-bits int
     * @param r  8-bits or 16-bits int
     * @param C1 char, either H, for halfcarry, or C, for carry
     * @param C2 char, either L, for less significant bit, or M, for most
     *           significant bit
     * @return a boolean, true if there is a 'halfcarry', false otherwise
     */
    private static boolean checkHC(int l, int r, char C1, char C2) {
        if (C1 == 'H') {
            if (C2 == 'L') {
                return (clip(4, l) + clip(4, r)) > MAX_4_BITS;
            } else {
                return (clip(12, l) + clip(12, r)) > MAX_12_BITS;
            }
        } else {
            if (C2 == 'L') {
                return (clip(8, l) + clip(8, r)) > MAX_8_BITS;
            } else {
                return (clip(16, l) + clip(16, r)) > MAX_16_BITS;
            }
        }
    }

    /**
     * Returns bits corresponding to the boolean value of flags Z, N, H
     * and C (1 if true, 0 otherwise).
     *
     * @param z boolean, true if result is 0, false otherwise
     * @param n boolean, true if the operation is a subtraction, false
     *          otherwise
     * @param h boolean, 'halfcarry', true if there is a half carry from
     *          the addition of the 4 less significant bits, false otherwise
     * @param c boolean, 'carry', true if there is a carry from
     *          the addition of the total 8 bits, false otherwise
     * @return 8-bits value representing flags
     */
    public static int maskZNHC(boolean z, boolean n, boolean h, boolean c) {
        int flags = 0x0;
        if (z)
            flags += Flag.Z.mask();
        if (n)
            flags += Flag.N.mask();
        if (h)
            flags += Flag.H.mask();
        if (c)
            flags += Flag.C.mask();

        return flags;
    }

    /**
     * Returns the value contained in valueFlags.
     *
     * @param valueFlags integer containing a value and its flags
     * @return an 8-bits or 16-bits value
     * @throws IllegalArgumentException if the given valueFlags is not an
     *                                  8-bits or 16-bits value
     */
    public static int unpackValue(int valueFlags)
            throws IllegalArgumentException {
        return extract(valueFlags, 8, 16);
    }

    /**
     * Returns the flags contained in valueFlags.
     *
     * @param valueFlags integer containing a value and its flags
     * @return an 8-bits value representing the flags contained in the given
     * valueFlags
     * @throws IllegalArgumentException if the given valueFlags is not an
     *                                  8-bits or 16-bits value
     */
    public static int unpackFlags(int valueFlags)
            throws IllegalArgumentException {
        return clip(8, valueFlags);
    }

    /**
     * Returns the sum of integers l and r (and adds 1 to this sum if c0 is
     * true, adds nothing otherwise), with the flags values Z0HC.
     *
     * @param l  8-bits integer
     * @param r  8-bits integer
     * @param c0 boolean, initial carry
     * @return a 32-bits value representing the result of the addition of l and
     * r and the initial carry
     * @throws IllegalArgumentException if l or r are not 8-bits values
     */
    public static int add(int l, int r, boolean c0)
            throws IllegalArgumentException {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);

        int c1 = (c0) ? 1 : 0;
        int res = clip(8, l + r + c1);

        boolean h = (clip(4, l) + clip(4, r) + c1) > MAX_4_BITS;
        boolean c = l + r + c1 > MAX_8_BITS;

        return packValueZNHC(res, res == 0, false, h, c);
    }

    /**
     * Returns the sum of integers l and r, with the flags values Z0HC.
     *
     * @param l int 8-bits
     * @param r int 8-bits
     * @return a 32-bits value
     * @throws IllegalArgumentException if l or r are not 8-bits values
     */
    public static int add(int l, int r) throws IllegalArgumentException {
        return add(l, r, false);
    }

    /**
     * Returns the sum of integers l and r, with the flags value 00HC
     * where H and C correspond to the sum of the 8 less significant
     * bits.
     *
     * @param l int 16-bits
     * @param r int 16-bits
     * @return a 32-bits value
     * @throws IllegalArgumentException if l or r are not 16-bits values
     */
    public static int add16L(int l, int r) throws IllegalArgumentException {
        Preconditions.checkBits16(l);
        Preconditions.checkBits16(r);

        return packValueZNHC(clip(16, l + r), false, false,
                checkHC(l, r, 'H', 'L'),
                checkHC(l, r, 'C', 'L'));
    }

    /**
     * Returns the sum of integers l and r, with the flags value 00HC
     * where H and C correspond to the sum of the 8 most significant
     * bits.
     *
     * @param l int 16-bits
     * @param r int 16-bits
     * @return a 32-bits value
     * @throws IllegalArgumentException if l or r are not 16-bits values
     */
    public static int add16H(int l, int r) throws IllegalArgumentException {
        Preconditions.checkBits16(l);
        Preconditions.checkBits16(r);

        int res = clip(16, l + r);
        return packValueZNHC(res, false, false,
                checkHC(l, r, 'H', 'M'),
                checkHC(l, r, 'C', 'M'));
    }

    /**
     * Returns the subtraction of integers l and r (and subtracts 1 to this
     * sum if b0 is true, 0 otherwise), with the flags value Z1HC.
     *
     * @param l  int 8-bits
     * @param r  int 8-bits
     * @param b0 boolean
     * @return an 32-bits value
     * @throws IllegalArgumentException if l or r are not 8-bits values
     */
    public static int sub(int l, int r, boolean b0)
            throws IllegalArgumentException {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);

        int b1 = (b0) ? 1 : 0;
        int res = clip(8, l - r - b1);

        boolean h = clip(4, l) < (clip(4, r) + b1);
        boolean c = l < (r + b1);

        return packValueZNHC(res, res == 0, true, h, c);
    }

    /**
     * Returns the subtraction of integers l and r, with the flags value Z1HC.
     *
     * @param l int 8-bits
     * @param r int 8-bits
     * @return an 32-bits value
     * @throws IllegalArgumentException if l or r are not 8-bits values
     */
    public static int sub(int l, int r) throws IllegalArgumentException {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);

        return sub(l, r, false);
    }

    /**
     * Returns the corresponding binary coded decimal of the given integer v.
     *
     * @param v int 8-bits value
     * @param n boolean, true if the operation is a subtraction, false
     *          otherwise
     * @param h boolean, 'halfcarry', true if there is a half carry from
     *          the addition of the 4 less significant bits, false otherwise
     * @param c boolean, 'carry', true if there is a carry from
     *          the addition of the total 8 bits, false otherwise
     * @return a 32-bits value
     * @throws IllegalArgumentException
     */
    public static int bcdAdjust(int v, boolean n, boolean h, boolean c)
            throws IllegalArgumentException {
        Preconditions.checkBits8(v);

        boolean fixL = h | (!n & clip(4, v) > 9);
        boolean fixH = c | (!n & v > 0x99);

        int fixHint = (fixH) ? 1 : 0;
        int fixLint = (fixL) ? 1 : 0;
        int fix = 0x60 * fixHint + 0x06 * fixLint;

        int bcd = (n) ? v - fix : v + fix;
        bcd = Bits.clip(8, bcd);

        return packValueZNHC(bcd, bcd == 0, n, false, fixH);
    }

    /**
     * Returns the 'and' (&) bitwise operation of l and r with flags Z010.
     *
     * @param l 8-bits integer
     * @param r 8-bits integer
     * @return an 8-bits value
     * @throws IllegalArgumentException if l or r are not 8-bits values
     */
    public static int and(int l, int r) throws IllegalArgumentException {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);

        int and = l & r;

        return packValueZNHC(and, and == 0, false, true, false);
    }

    /**
     * Returns the 'inclusive or' (|) bitwise operation of l and r with flags
     * Z000.
     *
     * @param l 8-bits integer
     * @param r 8-bits integer
     * @return an 8-bits value
     * @throws IllegalArgumentException if l or r are not 8-bits values
     */
    public static int or(int l, int r) throws IllegalArgumentException {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);

        int or = l | r;

        return packValueZNHC(or, or == 0, false, false, false);
    }

    /**
     * Returns the 'exclusive or' (^) bitwise operation of l and r with flags
     * Z000.
     *
     * @param l 8-bits integer
     * @param r 8-bits integer
     * @return an 8-bits value
     * @throws IllegalArgumentException if l or r are not 8-bits values
     */
    public static int xor(int l, int r) throws IllegalArgumentException {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);

        int xor = l ^ r;

        return packValueZNHC(xor, xor == 0, false, false, false);
    }

    /**
     * Returns the given value shifted from 1 bit to the left with flags Z00C
     * where C represents the bit ejected by the shifting.
     *
     * @param v 8-bits int
     * @return a 16-bits value
     * @throws IllegalArgumentException if v is not an 8-bits value
     */
    public static int shiftLeft(int v) throws IllegalArgumentException {
        Preconditions.checkBits8(v);

        boolean c = false;
        if (test(v, 7))
            c = true;

        int shiftedV = clip(8, v << 1);

        return packValueZNHC(shiftedV, shiftedV == 0, false, false, c);
    }

    /**
     * Returns the given value shifted from 1 bit to the right (arithmetic
     * shifting) with flags Z00C, where C represents the bit ejected by
     * the shifting.
     *
     * @param v 8-bits int
     * @return a 16-bits value
     * @throws IllegalArgumentException if v is not an 8-bits value
     */
    public static int shiftRightA(int v) throws IllegalArgumentException {
        Preconditions.checkBits8(v);

        int shiftedV = test(v, 7) ? v | mask(8) : v;

        return packValueZNHC(shiftedV >> 1, (v >> 1) == 0, false,
                false, test(v, 0));
    }

    /**
     * Returns the given value shifted from 1 bit to the right (logic shifting)
     * with flags Z00C, where C represents the bit ejected by the shifting.
     *
     * @param v 8-bits int
     * @return a 16-bits value
     * @throws IllegalArgumentException if v is not an 8-bits value
     */
    public static int shiftRightL(int v) throws IllegalArgumentException {
        Preconditions.checkBits8(v);

        return packValueZNHC(v >>> 1, (v >> 1) == 0, false, false,
                test(v, 0));
    }

    /**
     * Returns the rotation of v with distance of 1 bit in direction d, and
     * flags Z00C where C represents the bit removed by the rotation.
     *
     * @param d direction RotDir, left or right
     * @param v 8-bits int
     * @return a 16-bits value
     * @throws IllegalArgumentException if v is not an 8-bits value
     */
    public static int rotate(RotDir d, int v) throws IllegalArgumentException {
        Preconditions.checkBits8(v);

        int res = Bits.rotate(8, v, d.getD());
        boolean c = (d.getD() == 1) ? test(v, 7) : test(v, 0);

        return packValueZNHC(res, v == 0, false, false, c);
    }

    /**
     * Returns the rotation of the combination of value v and flag c, with
     * distance of 1 bit in direction d, i.e. the 8 less significant bits
     * obtained after rotation and flags Z00C, where C represents the most
     * significant bit.
     *
     * @param d direction RotDir, left or right
     * @param v 8-bits int
     * @param c boolean, 'carry', true if there is a carry from
     *          the addition of the total 8 bits, false otherwise
     * @return a 16-bits value
     * @throws IllegalArgumentException if v is not an 8-bits value
     */
    public static int rotate(RotDir d, int v, boolean c)
            throws IllegalArgumentException {
        Preconditions.checkBits8(v);

        int res = v;
        if (c)
            res = res | mask(8);

        res = Bits.rotate(9, res, d.getD());
        boolean carry = test(res, 8);
        res = clip(8, res);

        return packValueZNHC(res, res == 0, false, false, carry);
    }

    /**
     * Switches the less significant bits with the most ones, and adds the
     * flags Z000.
     *
     * @param v 8-bits int
     * @return a 16-bits value
     * @throws IllegalArgumentException if v is not an 8-bits value
     */
    public static int swap(int v) throws IllegalArgumentException {
        Preconditions.checkBits8(v);

        int switchV = Bits.rotate(8, v, 4);

        return packValueZNHC(switchV, v == 0, false, false,
                false);
    }

    /**
     * Returns 0 with the flags Z010, where Z is true iff the bit of index
     * bitIndex in v is 0.
     *
     * @param v         8-bits int
     * @param bitIndex, index at which we want to test the bit
     * @return a 16-bits value with flag z to 1 if bit is 0, 0 otherwise
     * @throws IllegalArgumentException if v is not an 8-bits value,
     *                                  IndexOutOfBoundsException if bitIndex
     *                                  is not between 0 and 7
     */
    public static int testBit(int v, int bitIndex)
            throws IllegalArgumentException, IndexOutOfBoundsException {
        Preconditions.checkBits8(v);

        if (!(0 <= bitIndex && bitIndex <= 7))
            throw new IndexOutOfBoundsException();

        return packValueZNHC(0, !test(v, bitIndex), false, true,
                false);
    }

}
