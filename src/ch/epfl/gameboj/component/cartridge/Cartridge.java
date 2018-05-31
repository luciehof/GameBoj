package ch.epfl.gameboj.component.cartridge;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.Rom;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Represents a game cartridge containing a rom of 32 768 bytes and a
 * corresponding memory controller
 *
 * @author Lucie Hoffmann (286865)
 */

public final class Cartridge implements Component {

    private final static int CARTRIDGE_TYPE = 0x147;
    private final static int RAM_SIZE = 0x149;
    private final Ram ram;

    private final Component mbc;

    /**
     * Constructs a cartridge containing the given controller mbc0 and the rom
     * attached to it.
     *
     * @param mbc Component, the memory bank controller
     */
    private Cartridge(Component mbc) {
        this.mbc = mbc;
        ram = new Ram(RAM_SIZE);
    }

    /**
     * Returns a cartridge in which the rom contains the bytes of the given
     * file.
     *
     * @param romFile, the file that must be contained in the rom bytes
     * @return a cartridge with a rom corresponding to the given file<
     * @throws IOException              if romFile does not exist
     */
    public static Cartridge ofFile(File romFile) throws IOException {
        Objects.requireNonNull(romFile);

        byte[] fileData = Files.readAllBytes(romFile.toPath());
        int cartridgeType = fileData[CARTRIDGE_TYPE];
        Preconditions.checkArgument(0 <= cartridgeType && cartridgeType < 4);

        Rom memory = new Rom(fileData);
        Component controller;

        if (cartridgeType == 0)
            controller = new MBC0(memory);

        else {
            int ramSize = fileData[RAM_SIZE];
            int[] sizes = { 0, 2048, 8192, 32768 };
            controller = new MBC1(memory, sizes[ramSize]);
        }
        
        return new Cartridge(controller);
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);

        return mbc.read(address);
    }

    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        mbc.write(address, data);
    }
}
