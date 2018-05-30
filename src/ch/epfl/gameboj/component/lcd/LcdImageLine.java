package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;

import java.util.Objects;

/**
 * represents a line of a Game Boy image
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */
public final class LcdImageLine {

    private BitVector msb;
    private final BitVector lsb;
    private final BitVector opacity; // est ce que le final est vraiment necessaire puisque BitVector est deja immuable ?...

    /**
     * Constructs an lcd image line.
     *
     * @param msb     BitVector representing the most significant bits
     * @param lsb     BitVector representing the less significant bits
     * @param opacity BitVector representing the opacity of the image line
     * @throws IllegalArgumentException if the given BitVector do not have the
     *                                  same size
     */
    public LcdImageLine(BitVector msb, BitVector lsb, BitVector opacity)
            throws IllegalArgumentException {
        Preconditions.checkArgument(
                msb.size() == lsb.size() && lsb.size() == opacity.size());

        this.msb = msb;
        this.lsb = lsb;
        this.opacity = opacity;
    }

    /**
     * Returns the size of the current lcd image line.
     *
     * @return an integer representing the size of the current lcd line
     */
    public int size() {
        return msb.size();
    }

    /**
     * Returns the BitVector representing the MSB.
     *
     * @return a BitVector representing the MSB
     */
    public BitVector getMsb() {
        return msb;
    }

    /**
     * Returns the BitVector representing the LSB.
     *
     * @return a BitVector representing the LSB
     */
    public BitVector getLsb() {
        return lsb;
    }

    /**
     * Returns the BitVector representing the opacity.
     *
     * @return a BitVector representing the opacity
     */
    public BitVector getOpacity() {
        return opacity;
    }

    /**
     * Returns a copy of the lcd image line shifted by the given distance
     * (to the left if distance is positive, right otherwise).
     *
     * @param distance integer representing how many bits we shift
     * @return the current lcd image line shifted by distance
     */
    public LcdImageLine shift(int distance) {
        BitVector newMsb = msb.shift(distance);
        BitVector newLsb = lsb.shift(distance);
        BitVector newOpacity = opacity.shift(distance);
        return new LcdImageLine(newMsb, newLsb, newOpacity);
    }

    /**
     * Extracts a vector of the given size in the wrapped extension of attribute
     * vector.
     *
     * @param index integer, where we start the extraction
     * @param size  integer, number of bits extracted at the left of index
     * @return an int[] representing the wanted wrapped extraction
     */
    public LcdImageLine extractWrapped(int index, int size) {
        BitVector newMsb = msb.extractWrapped(index, size);
        BitVector newLsb = lsb.extractWrapped(index, size);
        BitVector newOpacity = opacity.extractWrapped(index, size);
        return new LcdImageLine(newMsb, newLsb, newOpacity);
    }

    /**
     * Transforms the lcd image line depending on the given palette.
     *
     * @param palette Byte representing the colors transformation
     * @return an LcdImageLine after the colors transformation given by the
     * palette
     */
    public LcdImageLine mapColors(int palette) {
        Preconditions.checkBits8(palette);

        int noChangePalette = 0b11_10_01_00;
        if (palette == noChangePalette)
            return this;

        BitVector newMsb = new BitVector(size());
        BitVector newLsb = new BitVector(size());
        for (int i = 0; i < 4; ++i) {
            BitVector maskColor;
            switch (i) {
            case 0: // Color 0 : lsb = 0, msb = 0
                maskColor = (msb.not()).and(lsb.not());
                break;
            case 1: // Color 1 : lsb = 1, msb = 0
                maskColor = (msb.not()).and(lsb);
                break;
            case 2: // Color 2 : lsb = 0, msb = 1
                maskColor = msb.and(lsb.not());
                break;
            case 3: // Color 3 : lsb = 1, msb = 1
                maskColor = msb.and(lsb);
                break;
            default:
                maskColor = null;
            }
            newLsb = (Bits.test(palette, i * 2)) ?
                    newLsb.or(maskColor) :
                    newLsb;
            newMsb = (Bits.test(palette, i * 2 + 1)) ?
                    newMsb.or(maskColor) :
                    newMsb;
        }

        return new LcdImageLine(newMsb, newLsb, opacity);
    }

    /**
     * Composes the current lcd image line with the given one by putting the
     * current one below the given one and using the opacity of the first one.
     *
     * @param otherLine LcdImageLine we want to compose with
     * @return an LcdImageLine representing the composition of the current and
     * the given LcdImageLine
     */
    public LcdImageLine below(LcdImageLine otherLine)
            throws IllegalArgumentException {
        BitVector otherOpacity = otherLine.getOpacity();
        return below(otherLine, otherOpacity);
    }

    /**
     * Composes the current lcd image line with the given one by putting the
     * current one below the given one and using the given opacity vector.
     *
     * @param otherLine LcdImageLine we want to compose with
     * @return an LcdImageLine representing the composition of the current and
     * the given LcdImageLine
     * @throws IllegalArgumentException if the given LcdImageLine and the
     *                                  current on do not have the same size
     */
    public LcdImageLine below(LcdImageLine otherLine, BitVector newOpacity)
            throws IllegalArgumentException {
        Preconditions.checkArgument(otherLine.size() == this.size());
        BitVector otherMsb = otherLine.getMsb();
        BitVector newMsb = (otherMsb.and(newOpacity))
                .or(msb.and(newOpacity.not()));
        BitVector otherLsb = otherLine.getLsb();
        BitVector newLsb = (otherLsb.and(newOpacity))
                .or(lsb.and(newOpacity.not()));
        BitVector composedOpacity = opacity.or(newOpacity);
        return new LcdImageLine(newMsb, newLsb, composedOpacity);
    }

    /**
     * Joins the current lcd line image to the given one, starting from the
     * index.
     *
     * @param otherLine LcdImageLine we want to join with
     * @param index     integer from which we join the given LcdImageLine
     * @return an LcdImageLine joining the current one with the given one
     * @throws IllegalArgumentException if the given LcdImageLine and the
     *                                  current on do not have the same size
     */
    public LcdImageLine join(LcdImageLine otherLine, int index)
            throws IllegalArgumentException {
        Preconditions.checkArgument(otherLine.size() == this.size());

        BitVector maskRight = new BitVector(size(), true).shift(index).not();

        BitVector rightMsb = msb.and(maskRight);
        BitVector leftMsb = (otherLine.getMsb()).and(maskRight.not());
        BitVector newMsb = rightMsb.or(leftMsb);

        BitVector rightLsb = lsb.and(maskRight);
        BitVector leftLsb = (otherLine.getLsb()).and(maskRight.not());
        BitVector newLsb = rightLsb.or(leftLsb);

        BitVector rightOpacity = opacity.and(maskRight);
        BitVector leftOpacity = (otherLine.getOpacity()).and(maskRight.not());
        BitVector newOpacity = rightOpacity.or(leftOpacity);

        return new LcdImageLine(newMsb, newLsb, newOpacity);
    }

    @Override public boolean equals(Object that) {
        return lsb.equals(((LcdImageLine) that).lsb) && msb
                .equals(((LcdImageLine) that).msb) && opacity
                .equals(((LcdImageLine) that).opacity)
                && that instanceof LcdImageLine;
    }

    @Override public int hashCode() {
        return (int) (msb.hashCode() + 31 * lsb.hashCode()
                + Math.pow(31, 2) * opacity.hashCode());
    }

    /**
     * a lcd image line builder
     */
    public final static class Builder {

        private BitVector.Builder buildMsb;
        private BitVector.Builder buildLsb;

        /**
         * Constructs a lcd image line builder with the given msb and lsb
         * builders.
         *
         * @param size of the msb and lsb builders
         */
        public Builder(int size) {

            buildMsb = new BitVector.Builder(size);
            buildLsb = new BitVector.Builder(size);
        }

        /**
         * Sets the msb and lsb bytes at the given index with the given values.
         *
         * @param msb   the value we want to give to the msb of the line
         * @param lsb   the value we want to give to the lsb of the line
         * @param index at which we want to set the msb and lsb
         */
        public Builder setBytes(int index, int msb, int lsb)
                throws IllegalStateException {
            if (buildMsb == null || buildLsb == null)
                throw new IllegalStateException();

            buildMsb.setByte(index, msb);
            buildLsb.setByte(index, lsb);

            return this;
        }

        /**
         * Builds a lcd image line and returns it.
         *
         * @return the lcd image line in construction
         */
        public LcdImageLine build() {
            if (buildMsb == null || buildLsb == null)
                throw new IllegalStateException();
            
            BitVector msb = buildMsb.build();
            BitVector lsb = buildLsb.build();
            BitVector opacity = msb.or(lsb);

            buildMsb = null;
            buildLsb = null;

            return new LcdImageLine(msb, lsb, opacity);
        }
    }
}
