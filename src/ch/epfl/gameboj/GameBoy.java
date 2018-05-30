package ch.epfl.gameboj;

import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.lcd.LcdController;
import ch.epfl.gameboj.component.memory.BootRomController;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

import java.util.Objects;

import static ch.epfl.gameboj.AddressMap.*;

/**
 * the gameBoy
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */

public class GameBoy {

    public static final long CYCLES_PER_SECOND = 1 << 20;
    public static final double CYCLES_PER_NANOSECOND = CYCLES_PER_SECOND * 1e-9;

    private final Bus bus = new Bus();
    private final Ram workRam;
    private final RamController workRamController;
    private final RamController echoRamController;
    private final BootRomController bootRomController;
    private final Timer timer;
    private final Cpu cpu;
    private final LcdController lcdController;
    private final Joypad joypad;
    private long simulatedCycles;

    /**
     * Creates a game boy with a bus, a workRam and a copy of the workRam which
     * are attached to the bus.
     *
     * @param cartridge {@link Cartridge} containing the game
     */
    public GameBoy(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);

        workRam = new Ram(WORK_RAM_SIZE);
        workRamController = new RamController(workRam, WORK_RAM_START,
                WORK_RAM_END);
        echoRamController = new RamController(workRam, ECHO_RAM_START,
                ECHO_RAM_END);
        bootRomController = new BootRomController(cartridge);
        cpu = new Cpu();
        timer = new Timer(cpu);
        lcdController = new LcdController(cpu);
        joypad = new Joypad(cpu);

        simulatedCycles = 0;

        workRamController.attachTo(bus);
        echoRamController.attachTo(bus);
        bootRomController.attachTo(bus);
        timer.attachTo(bus);
        cpu.attachTo(bus);
        lcdController.attachTo(bus);
        joypad.attachTo(bus);
    }

    /**
     * Bus getter.
     *
     * @return the gameBoy's bus
     */
    public Bus bus() {
        return bus;
    }

    /**
     * Cpu getter.
     *
     * @return the gameBoy's cpu
     */
    public Cpu cpu() {
        return cpu;
    }

    /**
     * LcdController getter.
     *
     * @return the gameBoy's lcd controller
     */
    public LcdController lcdController() {
        return lcdController;
    }

    /**
     * Joypad getter.
     *
     * @return the gaameBoy's joypad
     */
    public Joypad joypad() {
        return joypad;
    }

    /**
     * Simulates the operation of the gameBoy until the given cycle - 1.
     *
     * @param cycle
     * @throws IllegalArgumentException if the number of simulated cycles is
     *                                  already strictly bigger than te given
     *                                  number of cycles to simulate
     */
    public void runUntil(long cycle) {
        Preconditions.checkArgument(simulatedCycles <= cycle);

        while (simulatedCycles < cycle) {
            timer.cycle(simulatedCycles);
            lcdController.cycle(simulatedCycles);
            cpu.cycle(simulatedCycles);
            simulatedCycles++;
        }
    }

    /**
     * Gets the number of simulated cycles.
     *
     * @return a long corresponding to the number of simulated cycles
     */
    public long cycles() {
        return simulatedCycles;
    }

    /**
     * Timer getter.
     *
     * @return the gameBoy's timer
     */
    public Timer timer() {
        return timer;
    }
}

