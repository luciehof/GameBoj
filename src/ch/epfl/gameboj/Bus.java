package ch.epfl.gameboj;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.gameboj.component.Component;

/**
 * a bus
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public final class Bus {

    private final int NO_VALUE = 0xFF;
    private final ArrayList<Component> components = new ArrayList<>();

    /**
     * Attaches the component to the bus.
     *
     * @param component the component to attach to the bus
     */
    public void attach(Component component) throws NullPointerException {
        Objects.requireNonNull(component);

        components.add(component);
    }

    /**
     * Returns the byte at the given address in the first encountered component
     * that has a value at this address.
     *
     * @param address integer representing the address we want to read in the
     *                bus
     * @return the byte at the given address or NO_VALUE (=0xFF) if there is no
     * value at this address
     */
    public int read(int address) {
        Preconditions.checkBits16(address);

        for (Component component : components) {
            int byteComponent = component.read(address);
            if (byteComponent != component.NO_DATA)
                return byteComponent;
        }
        return NO_VALUE;
    }

    /**
     * Stores the given data at the given address in each component of the bus
     * if possible.
     *
     * @param address integer representing the address in the bus where we want
     *                to write
     * @param data    integer representing the data we want to store in the bus
     */
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        for (Component component : components)
            component.write(address, data);
    }
}
