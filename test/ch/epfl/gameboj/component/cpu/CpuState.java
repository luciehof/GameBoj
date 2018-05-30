// Gameboj stage 4

package ch.epfl.gameboj.component.cpu;

import java.util.Arrays;

public final class CpuState {
    private final int pc, sp;
    private final int a, f, b, c, d, e, h, l;
    
    public static CpuState of(int pc, int sp, int a, int f, int b, int c, int d, int e, int h, int l) {
        return new CpuState(pc, sp, a, f, b, c, d, e, h, l);
    }
    
    public static CpuState of(int pc, int sp, long afbcdehl) {
        return new CpuState(pc,
                sp,
                b(afbcdehl, 7),
                b(afbcdehl, 6),
                b(afbcdehl, 5),
                b(afbcdehl, 4),
                b(afbcdehl, 3),
                b(afbcdehl, 2),
                b(afbcdehl, 1),
                b(afbcdehl, 0));
    }

    private static int b(long v, int s) {
        return (int) ((v >>> (s << 3)) & 0xFF);
    }
    
    public static CpuState ofArray(int[] a) {
        if (a.length < 10)
            a = Arrays.copyOf(a, 10);
        return new CpuState(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9]);
    }
    
    public CpuState(int pc, int sp, int a, int f, int b, int c, int d, int e, int h, int l) {
        this.pc = pc;
        this.sp = sp;
        this.a = a;
        this.f = f;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.h = h;
        this.l = l;
    }

    public int[] toArray() {
        return new int[] { pc, sp, a, f, b, c, d, e, h, l };
    }
    
    public int pc() { return pc; }
    public int sp() { return sp; }
    
    public int a() { return a; }
    public int f() { return f; }
    public int b() { return b; }
    public int c() { return c; }
    public int d() { return d; }
    public int e() { return e; }
    public int h() { return h; }
    public int l() { return l; }

    public int af() { return combine(a, f); }
    public int bc() { return combine(b, c); }
    public int de() { return combine(d, e); }
    public int hl() { return combine(h, l); }
    
    private static int combine(int h, int l) {
        return (h << 8) | l;
    }
    
    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof CpuState) {
            CpuState that = (CpuState)thatO;
            return Arrays.equals(this.toArray(), that.toArray());
        } else
            return false;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }
    
    @Override
    public String toString() {
        return String.format("PC: %04X SP: %04X AF: %04X BC: %04X DE: %04X HL: %04X",
                pc(), sp(), af(), bc(), de(), hl());
    }
    
    public String toJavaString() {
        return String.format("CpuState.of(0x%04X, 0x%04X, 0x%016XL)",
                pc(), sp(),
                ((long)a() << (7 << 3))
                | ((long)f() << (6 << 3))
                | ((long)b() << (5 << 3))
                | ((long)c() << (4 << 3))
                | ((long)d() << (3 << 3))
                | ((long)e() << (2 << 3))
                | ((long)h() << (1 << 3))
                | ((long)l() << (0 << 3)));
    }
}
