package ch.epfl.gameboj;

/**
 * a register
 *
 * @author Marie Jaillot (270130)
 */

public interface Register {

    /**
     * Returns the index of the register.
     *
     * @return int
     */
    public int ordinal();

    /**
     * Returns the index of the register (in the list of the enum types).
     *
     * @return int
     */
    public default int index() {
        return ordinal();
    }
}
