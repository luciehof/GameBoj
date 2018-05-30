// Gameboj stage 4

package ch.epfl.gameboj.component.cpu;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import ch.epfl.gameboj.component.Component;

public final class Assembler {
    private ByteArrayOutputStream s = new ByteArrayOutputStream();
    private int cycles = 0;
    
    public Assembler emit(Opcode op) {
        if (op.kind == Opcode.Kind.PREFIXED)
            s.write(0xCB);
        s.write(op.encoding);
        cycles += op.cycles;
        return this;
    }

    public Assembler emit(Opcode op, int n) {
        assert op.kind == Opcode.Kind.DIRECT;
        switch (op.totalBytes) {
        case 2: {
            assert (n & 0xFF) == n;
            emit(op);
            s.write(n);
        } break;
        
        case 3: {
            assert (n & 0xFFFF) == n;
            emit(op);
            s.write(n & 0xFF);
            s.write(n >> 8);
        } break;
        
        default:
            throw new Error("invalid opcode size: " + op.totalBytes);
        }
        return this;
    }
    
    public Assembler emitData8(int n) {
        assert (n & 0xFF) == n;
        s.write(n);
        return this;
    }
    
    public Program program() {
        return new Program(s.toByteArray(), cycles);
    }
    
    public static class Program {
        private final int cycles, bytesCount;
        private final Component rom;
        
        public Program(byte[] bytes, int cycles) {
            this.cycles = cycles;
            this.bytesCount = bytes.length;
            this.rom = new ProgRom(bytes);
        }
        
        public Component rom() { return rom; }
        public int cycles() { return cycles; }
        public int bytes() { return bytesCount; }
    }
    
    private static class ProgRom implements Component {
        private final byte[] p;

        public ProgRom(byte[] p) {
            this.p = Arrays.copyOf(p, p.length);
        }

        @Override
        public int read(int address) {
            if (0 <= address && address < p.length)
                return Byte.toUnsignedInt(p[address]);
            else
                return 0x100;
        }

        @Override
        public void write(int address, int data) { }
    }
}
