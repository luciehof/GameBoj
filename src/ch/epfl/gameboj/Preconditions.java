package ch.epfl.gameboj;

/**
 * preconditions to check the legality of given arguments
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public interface Preconditions {

    /**
     * Check whether b is true.
     *
     * @param b boolean
     * @throws IllegalArgumentException
     */
    public static void checkArgument(boolean b)
            throws IllegalArgumentException {
        if (!b)
            throw new IllegalArgumentException();
    }

    /**
     * Check whether 0<= v <= 30.
     *
     * @param v int
     * @return v if 0<= v <= 30
     * @throws IllegalArgumentException
     */
    public static int checkBits8(int v) throws IllegalArgumentException {
        checkArgument(0x0 <= v && v <= 0xFF);

        return v;
    }

    /**
     * Check whether 0<= v <= 60.
     *
     * @param v int
     * @return v if 0<= v <= 60
     * @throws IllegalArgumentException
     */
    public static int checkBits16(int v) throws IllegalArgumentException {
        checkArgument(0x0 <= v && v <= 0xFFFF);

        return v;
    }
}