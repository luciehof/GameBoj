package ch.epfl.gameboj.bits;

/**
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public interface Bit {

    /**
     * Returns the index of this bit.
     *
     * @return the index of this bit
     */
    public int ordinal();

    /**
     * Returns the index of this bit.
     *
     * @return the index of this bit
     */
    public default int index() {
        return ordinal();
    }

    /**
     * Returns the mask corresponding to this bit.
     *
     * @return the mask corresponding to this bit
     */
    public default int mask() {
        return Bits.mask(ordinal());
    }
}