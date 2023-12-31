package ch.epfl.gameboj.component;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;

import java.util.Objects;

public class Joypad implements Component {

    private static final int ROW0_INDEX = 4;
    private static final int ROW1_INDEX = 5;
    private static final int MASK = 0b11110000;

    private final Cpu cpu;
    private int regP1;
    private int row0;
    private int row1;

    public enum Key implements Bit {
        RIGHT, LEFT, UP, DOWN, A, B, SELECT, START
    }

    public Joypad(Cpu cpu) {
        Objects.requireNonNull(cpu);

        this.cpu = cpu;
        regP1 = 0;
        row0 = 0;
        row1 = 0;
    }

    @Override public int read(int address) {
        Preconditions.checkBits16(address);

        if (address == AddressMap.REG_P1)
            return Bits.complement8(regP1 & columnState());

        return NO_DATA;
    }

    @Override public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        if (address == AddressMap.REG_P1) {
            if (Bits.extract(regP1, ROW0_INDEX, 2) != Bits.extract(data, ROW0_INDEX, 2))
                cpu.requestInterrupt(Cpu.Interrupt.JOYPAD);

            regP1 = Bits.set(regP1, ROW0_INDEX, !Bits.test(data, ROW0_INDEX));
            regP1 = Bits.set(regP1, ROW1_INDEX, !Bits.test(data, ROW1_INDEX));
        }
    }

    /**
     * Simulates the pressure of a key.
     *
     * @param key which is pressed
     */
    public void keyPressed(Key key) {
        int index = key.index();

        if (index < ROW0_INDEX) {
            if (!Bits.test(row0, index) && Bits.test(regP1, ROW0_INDEX))
                cpu.requestInterrupt(Cpu.Interrupt.JOYPAD);
            row0 = Bits.set(row0, index, true);
        }
        else {
            if (!Bits.test(row1, index - ROW0_INDEX) && Bits.test(regP1, ROW1_INDEX))
                cpu.requestInterrupt(Cpu.Interrupt.JOYPAD);

            row1 = Bits.set(row1, index - ROW0_INDEX, true);
        }
        regP1 |= row0 | row1;
    }

    /**
     * Simulates the releasing of a key.
     *
     * @param key which is released
     */
    public void keyReleased(Key key) {
        int index = key.index();

       if (index < ROW0_INDEX)
           row0 = Bits.set(row0, index, false);
       else
           row1 = Bits.set(row1, index - ROW0_INDEX, false);

        regP1 &= (MASK | row0 | row1);
    }

    private int columnState() {
        int line0 = Bits.test(regP1, ROW0_INDEX) ? row0 : 0;
        int line1 = Bits.test(regP1, ROW1_INDEX) ? row1 : 0;

        return MASK | line0 | line1;
    }
}
