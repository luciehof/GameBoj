package ch.epfl.gameboj;

import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;

/**
 * 8-bits registers file
 *
 * @author Lucie Hoffmann (286865)
 */
public final class RegisterFile<E extends Register> {

    private final byte[] allRegs;

    /**
     * Constructs a register file of 8-bits registers which contains the same
     * number of registers than the size of the given array.
     *
     * @param allRegs array
     */
    public RegisterFile(E[] allRegs) {
        this.allRegs = new byte[allRegs.length];
    }

    /**
     * Returns the 8-bits value of the given register.
     *
     * @param reg register
     * @return an integer between 0(included) and 0xFF(included)
     */
    public int get(E reg) {
        return Byte.toUnsignedInt(allRegs[reg.index()]);
    }

    /**
     * Changes the register's value to the given newValue.
     *
     * @param reg      register which value is changed
     * @param newValue int
     * @throws IllegalArgumentException if newValue is not an 8-bits value
     */
    public void set(E reg, int newValue) throws IllegalArgumentException {
        Preconditions.checkBits8(newValue);

        allRegs[reg.index()] = (byte) newValue;
    }

    /**
     * Returns true if the given bit of the register is equal to 1, returns
     * false otherwise.
     *
     * @param reg register
     * @param b   bit
     * @return true or false
     */
    public boolean testBit(E reg, Bit b) {
        return Bits.test(get(reg), b.index());
    }

    /**
     * Changes the register's bit to the given bit.
     *
     * @param reg      register
     * @param bit      bit
     * @param newValue boolean, true for 1, false for 0
     */
    public void setBit(E reg, Bit bit, boolean newValue) {
        set(reg, Bits.set(get(reg), bit.index(), newValue));
    }
}
