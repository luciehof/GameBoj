package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.*;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.memory.Ram;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a Lcd controller
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */
public final class LcdController implements Component, Clocked {

    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;
    private static final int MODE2_DURATION = 20;
    private static final int MODE3_DURATION = 43;
    private static final int MODE0_DURATION = 51;
    private static final int LINE_DRAW_DURATION = 114;
    private static final int LY_MAX_VALUE = 153;
    private static final int BG_SIZE = 32, BG_PIXEL_SIZE = 256;
    private static final int WIN_SIZE = 20;
    private static final int TILE_SIZE = 8, BIG_TILE_HEIGHT = 16;
    private static final int NUMBER_OF_SPRITES = 40;
    private static final int MAX_SPRITES_PER_LINE = 10;
    private static final int SPRITE_X_COORDINATE_INDEX = 1;
    private static final int SPRITE_TILE_INDEX = 2;
    private static final int SPRITE_CARACTERISICS_INDEX = 3;
    private static final int SPRITE_ATTRIBUTES_SIZE = 4;
    private static final int TILE_SHIFT_INDEX = 0x80;
    private static final int SET_WX = 7;
    private static final int LCDC_ADDRESS = 0xFF40, STAT_ADDRESS = 0xFF41, LY_ADDRESS = 0xFF44, LYC_ADDRESS = 0xFF45, DMA_ADDRESS = 0xFF46;

    private Cpu cpu;
    private Ram videoRam;
    private Ram oam;
    private Bus bus;
    private LcdImage.Builder nextImageBuilder;
    private long nextNonIdleCycle;
    private int winY;
    private LcdImage currentImage;
    private int copyIndex;
    private boolean turnOnScreen;

    //DEBUG
    Mode currentMode;
    long currentCycle;
    long lastImageCycle = 0;
    int indexImage = 0;

    private long cycleOnWakeUp;
    //DEBUG

    private static final RegisterFile<Reg> regFile = new RegisterFile<>(
            Reg.values());

    /**
     * LCD controller's 8 bits registers
     */
    private enum Reg implements Register {
        LCDC, STAT, SCY, SCX, LY, LYC, DMA, BGP, OBP0, OBP1, WY, WX
    }

    private enum LCDCBits implements Bit {
        BG, OBJ, OBJ_SIZE, BG_AREA, TILE_SOURCE, WIN, WIN_AREA, LCD_STATUS
    }

    private enum STATBits implements Bit {
        MODE0, MODE1, LYC_EQ_LY, INT_MODE0, INT_MODE1, INT_MODE2, INT_LYC
    }

    private enum SpriteBits implements Bit {
        NOT_USED0, NOT_USED1, NOT_USED2, NOT_USED3,
        PALETTE, FLIP_H, FLIP_V, BEHIND_BG
    }

    private enum Mode {
        M0, M1, M2, M3
    }

    /**
     * Constructs an lcd controller belonging to the given cpu.
     *
     * @param cpu Cpu that is going to contain the lcd controller in construction
     */
    public LcdController(Cpu cpu) {
        this.cpu = cpu;
        videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
        oam = new Ram(AddressMap.OAM_RAM_SIZE);
        currentImage = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT).build();
        nextNonIdleCycle = Long.MAX_VALUE;
        winY = 0;
        copyIndex = LCD_WIDTH;

        // NIZAR
        nextImageBuilder = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT);
    }

    @Override public void cycle(long cycle) {
        currentCycle = cycle;

        assert (cycle
                <= nextNonIdleCycle) : "Current cycle is bigger than nextNonIdleCycle";

        // gestion allumage de l'écran : allumage
        if (nextNonIdleCycle == Long.MAX_VALUE && regFile
                .testBit(Reg.LCDC, LCDCBits.LCD_STATUS)) {
            turnOnScreen = true;
            setMode(Mode.M2);
            modifLY_LYC(Reg.LY, 0);     // Un peu sale...? Il semblerait que LY ne soit pas toujurs à 0 à l'allumage et ca pause un probleme pour initialiser nextImageBuilder
            System.out.println("LY allumage --------------->>  " +regFile.get(Reg.LY));
            nextNonIdleCycle = cycle + MODE2_DURATION;
            winY = 0;
            // System.out.println("allumage " + cycle);
            // System.out.println("nextNonIdleCycle " + nextNonIdleCycle);

            //NIZAR
            cycleOnWakeUp = cycle % (LINE_DRAW_DURATION * LY_MAX_VALUE);
        }

        if (copyIndex < 160) {
            oam.write(copyIndex,
                    bus.read(Bits.make16(regFile.get(Reg.DMA), copyIndex)));
            copyIndex += 1;
            //nextNonIdleCycle += 1;
        }

        if (cycle == nextNonIdleCycle)
            reallyCycle();
    }

    //TEST
    boolean reallycycle = true;

    /*private void reallyCycle() {
        //TEST
        if (reallycycle) {
            //System.out.println("currentCycle " + currentCycle + " mode " + getMode() + "  LY " + regFile.get(Reg.LY));
            reallycycle = false;
        }

        int currentLine = regFile.get(Reg.LY);

        if (currentLine < LCD_HEIGHT) {

            switch (getMode()) {
            // MODE 0
            case M0: {
                //test
                currentMode = getMode();

                nextNonIdleCycle += MODE2_DURATION;

                if (!turnOnScreen)
                    modifLY_LYC(Reg.LY, currentLine + 1);
                else
                    turnOnScreen = false;

                if (regFile.get(Reg.LY) < LCD_HEIGHT)
                    setMode(Mode.M2);
            }
            break;

            // MODE 2
            case M2: {
                //test
                currentMode = getMode();

                nextNonIdleCycle += MODE3_DURATION;

                if (regFile.get(Reg.LY) == 0) {
                    nextImageBuilder = new LcdImage.Builder(LCD_WIDTH,
                            LCD_HEIGHT);
                    indexImage += 1;
                    lastImageCycle = currentCycle - MODE2_DURATION;
                    winY = 0;
                }

                setMode(Mode.M3);
                nextImageBuilder.setLine(currentLine, computeLine(currentLine));
            }
            break;

            // MODE 3
            case M3: {
                //test
                currentMode = getMode();

                nextNonIdleCycle += MODE0_DURATION;

                setMode(Mode.M0);
            }
            break;

            /*case M1: {
                modifLY_LYC(Reg.LY, currentLine);
                setMode(Mode.M2);
                nextNonIdleCycle += MODE2_DURATION;
            }
            break;
            }
        } else {
            // MODE 1
            nextNonIdleCycle += LINE_DRAW_DURATION;

            if (currentLine == LCD_HEIGHT) {
                setMode(Mode.M1);

                currentImage = nextImageBuilder.build();
                cpu.requestInterrupt(Cpu.Interrupt.VBLANK);

                //TEST
                //if (indexImage == 2) System.out.println("cycles:  " + currentCycle + "  since frame:     " + (currentCycle - lastImageCycle) + "| request VBLANK interrupt");

                //nextImageBuilder = null;
            }


            if (currentLine < LY_MAX_VALUE)
                modifLY_LYC(Reg.LY, currentLine + 1);
            else {
                setMode(Mode.M2);
                nextNonIdleCycle += MODE2_DURATION;
                modifLY_LYC(Reg.LY, 0);
            }
        }
    }*/

    private void raiseStatInterrupt(STATBits m) {
        if (regFile.testBit(Reg.STAT, m))
            cpu.requestInterrupt(Cpu.Interrupt.LCD_STAT);

    }

    private void reallyCycle() {
        long cycleMode = nextNonIdleCycle - cycleOnWakeUp;
        int lineIndex = (int) (cycleMode / LINE_DRAW_DURATION) % (LY_MAX_VALUE);
        switch (getMode(cycleMode)) {
        case M0: {
            nextNonIdleCycle += MODE0_DURATION;
            setMode(Mode.M0);
            raiseStatInterrupt(STATBits.INT_MODE0);

        }
        break;
        case M1: {
            nextNonIdleCycle += 114;
            setMode(Mode.M1);
            if (lineIndex == LCD_HEIGHT)
                cpu.requestInterrupt(Cpu.Interrupt.VBLANK);
            modifLY_LYC(Reg.LY, lineIndex);
            raiseStatInterrupt(STATBits.INT_MODE1);
        }
        break;
        case M2: {
            nextNonIdleCycle += MODE2_DURATION;
            if (lineIndex == 0) {
                System.out.println("initiate image builder");
                currentImage = nextImageBuilder.build();
                nextImageBuilder = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT);
                winY = 0;
            }
            setMode(Mode.M2);
            raiseStatInterrupt(STATBits.INT_MODE2);
            modifLY_LYC(Reg.LY, lineIndex);

        }
        break;
        case M3: {
            nextNonIdleCycle += MODE3_DURATION;
            nextImageBuilder.setLine(lineIndex, computeLine(lineIndex));
            setMode(Mode.M3);
        }
        break;
        }
    }

    @Override public void attachTo(Bus bus) {
        this.bus = bus;
        Component.super.attachTo(bus);
    }

    //NIZAR
    private Mode getMode(long cycle) {
        long step = cycle % LINE_DRAW_DURATION;
        int lineIndex = (int) (cycle / LINE_DRAW_DURATION) % LY_MAX_VALUE;
        if (lineIndex >= LCD_HEIGHT)
            return Mode.M1;
        else if (step < MODE2_DURATION && step >= 0)
            return Mode.M2;
        else if (step < MODE2_DURATION + MODE3_DURATION && step >= MODE2_DURATION)
            return Mode.M3;
        else
            return Mode.M0;
    }
    //

    /*private Mode getMode() {
        int statValue = regFile.get(Reg.STAT);
        int mode = Bits.clip(2, statValue);

        return Mode.values()[mode];
    }*/

    private void setMode(Mode mode) {
        //TEST
        //if (indexImage == 2) System.out.println("cycles:  " + currentCycle + "  since frame:     " + (currentCycle - lastImageCycle) + " | mode: " + getMode().toString() + " -> " + mode.toString());

        regFile.setBit(Reg.STAT, STATBits.MODE0, Bits.test(mode.ordinal(), 0));
        regFile.setBit(Reg.STAT, STATBits.MODE1, Bits.test(mode.ordinal(), 1));

        if ((mode == Mode.M0 && regFile.testBit(Reg.STAT, STATBits.INT_MODE0))
                || (mode == Mode.M1 && regFile.testBit(Reg.STAT, STATBits.INT_MODE1))
                || (mode == Mode.M2 && regFile.testBit(Reg.STAT, STATBits.INT_MODE2)))
            cpu.requestInterrupt(Cpu.Interrupt.LCD_STAT);
    }

    @Override public int read(int address) {
        Preconditions.checkBits16(address);

        if (AddressMap.VIDEO_RAM_START <= address
                && address < AddressMap.VIDEO_RAM_END)
            return videoRam.read(address - AddressMap.VIDEO_RAM_START);

        if (AddressMap.OAM_START <= address && address < AddressMap.OAM_END)
            return oam.read(address - AddressMap.OAM_START);

        if (AddressMap.REGS_LCDC_START <= address
                && address < AddressMap.REGS_LCDC_END) {
            Reg r = Reg.values()[address - AddressMap.REGS_LCDC_START];

            return regFile.get(r);
        }
        return NO_DATA;
    }

    @Override public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        /*boolean extinction = !Bits.test(data, 7);*/
        boolean extinction = !regFile.testBit(Reg.LCDC, LCDCBits.LCD_STATUS);

        if (AddressMap.VIDEO_RAM_START <= address
                && address < AddressMap.VIDEO_RAM_END)
            videoRam.write(address - AddressMap.VIDEO_RAM_START, data);

        else if (AddressMap.OAM_START <= address
                && address < AddressMap.OAM_END)
            oam.write(address - AddressMap.OAM_START, data);

        else if (AddressMap.REGS_LCDC_START <= address
                && address < AddressMap.REGS_LCDC_END) {
            Reg r = Reg.values()[address - AddressMap.REGS_LCDC_START];

            switch (address) {
            case LCDC_ADDRESS:
                regFile.set(Reg.LCDC, data);
                if (extinction) {
                    // passage de LCD en mode 0
                    setMode(Mode.M0);
                    //LY --> 0
                    modifLY_LYC(Reg.LY, 0);
                    nextNonIdleCycle = Long.MAX_VALUE;
                }
                break;
            case STAT_ADDRESS:
                int lsbSTAT = Bits.clip(3, regFile.get(Reg.STAT));
                int maskLsb = 0b1111_1000;
                int realData = data & maskLsb;
                regFile.set(Reg.STAT, lsbSTAT | realData);
                break;
            case LY_ADDRESS:
                break;
            case LYC_ADDRESS:
                modifLY_LYC(Reg.LYC, data);
                break;
            case DMA_ADDRESS:
                regFile.set(Reg.DMA, data);
                copyIndex = 0;
                break;
            default:
                regFile.set(r, data);
            }
        }
    }

    private void modifLY_LYC(Reg LYorLYC, int data) {
        // TEST
        //if (indexImage == 2) System.out.println ("cycles:  " + currentCycle + "  since frame:     " + (currentCycle - lastImageCycle) + " | LY:   "+ regFile.get(Reg.LY) + " -> " + data);

        regFile.set(LYorLYC, data);

        regFile.setBit(Reg.STAT, STATBits.LYC_EQ_LY,
                regFile.get(Reg.LY) == regFile.get(Reg.LYC));

        if (regFile.testBit(Reg.STAT, STATBits.INT_LYC))
            cpu.requestInterrupt(Cpu.Interrupt.LCD_STAT);
    }

    /**
     * Returns the image currently displayed on the screen.
     *
     * @return LcdImage, the image currently displayed on the screen
     */
    public LcdImage currentImage() {
        // retourne toujours une image non nulle de 160×144 pixels
        return currentImage;
    }

    private LcdImageLine computeLine(int indexLine) {
        Objects.checkIndex(indexLine, LCD_HEIGHT);

        int lineToCompute = Bits.clip(8, indexLine + regFile.get(Reg.SCY));
        LcdImageLine bgLine = new LcdImageLine.Builder(BG_PIXEL_SIZE).build();

        boolean drawBg = regFile.testBit(Reg.LCDC, LCDCBits.BG);
        int wy = regFile.get(Reg.WY);
        int wx = regFile.get(Reg.WX) - SET_WX;
        boolean drawWin = regFile.testBit(Reg.LCDC, LCDCBits.WIN) && 0 <= wx
                && wx < LCD_WIDTH && wy <= indexLine;
        boolean drawSprite = regFile.testBit(Reg.LCDC, LCDCBits.OBJ);
        int palette = regFile.get(Reg.BGP);

        //======================================================================
        // Background drawing

        /*if (drawBg) {
            int plageBg = regFile.testBit(Reg.LCDC, LCDCBits.BG_AREA) ?
                    AddressMap.BG_DISPLAY_DATA[1] :
                    AddressMap.BG_DISPLAY_DATA[0];
            bgLine = constructFromTiles(BG_SIZE, plageBg, lineToCompute)
                    .extractWrapped(regFile.get(Reg.SCX), LCD_WIDTH)
                    .mapColors(palette);
        }*/
        LcdImageLine completeLine = bgLine;

        //======================================================================
        // Window drawing

        /*if (drawWin) {
            int plageWin = regFile.testBit(Reg.LCDC, LCDCBits.WIN_AREA) ?
                    AddressMap.BG_DISPLAY_DATA[1] :
                    AddressMap.BG_DISPLAY_DATA[0];

            LcdImageLine winLine = constructFromTiles(WIN_SIZE, plageWin, winY)
                    .shift(wx)
                    .mapColors(palette);

            completeLine = completeLine.join(winLine, wx);
            winY += 1;
        }*/

        //======================================================================
        // Sprites

        if (drawSprite) {
            LcdImageLine spritesLineBG = new LcdImageLine.Builder(LCD_WIDTH)
                    .build();
            LcdImageLine spritesLineFront = new LcdImageLine.Builder(LCD_WIDTH)
                    .build();

            int[] spritesTable = spritesIntersectingLine(lineToCompute);

            for (int spriteIndex : spritesTable) {

                LcdImageLine.Builder singleSpriteLineBuilder = new LcdImageLine.Builder(
                        LCD_WIDTH);

                int spriteCaracteristics = oam.read(SPRITE_CARACTERISICS_INDEX
                        + spriteIndex * SPRITE_ATTRIBUTES_SIZE);
                boolean behindBg = Bits
                        .test(spriteCaracteristics, SpriteBits.BEHIND_BG);
                boolean flipV = Bits.test(spriteCaracteristics, SpriteBits.FLIP_V);
                boolean flipH = Bits.test(spriteCaracteristics, SpriteBits.FLIP_H);
                boolean spritePalette = Bits
                        .test(spriteCaracteristics, SpriteBits.PALETTE);
                int paletteSprite = spritePalette ?
                        regFile.get(Reg.OBP1) :
                        regFile.get(Reg.OBP0);

                int coordY = oam.read(spriteIndex * SPRITE_ATTRIBUTES_SIZE)
                        - BIG_TILE_HEIGHT;
                int coordX = oam.read(SPRITE_X_COORDINATE_INDEX
                        + spriteIndex * SPRITE_ATTRIBUTES_SIZE) - TILE_SIZE;

                int spriteLine = lineToCompute - coordY;
                int spriteHeight = regFile
                        .testBit(Reg.LCDC, LCDCBits.OBJ_SIZE) ?
                        BIG_TILE_HEIGHT :
                        TILE_SIZE;
                spriteLine = flipV ?
                        spriteHeight - 1 - spriteLine :
                        spriteLine;

                int lsb = getByte(spriteIndex, spriteLine, true, flipH);
                int msb = getByte(spriteIndex, spriteLine, false, flipH);

                LcdImageLine singleSpriteLine = singleSpriteLineBuilder
                        .setBytes(0, msb, lsb).build().shift(coordX)
                        .mapColors(paletteSprite);

                if (behindBg)
                    spritesLineBG = singleSpriteLine.below(spritesLineBG);
                else
                    spritesLineFront = singleSpriteLine.below(spritesLineFront);
            }

            //BitVector bgLineOpacity = completeLine.getOpacity().or(spritesLineBG.getOpacity().not());
           // LcdImageLine bgLineWitSprites = spritesLineBG.below(completeLine, bgLineOpacity);

            completeLine = spritesLineBG.below(spritesLineFront);
            //completeLine = bgLineWitSprites.below(spritesLineFront);
        }

        return completeLine;
    }

    private int getByte(int indexSprite, int currentLine, boolean lsb,
            boolean flipH) {
        int lsbOrMsb = lsb ? 0 : 1;
        int plageTile = AddressMap.TILE_SOURCE[1];
        int indexTile = oam
                .read(SPRITE_TILE_INDEX + indexSprite * SPRITE_ATTRIBUTES_SIZE);
        int indexLineInTile = currentLine % 8;
        int addressByte = plageTile + indexTile * 16 + currentLine * 2;

        if (flipH)
            return read(addressByte + lsbOrMsb);
        else
            return Bits.reverse8(read(addressByte + lsbOrMsb));
    }

    private LcdImageLine constructFromTiles(int size, int plage,
            int indexLine) {
        LcdImageLine.Builder currentLineBuilder = new LcdImageLine.Builder(
                size * TILE_SIZE);

        int tileLineIndex = Bits.extract(indexLine, 3, 5);
        int lineInTileIndex = Bits.clip(3, indexLine);

        int plageTile = regFile.testBit(Reg.LCDC, LCDCBits.TILE_SOURCE) ?
                AddressMap.TILE_SOURCE[1] :
                AddressMap.TILE_SOURCE[0];

        for (int column = 0; column < size; ++column) {

            int indexTile = read(plage + column + tileLineIndex * BG_SIZE);

            if (plageTile == AddressMap.TILE_SOURCE[0])
                indexTile = Bits.clip(8, indexTile + TILE_SHIFT_INDEX);

            int bytesAddress = plageTile + indexTile * 16 + lineInTileIndex * 2;
            int LSB8 = read(bytesAddress);
            int MSB8 = read(bytesAddress + 1);

            currentLineBuilder
                    .setBytes(column, Bits.reverse8(MSB8), Bits.reverse8(LSB8));

        }

        return currentLineBuilder.build();
    }

    private int[] spritesIntersectingLine(int currentLine) {
        int spriteHeight = regFile.testBit(Reg.LCDC, LCDCBits.OBJ_SIZE) ?
                BIG_TILE_HEIGHT : TILE_SIZE;
        int[] infoSprites = new int[MAX_SPRITES_PER_LINE];
        int nbSprite = 0;

        for (int i = 0; i < NUMBER_OF_SPRITES; ++i) {
            int coordY = oam.read(i * SPRITE_ATTRIBUTES_SIZE) - BIG_TILE_HEIGHT;
            int coordX = oam.read(SPRITE_X_COORDINATE_INDEX + i * SPRITE_ATTRIBUTES_SIZE);

            boolean spriteInLine = coordY <= currentLine
                    && currentLine < coordY + spriteHeight;

            if (nbSprite < MAX_SPRITES_PER_LINE && spriteInLine) {
                infoSprites[nbSprite] = Bits.make16(coordX, i);
                nbSprite += 1;
            }
        }

        Arrays.sort(infoSprites, 0, nbSprite);
        int[] indexSprite = new int[nbSprite];

        for (int i = 0; i < nbSprite; ++i)
            indexSprite[i] = Bits.clip(8, infoSprites[i]);

        return indexSprite;
    }
}