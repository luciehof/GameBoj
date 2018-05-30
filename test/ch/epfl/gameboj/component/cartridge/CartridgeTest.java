// Gameboj stage 6

package ch.epfl.gameboj.component.cartridge;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.ComponentTest;

public final class CartridgeTest implements ComponentTest {
    public static Cartridge cartridgeWithData(byte[] romData) {
        try {
            File tempPath = Files.createTempFile("TestROM_", ".gb").toFile();
            tempPath.deleteOnExit();
            try (OutputStream s = new FileOutputStream(tempPath)) {
                s.write(romData);
            }
            return Cartridge.ofFile(tempPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    @Override
    public Component newComponent() {
        return cartridgeWithData(new byte[0x8000]);
    }

    @Test
    void ofFileFailsWithNonExistentFile() {
        assertThrows(IOException.class, () -> {
            Cartridge.ofFile(new File("____\\\\....////____"));
        });
    }
    
    @Test
    void ofFileFailsWithNonZeroByteAt147() {
        byte[] romData = new byte[0x8000];
        romData[0x147] = (byte) 0xFF;
        assertThrows(IllegalArgumentException.class, () -> {
            cartridgeWithData(romData);
        });
    }
}
