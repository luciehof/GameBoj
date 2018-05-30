package ch.epfl.gameboj.component.cartridge;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

import java.util.Objects;

/**
 * Represents a memory bank controller of type 0 (ROM of 32 768 bytes)
 *
 * @author Lucie Hoffmann (286865)
 */

public final class MBC0 implements Component {

    private final Rom controlledRom;
    private static final int ROM_SIZE = 0x8000;

    /**
     * Constructs a MBC of type 0 for the given rom.
     *
     * @param rom Rom for which we build a controller
     * @throws NullPointerException     if the given rom is null
     * @throws IllegalArgumentException if the given rom is not containing
     *                                  exactly 32 768 bytes
     */
    public MBC0(Rom rom) {
        Objects.requireNonNull(rom);
        Preconditions.checkArgument(rom.size() == ROM_SIZE);

        controlledRom = rom;
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);

        if (address <= 0x7FFF)
            return controlledRom.read(address);

        return NO_DATA;
    }

    @Override
    public void write(int address, int data) {
    }
}
