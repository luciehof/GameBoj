package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;

import java.util.Objects;

/**
 * a ram controller (component)
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public final class RamController implements Component {

    private final Ram controlledRam;
    private final int startAddress;
    private final int endAddress;

    /**
     * Constructs a ramController which controls the ram from the
     * startAddress to the endAddress.
     *
     * @param ram          we want to control
     * @param startAddress integer representing the starting address where we
     *                     want to control the ram
     * @param endAddress   integer representing the ending address where we
     *                     want to control the ram
     */
    public RamController(Ram ram, int startAddress, int endAddress) {
        Objects.requireNonNull(ram);

        Preconditions.checkBits16(startAddress);
        Preconditions.checkBits16(endAddress);
        Preconditions.checkArgument(startAddress <= endAddress);
        Preconditions.checkArgument(
                (endAddress - startAddress) <= ram.size());

        controlledRam = ram;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    /**
     * Constructs a ramController which controls the ram from the startAddress.
     *
     * @param ram          we want to control
     * @param startAddress integer representing the starting address where we
     *                     want to control the ram
     */
    public RamController(Ram ram, int startAddress) {
        this(ram, startAddress, ram.size() + startAddress);
    }

    /**
     * Gives the byte at the given address.
     *
     * @param address of the byte we want to read
     * @return the byte at the given address or NO_DATA if there is no byte at
     * this address
     */
    @Override public int read(int address) {
        Preconditions.checkBits16(address);

        if (startAddress <= address && address < endAddress)
            return controlledRam.read(address - startAddress);

        return NO_DATA;
    }

    /**
     * Stores the given data at the given address if possible.
     *
     * @param address where we want to store the data
     * @param data    we want to store at the given address in the component
     */
    @Override public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        if (startAddress <= address && address < endAddress)
            controlledRam.write(address - startAddress, data);
    }
}
