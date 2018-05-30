package ch.epfl.gameboj.bits;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.cpu.Alu;

import java.util.Arrays;

/**
 * a bits vector which size is a multiple of 32
 *
 * @author Lucie Hoffmann (286865)
 */

public final class BitVector {

    private static final int MAX_8_BITS = 0xFF;
    private static final int MAX_32_BITS = 0xFFFFFFFF;

    private final int[] vector;

    private BitVector(int[] vector) {
        this.vector = vector;
    }

    /**
     * Constructs a bits vector of the given size in which all bits have
     * the given value.
     *
     * @param size      size of the bits vector to construct
     * @param initValue value of the initial bits (true for 1, false for 0)
     */
    public BitVector(int size, boolean initValue) {
        Preconditions.checkArgument((size % Integer.SIZE == 0) && size > 0);

        vector = new int[size / Integer.SIZE];
        if (initValue)
            Arrays.fill(vector, MAX_32_BITS);
    }

    /**
     * Constructs a bits vector of the given size in which all bits are 0.
     *
     * @param size size of the bits vector to construct
     */
    public BitVector(int size) {
        this(size, false);
    }

    /**
     * Returns the vector's size in number of bits.
     *
     * @return an integer representing the vector's size
     */
    public int size() {
        return vector.length * Integer.SIZE;
    }

    /**
     * Tests if the bit at the given index is 1 (true) or 0 (false).
     *
     * @param index of the bit we want to test
     * @return a boolean representing the bit value
     */
    public boolean testBit(int index) {
        Preconditions.checkArgument(0 <= index && index < size());

        return Bits.test(vector[index / Integer.SIZE], index % Integer.SIZE);
    }

    /**
     * Computes the complement of attribute vector.
     *
     * @return an int[] representing the complement of vector
     */
    public BitVector not() {
        int length = vector.length;
        int[] comp = new int[length];

        for (int i = 0; i < length; ++i)
            comp[i] = ~vector[i];

        return new BitVector(comp);
    }

    /**
     * Computes the conjunction between attribute vector and the given other
     * vector.
     *
     * @param otherVector an int[] we want to make the conjunction with
     * @return an int[] representing the conjunction between vector and
     * otherVector
     */
    public BitVector and(BitVector otherVector) {
        Preconditions.checkArgument(otherVector.size() == size());

        int length = vector.length;
        int[] and = new int[length];

        for (int i = 0; i < length; ++i)
            and[i] = otherVector.vector[i] & vector[i];

        return new BitVector(and);
    }

    /**
     * Computes the disjunction between attribute vector and the given other
     * vector.
     *
     * @param otherVector an int[] we want to make the disjunction with
     * @return an int[] representing the disjunction between vector and
     * otherVector
     */
    public BitVector or(BitVector otherVector) {
        Preconditions.checkArgument(otherVector.size() == size());

        int length = vector.length;
        int[] or = new int[length];

        for (int i = 0; i < length; ++i)
            or[i] = otherVector.vector[i] | vector[i];

        return new BitVector(or);
    }

    private int extractElement(boolean extractWrapped, int index) {
        int length = vector.length;

        if (extractWrapped)
            return vector[Math.floorMod(index, length)];
        if (0 <= index && index < length)
            return vector[index];

        return 0;
    }

    private BitVector extract(boolean extractWrapped, int index, int size) {
        Preconditions.checkArgument((size % Integer.SIZE == 0) && size > 0);

        int tabSize = size / Integer.SIZE;
        int[] extractVec = new int[tabSize];
        int extractIndex = Math.floorDiv(index, Integer.SIZE);

        if (Math.floorMod(index, Integer.SIZE) == 0)
            for (int i = 0; i < tabSize; ++i)
                extractVec[i] = extractElement(extractWrapped,
                        extractIndex + i);
        else {
            for (int i = 0; i < tabSize; ++i) {
                int rightBlock = extractElement(extractWrapped,
                        extractIndex + i);
                int leftBLock = extractElement(extractWrapped,
                        extractIndex + i + 1);
                int shift = (Math.floorMod(index, Integer.SIZE));
                extractVec[i] =
                        (rightBlock >>> shift) | leftBLock << (Integer.SIZE
                                - shift);
            }
        }
        return new BitVector(extractVec);
    }


    /**
     * Extracts a vector of the given size in the zero extension of attribute
     * vector.
     *
     * @param index integer, where we start the extraction
     * @param size  integer, number of bits extracted at the left of index
     * @return an int[] representing the wanted zero extended extraction
     */
    public BitVector extractZeroExtended(int index, int size) {
        return extract(false, index, size);
    }

    /**
     * Extracts a vector of the given size in the wrapped extension of attribute
     * vector.
     *
     * @param index integer, where we start the extraction
     * @param size  integer, number of bits extracted at the left of index
     * @return an int[] representing the wanted wrapped extraction
     */
    public BitVector extractWrapped(int index, int size) {
        return extract(true, index, size);
    }

    public int[] getVector() {
        return vector;
    }

    /**
     * Returns a copy of the current vector shifted by the given distance
     * (to the left if distance is positive, right otherwise).
     *
     * @param distance integer representing how many bits we shift
     * @return the current vector shifted by distance
     */
    public BitVector shift(int distance) {
        return extractZeroExtended(-distance, size());
    }

    @Override public boolean equals(Object that) {
        Preconditions.checkArgument(that instanceof BitVector);

        return Arrays.equals(vector, ((BitVector) that).vector);
    }

    @Override public int hashCode() {
        return Arrays.hashCode(vector);
    }

    @Override public String toString() {
        StringBuilder s = new StringBuilder();

        for(int i = 0; i < vector.length; ++i)
            for(int j = 0; j < Integer.SIZE; ++j)
                s.append(Bits.extract(vector[i], j, 1));

        return s.reverse().toString();
    }

    /**
     * a bit vector builder
     */
    public final static class Builder {
        private int[] buildingVec;

        /**
         * Constructs a bit vector builder, with the given size (in bits)
         * (all bits are set to 0 initially).
         *
         * @param size of the bit vector we construct (integer)
         */
        public Builder(int size) {
            Preconditions.checkArgument((size % Integer.SIZE == 0)
                    && size > 0);

            buildingVec = new int[size / Integer.SIZE];
        }

        /**
         * Sets the byte at the given index at the given value.
         *
         * @param index at which we want to set the byte
         * @param value we want to set the byte at
         */
        public Builder setByte(int index, int value) {
            Preconditions.checkBits8(value);
            if (buildingVec == null)
                throw new IllegalStateException();
            if (!(0 <= index && index < buildingVec.length * Byte.SIZE))
                throw new IndexOutOfBoundsException();

            buildingVec[index / 4] = (~(MAX_8_BITS << (index % 4) * Byte.SIZE)
                    & buildingVec[index / 4])
                    | value << ((index % 4) * Byte.SIZE);

            return this;
        }

        /**
         * Constructs the bit vector and returns it.
         *
         * @return the BitVector in construction
         */
        public BitVector build() {
            if (buildingVec == null)
                throw new IllegalStateException();

            BitVector bV = new BitVector(buildingVec);
            buildingVec = null;

            return bV;
        }
    }
}