package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * represents a Game Boy image
 *
 * @author Marie Jaillot (270130)
 */
public final class LcdImage {

    private final int width;
    private final int height;
    private final List<LcdImageLine> lines;

    /**
     * Constructs a lcd image with the given width and height and list of lines.
     *
     * @param width    width of the lcd image to construct
     * @param height   height of the lcd image to construct
     * @param lineList a list of the image lines of the lcd image to construct
     */
    public LcdImage(int width, int height, List<LcdImageLine> lineList) {
        Preconditions.checkArgument(width > 0 && height > 0);

        this.width = width;
        this.height = height;
        lines = Collections.unmodifiableList(lineList);
    }

    /**
     * Returns the width of the current lcd image.
     *
     * @return an int representing the width of the lcd image
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the current lcd image.
     *
     * @return an int representing the height of the lcd image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the color of the pixel at the given index (x, y), as an integer
     * between 0 and 3.
     *
     * @param x the horizontal coordinate of the pixel
     * @param y te vertical coordinate of the pixel
     * @return an integer between 0 and 3 representing the color of the pixel at
     * index (x, y)
     */
    public int get(int x, int y) {
        LcdImageLine line = lines.get(y);
        int lsb = line.getLsb().testBit(x) ? 1 : 0;
        int msb = (line.getMsb().testBit(x) ? 1 : 0) << 1;

        return msb | lsb;
    }

    @Override public boolean equals(Object that) {
        return width == ((LcdImage) that).width
                && height == ((LcdImage) that).height
                && lines.equals(((LcdImage) that).lines)
                && that instanceof LcdImage;
    }

    @Override public int hashCode() {
        return (int) (Integer.hashCode(width) + 31 * Integer.hashCode(height)
                + Math.pow(31, 2) * lines.hashCode());
    }

    /**
     * a lcd image builder
     */
    public final static class Builder {

        private int width;
        private int height;
        private List<LcdImageLine> lines;

        /**
         * Constructs a lcd image builder with the given width and height.
         * Its lcd image lines are all made of pixels of color 0.
         *
         * @param width  of the lcd image we want to build
         * @param height of the lcd image we want to build
         */
        public Builder(int width, int height) {
            Preconditions.checkArgument(width > 0 && height > 0);

            this.width = width;
            this.height = height;
            lines = new ArrayList<>();
            LcdImageLine line = new LcdImageLine.Builder(width).build();

            for (int i = 0; i < height; ++i) {
                lines.add(i, line);
            }
        }

        /**
         * Sets the line at the given index with the given image line.
         *
         * @param index at which we want to change the line
         * @param line  the new value of the line we want to set
         */
        public Builder setLine(int index, LcdImageLine line) {
            lines.set(index, line);
            return this;
        }

        /**
         * Builds a lcd image and returns it.
         *
         * @return the lcd image in construction
         */
        public LcdImage build() {
            return new LcdImage(width, height, lines);
        }
    }
}
