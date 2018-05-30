package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.Preconditions;

import java.util.Arrays;
import java.util.Objects;

/**
 * a rom (read only memory)
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

final public class Rom {

    private final byte[] data;

    /**
     * Constructs an array representing the ROM.
     *
     * @param data, array representing the data we want to store in the rom
     * @throws NullPointerException if the given data is null
     */
    public Rom(byte[] data) {
        Objects.requireNonNull(data);

        this.data = Arrays.copyOf(data, data.length);
    }

    /**
     * Gives the number of bytes in the ROM.
     *
     * @return size of array of data representing the rom
     */
    public int size() {
        return data.length;
    }

    /**
     * Gives the byte represented by the given index in the ROM.
     *
     * @param index of byte we want to read
     * @return byte at index position of array data
     * @throws IndexOutOfBoundsException if the given index is negative or is
     *                                   bigger than the data array
     */
    public int read(int index) {
        if (0 > index || index >= data.length)
            throw new IndexOutOfBoundsException();

        return Byte.toUnsignedInt(data[index]);
    }
}
