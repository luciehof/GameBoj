package ch.epfl.gameboj.component;

import ch.epfl.gameboj.Bus;

/**
 * a component
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public interface Component {

    public final static int NO_DATA = 0x100;

    /**
     * Returns the byte at the given address in the component.
     *
     * @param address of the byte
     * @return the element at the index address or NO_DATA if there is no byte
     * at this address
     */
    public abstract int read(int address);

    /**
     * Stores the given data at the given address if possible.
     *
     * @param address where we want to store the data
     * @param data    we want to store at the given address in the component
     */
    public abstract void write(int address, int data);

    /**
     * Attaches the bus to the component.
     *
     * @param bus the bus to attach to the component
     */
    public default void attachTo(Bus bus) {
        bus.attach(this);
    }
}