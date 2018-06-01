package ch.epfl.gameboj.gui;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.lcd.LcdImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import static ch.epfl.gameboj.component.lcd.LcdController.LCD_HEIGHT;
import static ch.epfl.gameboj.component.lcd.LcdController.LCD_WIDTH;

/**
 * Converts the Gameboy's images into javafx images
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */
public final class ImageConverter {

    private static final int[] COLOR_MAP = new int[] {
            0xFF_FF_FF_FF, 0xFF_D3_D3_D3, 0xFF_A9_A9_A9, 0xFF_00_00_00
    };

    /**
     * Converts the given Game Boy image into a JavaFX image of the same size.
     *
     * @param image LcdImage of a GameBoy
     * @return a JavaFX image 160*144 corresponding to the given one
     */
    public static Image convert(LcdImage image) {
        Preconditions.checkArgument(image.getHeight() == LCD_HEIGHT &&
                image.getWidth() == LCD_WIDTH);

        WritableImage writableImage = new WritableImage(LCD_WIDTH, LCD_HEIGHT);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < LCD_HEIGHT; ++y)
            for (int x = 0; x < LCD_WIDTH; ++x)
                pixelWriter.setArgb(x, y, COLOR_MAP[image.get(x, y)]);

        return writableImage;
    }
}
