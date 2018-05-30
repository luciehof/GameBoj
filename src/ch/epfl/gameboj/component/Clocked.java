package ch.epfl.gameboj.component;

/**
 * A component driven by the system clock
 *
 * @author Marie Jaillot (270130)
 */
public interface Clocked {

    /**
     * Instructs the component to evolve by executing all the operations it
     * must perform during the given cycle.
     *
     * @param cycle long representing the index of cycle
     */
    void cycle(long cycle);
}
