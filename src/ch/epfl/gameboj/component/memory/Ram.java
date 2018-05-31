package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;

import java.util.Objects;

/**
 * a ram (random access memory)
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public final class Ram {

    private final byte[] data;

    /**
     * Constructs array representing RAM.
     *
     * @param size of array (number of bytes in the RAM)
     * @throws IllegalAccessException if the given size is negative
     */
    public Ram(int size) {
        Preconditions.checkArgument(0 <= size);

        data = new byte[size];
    }

    /**
     * Gives the number of bytes in the RAM.
     *
     * @return size of array data
     */
    public int size() {
        return data.length;
    }

    /**
     * Gives the byte represented by the given index in the RAM.
     *
     * @param index of byte we want to read
     * @return byte at index position of array data
     */
    public int read(int index) {
        if (0 > index || index >= data.length)
            throw new IndexOutOfBoundsException();

        return Byte.toUnsignedInt(data[index]);
    }

    /**
     * Replaces byte value at index position in RAM by given value.
     *
     * @param index of byte we want to modify
     * @param value of byte (to replace the old one)
     */
    public void write(int index, int value) {
        if (0 > index || index >= data.length)
            throw new IndexOutOfBoundsException();
        Preconditions.checkBits8(value);

        data[index] = (byte) value;
    }
}
