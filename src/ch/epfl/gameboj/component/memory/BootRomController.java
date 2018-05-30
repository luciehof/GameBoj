package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cartridge.Cartridge;

import java.util.Objects;

/**
 * a boot rom controller
 *
 * @author Marie Jaillot (270130)
 */

public final class BootRomController implements Component {

    private final Cartridge cartridge;
    private final Rom bootRom;
    private boolean activated;

    /**
     * Constructs a Boot Rom controller
     * @param cartridge attached to the Boot Rom controller
     */
    public BootRomController(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);

        activated = true;
        this.cartridge = cartridge;
        bootRom = new Rom(BootRom.DATA);
    }

    @Override public int read(int address){
        Preconditions.checkBits16(address);

        if (activated && AddressMap.BOOT_ROM_START <= address
                && address < AddressMap.BOOT_ROM_END)
            return Byte.toUnsignedInt(BootRom.DATA[address]);

        return cartridge.read(address);
    }

    @Override public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        if (address == AddressMap.REG_BOOT_ROM_DISABLE)
            activated = false;
        cartridge.write(address, data);
    }
}
