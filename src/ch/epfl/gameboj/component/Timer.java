package ch.epfl.gameboj.component;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Alu;
import ch.epfl.gameboj.component.cpu.Cpu;

import java.util.Objects;

/**
 * a timer
 *
 * @author Marie Jaillot (270130)
 */

public final class Timer implements Component, Clocked {

    private final Cpu cpu;
    private int mainCounter;
    private int TIMA;
    private int TMA;
    private int TAC;
    private static final int MAX_8_BITS = 0xFF;

    /**
     * Constructs a timer for a game boy, associated to the given cpu.
     *
     * @param cpu which we associate a timer to
     * @throws NullPointerException if the given cpu is null
     */
    public Timer(Cpu cpu) {
        Objects.requireNonNull(cpu);

        mainCounter = 0;
        TIMA = 0;
        TMA = 0;
        TAC = 0;
        this.cpu = cpu;
    }

    @Override public void cycle(long cycle) {
        boolean s0 = state();
        mainCounter = Bits.clip(16, mainCounter + 4);
        incIfChange(s0);
    }

    @Override public int read(int address) {
        Preconditions.checkBits16(address);

        switch (address) {
        case AddressMap.REG_DIV:
            return Bits.extract(mainCounter, 8, 8);
        case AddressMap.REG_TIMA:
            return TIMA;
        case AddressMap.REG_TMA:
            return TMA;
        case AddressMap.REG_TAC:
            return TAC;
        default:
            return NO_DATA;
        }
    }

    @Override public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        boolean s0 = state();

        switch (address) {
        case AddressMap.REG_DIV:
            mainCounter = 0;
            incIfChange(s0);
            break;
        case AddressMap.REG_TIMA:
            TIMA = data;
            break;
        case AddressMap.REG_TMA:
            TMA = data;
            break;
        case AddressMap.REG_TAC:
            TAC = data;
            incIfChange(s0);
            break;
        }
    }

    /**
     * Returns the timer's current state.
     *
     * @return boolean, true if the activated bit of the timer is 1
     */
    private boolean state() {
        boolean timerActivated = Bits.test(TAC, 2);
        boolean[] states = { Bits.test(mainCounter, 9),
                Bits.test(mainCounter, 3),
                Bits.test(mainCounter, 5),
                Bits.test(mainCounter, 7) };
        return states[Bits.clip(2, TAC)] && timerActivated;
    }

    /**
     * Increments the secondary timer (TIMA) iff the previous state is true and
     * the current state is false (iff a specific bit of mainCounter changes
     * from 1 to 0).
     *
     * @param previousState boolean, the previous state
     */
    private void incIfChange(boolean previousState) {
        if (previousState && !state()) {
            if (TIMA == MAX_8_BITS) {
                cpu.requestInterrupt(Cpu.Interrupt.TIMER);
                TIMA = TMA;
            } else {
                ++TIMA;
            }
        }
    }
}
