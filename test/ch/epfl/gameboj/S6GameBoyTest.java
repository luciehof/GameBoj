// Gameboj stage 6

package ch.epfl.gameboj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cartridge.CartridgeTest;
import ch.epfl.gameboj.component.cpu.Cpu;

public final class S6GameBoyTest {
    @Test
    void constructorFailsWithNullCartridge() {
        assertThrows(NullPointerException.class, () -> {
            new GameBoy(null);
        });
    }

    @Test
    void runUntilWorksWhenNotAdvancing() {
        GameBoy g = new GameBoy(CartridgeTest.cartridgeWithData(new byte[0x8000]));
        for (long c = 0; c <= 1000; ++c) {
            g.runUntil(c);
            g.runUntil(c);
            g.runUntil(c);
        }
    }

    @Test
    void runUntilFailsWhenGoingBackwardsInTime() {
        GameBoy g = new GameBoy(CartridgeTest.cartridgeWithData(new byte[0x8000]));
        g.runUntil(2018);
        assertThrows(IllegalArgumentException.class, () -> {
            g.runUntil(2017); 
        });
    }

    @Test
    void blarggsTestsWork() throws IOException {
        Iterator<Integer> reqCycles = List.of(
                5000000, 5000000, 5000000, 10000000,
                10000000, 5000000, 5000000, 10000000, 20000000, 20000000,
                30000000, 5000000).iterator();
        
        Base64.Decoder b64Decoder = Base64.getDecoder();
        StringBuilder s = new StringBuilder();
        for (String e: BASE64_BLARGGS_TESTS) {
            try (InputStream inStream = new GZIPInputStream(new ByteArrayInputStream(b64Decoder.decode(e)))) {
                byte[] romData = inStream.readAllBytes();
                assert romData.length == 0x8000;
                GameBoy gb = new GameBoy(CartridgeTest.cartridgeWithData(romData));
                RecordingComponent recordingC = new RecordingComponent(s);
                recordingC.attachTo(gb.bus());
                int cycles = reqCycles.next();
                while (gb.cycles() < cycles) {
                    long nextCycles = Math.min(gb.cycles() + 17_556, cycles);
                    gb.runUntil(nextCycles);
                    assertEquals(nextCycles, gb.cycles());
                    gb.cpu().requestInterrupt(Cpu.Interrupt.VBLANK);
                }
            }
        }
        assertEquals(BLARGGS_TESTS_EXPECTED_OUTPUT, s.toString());
    }

    private final static class RecordingComponent implements Component {
        private final StringBuilder buf;

        public RecordingComponent(StringBuilder buf) {
            this.buf = buf;
        }

        @Override
        public int read(int address) {
            return NO_DATA;
        }

        @Override
        public void write(int address, int data) {
            if (address == 0xFF01)
                buf.append((char) data);
        }
    }

    private static String BLARGGS_TESTS_EXPECTED_OUTPUT = "01-special\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "02-interrupts\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "03-op sp,hl\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "04-op r,imm\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "05-op rp\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "06-ld r,r\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "07-jr,jp,call,ret,rst\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "08-misc instrs\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "09-op r,r\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "10-bit ops\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "11-op a,(hl)\n" + 
            "\n" + 
            "\n" + 
            "Passed\n" + 
            "instr_timing\n" + 
            "\n" + 
            "\n" + 
            "Passed\n";

    // Blargg's tests, gzipped and encoded in base64
    private static String[] BASE64_BLARGGS_TESTS = new String[] {
            // 01-special.gb
            "H4sICH9qrVoAAzAxLXNwZWNpYWwuZ2IA7ZVfbBRFGMDnrr2llOvRAg8jtLDlTy0NDatGvMR1U6ABo2CJ"
            + "D5gQjNtIF6vntVotV7iuLU2Mf14kxkRCouFF44uEF6H0Zbd7d2WFIZJAsyY03EXbyAFNV11FxDu/2WvB"
            + "PzwajeH7ZWdnv5lvvn8zN0fIPY61OHjuuqadrSILynrIEAmTKlJRs+Ktt0nk0tn41MTE14dHRvY+H49c"
            + "Oztx6fDHI8MPKX+xMHD7S9t+9V+N/R9gaw0xItVNi2rFX5dUiT8nLGLUkxaLBP/rwBAEQRAEQRAEQRAE"
            + "QRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQe4xLHF0PCTdWiGRc4GvThP7xGlPuczKDPcx"
            + "8SdfKLJFhnKhNOLaXpJVG/1csv9uy6ZEKWYHsoPZg9kh20tfnKwfKm5pfqp5Z/PnDRVnomfuO7N0hVT9"
            + "XGL6+ta+6ZNt+6eHnjkwLa4Rv20ornx13SvrunPnTdfuzwedZL7MoeWKTYOKlQ84tleoTO0yWMCBEBRx"
            + "tq/0+xwbM654tIx9ajSdEH9xbTZtsMUmqzPZKpOdMmyPrTXn1sCnm/ZMVjRM20vQSa+P5rz9dMI7QL/2"
            + "kvSi10/Pe+kEqPWZdMxLb3qSfu+lW7fTa2yX4Z15hD1kulMLC5XRYGZeRrICjpemYS8NAwsie4YvPGtG"
            + "KtmzZkaCYCATYX35IenWYFsCxD47N+mtWtWUL3f68yFIUIB4JtlLJg0WSY5tN2yIdZKNGjvae3o69pCc"
            + "QqyXRm+WO3Jj6LHGb+hCPtfaFe+Ymwk5XTcFZ69+onE+rIQiwBvUC4FzfNqbGVjvzQxCOwhtaD1P/Hb7"
            + "BrZvZmBEXDQzeEqsmjk4LFbMDJ0Uy6w2gzmmNWw2QbYLEsfFn+0fHihezhNHIdl50BZCK0JrUAagFbNr"
            + "lH3Z1fXHx9j7BtNNdmSMtRmeUsZWGKVzw/hJYZ+M+hGDJY/1jrrW6TGPR3zUcAsBR4zwxLa0d8Y69lSS"
            + "nH1HEldBXVr9LfTrVBV84mkx3rG3/bXO3lIVvhyVZbkQHB02rbOj9MeSVhlodXf1dM5p0cCckj9fWb6t"
            + "Vdyxed3j20iuvtxSyJU/TVeEdrTtEDduIbkAWZR2vfN9UzMjMFkeFl1fISS0btxIcjWEXHTv96BOuX54"
            + "LRHdA5nq58SrAfWDmtdfZb+Y1rtmOrD817LE8UZhprXwnuiZNqtO3STOVHXqmAUH8qhVTw5GCPsO+nci"
            + "ReiVJfllUOp8raPcyNc5bGeK7q4nHwrl+9aJt1aHxBs21P0FoRoq36m8AY3cvYXECSWQb4OdAidgr61o"
            + "1xxJCWpaqFi6+LOVK8GSGeK/ZrbbUg5lW2DNZuXb7FZrf8ofqsm2uPYx+O0NTA1AMPzX11g36dUvc5oK"
            + "88T5bkFsrGRbUp77RfPD8X25kiE2lIKA4Q1W0pM365xMBeSgiPVLHWHJgyHxlrIMssuZc8qgMf5WdpNr"
            + "X+TKXQ0Na9fWLHPqlq48AD5u5M7fvmAoh1ASi8V8OVaIwQPjiioLSUq0GJXUzJukdkNttEt7lITDYV8v"
            + "UgvraiOkOwp9tJsQTS7Kmm9QB4MlJEmFt377DlNVEhTCYLCFyFq8V9NkQvl6qoPMJ3Sih2lYgPFwrRzT"
            + "wZeuJgUuy6qa5Po6V5Mk0Nf4w3tFCEe5X+L79XuJcC8SDZfcwwPffGXJD+QL/l+Mq3CAwYCuaQSM8wfG"
            + "VVUFu4mYpmmxBPhXk6qqz/YqUVQ1Di6J5i/TiOwXUCYCB9bBmm5YC6lydJKZLoxnMhmi9ep6nOtrHNn3"
            + "x+1xuTe2wZdj/jzUnedJSzvjq5f8abJMCdgaL0yDPU3mofNxHkKpLuBvub9sOWnh2QtBkvDlBKmOxjJ/"
            + "+DcpGKo0+ykLCtj3ywv5E15pleej+H5B1nSQa6WkxOtOiMIrnizpa1DJqJ8/LZ2kbihejOcfna0LIdd4"
            + "AaAv6RM/Y3lWTqq+PTDIZV4PkPnBg33kFYiArM3G4WcKPRQgyc+qX4DSuMIX6P75icDm83V0tn7dlIYh"
            + "LH74vv8ofKcA7GWLX4bSA8093R3Pd7bHKvn96GUDymA26CkV/IJ1+AXrWmtS7DeLXRjjVzMt3OWvGUEQ"
            + "BEEQBEEQBEEQBEEQBEEQBEEQBEEQBEEQBEEQBEEQBEEQBEEQBEGQ/xG/A+6zdHMAgAAA",

            // 02-interrupts.gb
            "H4sICHRqrVoAAzAyLWludGVycnVwdHMuZ2IA7ZxNbBtFFICfHXsbUtdJ2h6G1mk3LYQ0atQFBFhiWQUI"
            + "FMRPOCCQEIhFNAulxglJSV3qLkkjIf4OIC70AoIDiAuoF0ray27Wdljaqaig1SIRYQsSCatENmihtJXN"
            + "m7VbKOJYQIj3aWdn3+yb9zez41MCcGlR3Uts8O/GWRU+9oNhHF0By1vGYRpisAJaO9e/+BLEvz6aXpyf"
            + "/2r/4cNPPJ6Onzo6//X+tw/PXKv9ycLkhSfDuPEfjf0SsLUTrHhH38qEfHb1CvmXjANWNww4EP63AyMI"
            + "giAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgvif4cizJ6PKufUKHAt9/im4"
            + "Bz/1tW94i1W9Sf45EOp8paV92Ripun6Wd1h7hfQXf+fvMtDqxcniVHFfcdr18ycWuqfrt/ff2/9A/4c9"
            + "rUeSRy4/sma90vFoZumHrbuXPhl6bmn6wT1L8pXydz31DWObn9k8WjpuV9295bCXLbd4LKK5LKw55ZDn"
            + "+rW23EMWD3kYgiY3+7agL/E563uftfD3rb6D8q9Vly9ZfJXNu2y+0eaHLNfnm+zzc/CxmvdtXrds18+w"
            + "BX83K/nPsXl/D/vKz7IT/l523M9nUG23zeb8/C13sR/9/OA97BR/yPKP3MCvtauL7bW2ZLiwrKA4Ic/P"
            + "s5ifx4Hl8W0zXz5ix9v4I3ZBwWAwE2lL5HXl3NRQBsXdbmnB37ixrxzx9pajmKCE8SzwHTYL16HE77Fc"
            + "jHWBz1r3PTY+PrwNSho4O2bPRDy1N3pT77esXbwbHEkPn38T9UbOSN4T5sHey3AmFgHvqF4LHROv/crk"
            + "Fr8yhW0ftuktIvEL7VtcvsrkYXllZeqQvKKyb0ZurUx/Irc4Qxb3bGfG7sNsl2cOyL+4P11d/6YMngbF"
            + "ZdjasdWx9WiT2OrFK7VdxSu6v5jjb1jcxGLP8SHL11r4equxb7jYKfy92SBitOTzidmqU57zRcTvWNVa"
            + "yJPjIrHbH9ueGt7WBiX3d0neiHUZDJZQixTrQa0i4dvuhNLZEEDejuBoe3R2xj5d66tZ2Ju1WbxX2hcj"
            + "2DXUWwZR/adAXWij5sdNlc+aKqsj929/enhM3jYyPJ6+aqe8a2RsB5Y4GuQbFTn7WkjkclTkUrlIChxd"
            + "/DowXA0sS9E7br77/ottTUBTxXnFzofWnW3JHOiVKoO112TfdvmW3BnwFjtyxxzcr5bTDfviwJfluuHl"
            + "eB17bXV5La5EOeFpp8tdHn82xx7uhjelyK7N8rkrovJpF5flSakDF2a79jw2+OsWlee1UHkIFxKdoL2h"
            + "utt5KCfpeal1zaoPNmxAS3ZUfOx8j6O9XhzAObdq3xW3OvtzwVBncaDqfoSf5uTiJAYjPs7ergW/e63X"
            + "V1smX1atyb1tfDjnVz/uvy69q9QwxN/NYcB4Ryv5hTNdXqEVc9Dk7jWetPqaqHxOW4vZlezzyqhx8sXi"
            + "LVX3hFAe6enZtKlzrde1ZsMe9HG6dPzC+cMEwCCVSgVyqpbCC8c1XZWyDIwUU/TCC5C4PpEcMW6EWCwW"
            + "6MUTOC8Rh9Ek9slRAEOtq0Zg0ESDDRRFx7t54YjTdQhLMTQ4AKqRnjAMFZiYz0yUxQsTzBiLSTgeS6gp"
            + "E32ZelYSsqrrWaFvCjVFQX1DXKLXpFhS+IXAb9ArILwoLNZwjxc+i5kNP5gv+n8qrWvA0IBpGIDGxYXj"
            + "uq6j3UzKMIxUBv3rWV03m70Omq6n0SUYwTQD1KCAKkgCnIdzRnEupiowobBUO1koFMCYMM200DcEauBP"
            + "2BPyROr6QE4F77HuIk/WWJlAveHPUFUGaOtkbQntGaoIXYyLEBp1QX/rgmnrYEBkL4UhE8gZ6EimCn/4"
            + "salZutJ8VCUN7QflxfxBVFoX+WiBX5QNE+WEklVE3QE0UfFsQ9/ASiaD/FljJ41i8VIi/2SzLgCnRAGw"
            + "b+hDkLHalLN6YA8NClnUA2Wx8XAdRQXiAGA04wgyxR4LkBV7NShAY1wTE8xg/8Rx8cU81qzfKGMxDEts"
            + "vh/fiv1eAP6qI85K5Zr+7emdw2Njz47uHG8TJ6hfDGlTxbCvtYpzyRPnUtUZzPFEjv88Jw5vVvuv/ZMe"
            + "giAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgvh/8Ru7YnebAIAAAA==",

            // 03-op sp,hl.gb
            "H4sICGdqrVoAAzAzLW9wIHNwLGhsLmdiAO2cX2xT1xnAPzuJcYNxnD/aLiEk1yRYIYLhjg2y1RxlFMHD"
            + "lmUvo1oF5XaDS1usJIAEBhw3f6aunSZ1aNLU7qFdXzpNWmE8rBSoNDvGNlfmoLIVdJGacL1i1rk0stve"
            + "NTHgu+9cB+jWKg/btD30+8nnnHvO+f6c77vn3HufDPAFJ9nsvPiBql5YAotrDsAEeGAJuBs7nn0OvO9c"
            + "GLwxNXX1xbNn9/xo0HvzwtQ7L7589vQ69i8WRu9dqXL4f7r2/wJbGyHu9fU0tcm3WpbIn0SSEPdDXxKc"
            + "/++FEQRBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBfMFIypNX6oK3O4Jw"
            + "0fHWedBOnTfZNV4TL22U/253LN4UZ29XR0qaGeW++IjoaZ+1pUnALGPUGDPGjQnNTF3O+yesLWu+u2bb"
            + "muMBd7Y3uzTb2hH07YzMfLD18MwbA0dmJh45OiOvlK8HrBX7V+9bPZy7lChpIwWnHi3U6FIt0yQnSxYc"
            + "umZW6s89GucOHZfA5Pm23m5zPBP/mynV8N/Ee07JcyWNz8R5c4IvT/DOBD8T10y+KnFXBy9LKTPBrXhC"
            + "MyNS3jws5cwj0pR5VLpqRqXL5oh0yUxFUOxwQsqYqU3flj40U5v7pZv80biZ3cDXJUo3Gir1vc70onQw"
            + "6dDNlOQxUziw2Lvr9Ns7Et56viORDuJiMBLX2tpjwdtjAxHsHtZyebOzs6dQq48U6jBAF64nz/cmJKcF"
            + "Od4f13CteT4Z/97jBw7s3gU5Bsm9k+VaPdRdt7H7XalBzG0eGtx9d6ZOHyq79D2xU90PoCYmAWsUrzgu"
            + "immzOLrWLI5hGccysVYEfq+8i7evOHpWbiqOnZGXFMdPy+7ixBtyTXIgzvVE8nSiB6NdHDkpf6J99KB1"
            + "rQA6A2MRlgYsFpYAG8ViGSvZIaPL35nlv4jzWIJ7snwgbrIa3hGv7hsudgr/3Tl7xWjJ5AcnS8lvZk2x"
            + "4lfipYpDl70isC2PPxnevasectr9ntyJedls30L2WqFdZ+lCh67hriq3Y+gd+p7GUcsFrcd/v7Wz7Wjl"
            + "x7I5gkJRlIlUADeSVgb9hu98sX/RW1L9vWv9o6JlnjAsDCVoAHMY/T4oGZaW4t9KsrjRgkE1MrfRJK4d"
            + "L92pKQZuOOQ5BtmI7AglNBG79ungxchSxowvodKXUWkpKjjlssYaDZ9txzKa2YTRYtspaykUb/UGe/7i"
            + "ye7rnktoJ4zARiNgrDS6WAmFnzFaPpM+LTXgcqOaD902slk0p2Qbg072a5T/iZB3CNFmIYpSTSb7jris"
            + "k/WElvIGbUflhDACxcAr3Q0maxDyF4VQrXwzgucg7w9Y9vL7MJQKhoJhMOer8p152ZRt26pOFAMAGOH1"
            + "XEk7JbvwFP5Zqqk+MkzmFdK6kMaHRUH7CpwYeGRHwN0hDn52WdD3z+d+Z0Sc/KsHuvZ37es6tFr+U7I/"
            + "PlQcfW0ocLU4drzLGC2OYz1WnMB6PIZPFP7XSb79HH91snpsAHL+W6mewux0T2EOS3k6jw+LW9NsU+H2"
            + "NG6WO9O4G3lzmp+c7F5aVXFArjw7jfu/kpVd5TlxlRupNMtnopW0/Ifky4m8udAKciMLzUYWmjy80OSR"
            + "hSaPLjR56d9X1dYBPATwDYA5gPcc8J4Fsw6YtcALvD3tLWKl+felH+vc1pnHFs9dquSGqR51aO6He1mT"
            + "49i2xjd/lZyddjunHhy+xhtSZecUf/5cucauYWqo7JjaM4cHsbNzpPK8/MecqE/ia8MBDeCDDngaRsGq"
            + "djvAZ2HXshaeffNj/rPMY8/l1+/2f//KUz3W+1/bcNq9c0Xz6z0bkvsf3t6fcrTfqomc7HYVN1d+LpsJ"
            + "jY9l7JN/Ts7gG6Ap44dxjO4H2P7Ua2HLWgrL8NlWaNPZbGG5zjMZabsfXnDV4na83VUnz4qz/oTLh5v/"
            + "SfY0Fvj8UidPMUdhAB+N6ATtDVhao+e8S0m53K3Nv12xAi0l6sTrk/M0O2b0oc7D7LqxNfl+xh5qNPpK"
            + "2gl82Y3eGMXFiNdd9/K86V+m91QWyQ+UKnJ3PT+VMUuvr/n64KFc1RD/OIMLxhqtpPLl5XrajTEw2d+q"
            + "u1q+WiffZsswulzirjBKXHnW2FTSLgvhoUBg1arGZfry1hVH0cds7tK9N7okAAnC4erf2YQrYfzhOFNC"
            + "rqgEalgKKulnoG19W++Q+hB4PB5bztuGem1eGO7FtncYQA1ZIdU2GEODVYJBBevYvY8GRQGny4MG+yCk"
            + "Dh5U1RBIQl+KYV9MxCDmkTwuHPe0hcIx9BVToi7RDylKVMjHhFgwiPKq+ImWuTy9wi/Yfu02CMJLUPJU"
            + "3eMPr4Vm1Q/Gi/6fGlQYSGggpqqAxsUPxxVFQbuRsKqq4Qj6V6KKEptvFWCKMoguQbXVVAjZCQyBS4B6"
            + "qDOMuhiqIAbpmcqVdDoN6sFYbFDIq4KQ7U/YE/2D4fV2P2zPY95FnFL1ztjiVX9qKCQB2rpSmUF7akgs"
            + "XYyLJVTzgv7abbV26BPRu5wQsfsR8PWG05/6fKvEleD8ZcjF0L6dXowfRKYVEQ+z/WJfjWG/LRgNirwD"
            + "MJHxaFVexUz22vFL1Z00jMkLi/h75/MCcFMkANuqPNgRh+b7UcW2hwZFX+QD+2Lj4X0UGfBiX51fhx0p"
            + "tpiAqNirdgKq40woxOz948WbL/Sk+fwNS5IHlyU234cvee4ngE+nxddHcN2aoWH5wPDqJ8L14ovENBxs"
            + "zHCazH3/9Zb8ZYYPZviWrPggkCqf8zFMEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARB"
            + "EATxH/IP0Ul9lgCAAAA=",

            // 04-op r,imm.gb
            "H4sICF5qrVoAAzA0LW9wIHIsaW1tLmdiAO2Wf2wT1x3Av3YSkwbj2CHqjhCSMwlWCFBuK4NIM08ZRaD9"
            + "SLN/RiUE4qrCMVY3CaCBAcfkxwbdNGmrVmmrJtF1f3SbtsH4Y+WXtvlibHMzR6GD6NggPa+YaRaN7LbX"
            + "BgP2vu8coFur/DFV2x/9fnTvvXvvfX+87/fevTuATznxuc4LbyvK+Tkwu2o3jIIb5kCtr/X574Ln2vm+"
            + "m9evX33pzJntz/R5bp2/fu2ll8+cepz9h4WhB3eK+MT/dO2fAOt9EPN4OxuaxTuNc8QPwnGI+aE7Ds7/"
            + "98IIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiA+ZcTFsfEa6W6rBBcc"
            + "F8+BduKcxd7Uq2KF1eL7dqesN8TY5cpIQbMiujc2yHvaR21pArCyOWQOmyPmqGYlrmT9o+V1y55ctmHZ"
            + "0UBtuis9L93UKnm3hCffXr9v8mTv/snRpw5MiovEG4Hywl1Ldy4dyFxSC9pgzmlEclWGUM00wcniOYeh"
            + "WaW6sxtjusPAJTBxuq2z24yeiv3TEqr0X8Q6T4i3C5o+GdPnqvoCVW9T9dMxzdIXq/d18LaQsFS9HFM1"
            + "KyxkrX1CxtovXLcOCFetiHDFGhQuWYkwiu1ThZSVWPMV4R0rsbZHuKVvjFnpVfrjauFmfamuy5mclZTi"
            + "DsNKCG4rgQOzPVtPXd6seur0zWpSwsVgJK7l1S9Id4d7w9jdp2WyVltbZ67aGMzVYIAuXE9Wf1YVnGXI"
            + "6D0xDdea1cdiX3t69+5tWyHDIP7sWLHaCHbUrO54S6jnc2v7+7bdn6kx+osuY3v0RMcjqIlJwBrFS44L"
            + "fNrKDy238sNYRrCMLueBPyhv4ePLD50RG/LDp8U5+ZFTYm1+9KRYFe+N6YYaP6V2YrSzw8fFD7R3P1t+"
            + "MwcGA3MWlnosZSwBNoSlbC5ie812f2Na/1FMj6r6vT/rvTGLVemtscq+0flO0X9z1l4xWrL0PWOF+JK0"
            + "xVf8SqxQchiihwe27ukdoW1b6yCjPeyJbZiXtfYjZNFci8GSuVZDw11VbMHQW43tvqGyC5qO/m59W/OB"
            + "0rdFaxCFIigTLgFuJK0Ixk3vuXzPrItC3YN749182TpmljEUyQTmMHu8UDDLWkL/YpzFzEYMysdqzQZ+"
            + "7zhyryofuOkQbzNIh0VHUNV47NqHg+cj8xgzH0Wlz6DSPFRwikWN+UyvbadszmWjZqNtp6glULzJI3X+"
            + "3Z3e2XFb1Y6ZgdVmwFxktrMCCh8yGz+SPi3R66pFNS+69bEpNCenfZKT/QzlD3N5Bxedy0VRqsFiX+W3"
            + "NaKhagmPZDsqqtwI5AOvdNRbrJ7LX+BC1eKtML4HWX+gbC+/G0MpYSgYBnO+Kt6blk3YtsuViXwAACO8"
            + "kSloJ0QXvoV/EaoqR4bFPFza4NJ4WOS0x+BY71ObA7Wt/MVPz5e8//7ebwnzN//q7vZd7Tvb9y4V34j3"
            + "xPrzQ7/tD1zNDx9tN4fyI1gP50exHoniiaL/Y0zfdFZ/dazy2gBk/HcSnbmpic7cbSzFiSweFncm2Kbc"
            + "3QncLPcmcDfqS5L68bGOeRUVB2SKUxO4/0tp0VW8ze8yg6Ul4ulIKSn+Pv6ymrVmWkFmcKbZ8EyT+2aa"
            + "3D/T5IGZJi/996raSgAXgAfgUYAWAHyujwEwgPcBSgBJgAsA4wAT+LwBJrmk3p30eHll8iqPleb/VnJd"
            + "FqtENhHFp+BY0eALb/C/N1GIY57rE8X3JvQfnM2obYOlqPi3jN28roGj3tt6cChfPniw5sR3Rl58zagb"
            + "t9Z+f1XnnXU1xT8sP6Ju3ND514uTjadza1a+cbjj11LPz59Jrnjy67U/bf6x50tTuy8fSl1LVv3pCX/C"
            + "0XKnKny8w5VfW/qhaKmavitlv+pnfSk88qtSfhjBZX8Z2+95ytiyxtx8PMxyzQabyi0w9JMpYZMffuKq"
            + "xv13t71GnOIv9zdcXtztO9hBLPDxpUa8zhy5XjwL0Qna6y1rvnspl5xw1TbN/dXChWhJreHfS/2PSfaC"
            + "2Y06T7Ab5vr4tZQ95DO7C9ox/LoN3RzCxfDvW8eCrOWfb3SWZomPFEpiR53+y5RVeG3Z5/v2ZiqG9GwK"
            + "F4w1WklkiwuMZC3GwER/k+Fq/FyNeJfNx+gy6n1hlBh/3lxT0K5w4f5AYPFi33xjQdPCA+hjKnPpwSdc"
            + "4IAAoVDI7odKIbxwnMlBV0QAJSRIcvIQNK9s7upXvgBut9uW8zSjXrMHBrqw7RoAUILloGIbjKLBCpIk"
            + "Yx198Jcgy+B0udFgNwSVvj2KEgSB6wtR7POJKETdgtuF4+7mYCiKvqJyxMX7QVmOcPkoF5MklFf4xVvm"
            + "cndxv2D7tVsJuBdJcFfc44X3XLPiB+NF/9/skxkIaCCqKIDG+YXjsiyj3XBIUZRQGP3LEVmOTrcyMFnu"
            + "Q5eg2GoKBO0EBsHFQT3UGUBdDJUTheRkaTyZTIKyJxrt4/IKJ2j74/Z4f09opd0P2fOYdx6nUHkytnjF"
            + "nxIMCoC2xkuTaE8J8qXzcb6ESl7QX4ut1gLdPHqXE8J2PwzerlDyQ/9rpZgsTd8GXQzt2+nF+IFnWubx"
            + "MNsv9pUo9puliMTzjscEz3ikIq9gJrvs+IXKThrA5IV4/F3TeQG4xROAbUUe7IiD0/2IbNtDg7zP84F9"
            + "vvHwOfIM4AkFyvQ67EixxQRE+F61E1AZZ1whau8fDz58ridM529AENy4LL753jnifpgA/fUk/92QVizr"
            + "HxB3Ld3x3HN1/BfEMh1s2HRarPbh9yx+OKVvSemr0vwPQCh9zN8vQRAEQRAEQRAEQRAEQRAEQRAEQRAE"
            + "QRAEQRAEQRAEQRAEQRAEQRDEJ8u/ANEt8ZcAgAAA",

            // 05-op rp.gb
            "H4sICFJqrVoAAzA1LW9wIHJwLmdiAO2Wb2wUxxXA353tw5jjfMYuXWyD15y5GgNlUwS11GPkJggUpcT9"
            + "0kSKQrJRYUnKyTagwoHPF/+paFr1Q6P0Q/kSiiolqtRCkdryT612fdwd22NRSANa1ODsNRxVT8S6S7SN"
            + "Ocxt3+wZSJvIH6qo/ZD3087Mvpn33sx7O7O7AF9wks3eyx8oyqXFsKhmP0yAHxZDfVPHKz+CwLuXBm7d"
            + "uHH96Pnzu787ELh96ca7R4+dP7uB/YeH0Qd3iv87/9O1fw5sawI1EOxZ0i7ebVksfhxLgtoJfUnw/r8X"
            + "RhAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAE8QUjKU5eq5NmOyS47Hnr"
            + "IuinL9rsPaNGLW0W/+kKjrFEZe9Ue0q6HTeC6giX9E/70gVgjjVqjVnj1oRup67mOyecreueXPfUuhPh"
            + "+mxvdlm2tUMKPh+b/mDboekz/YenJ54enhZXiTfDzsp9a/euHcpd0Ur6SMFrxgs1plDLdMHLkgWPqduV"
            + "hgvPqIbHxCUwca5tcNuckVH/YQs1xptqz2nxTkk3plWjWTOWa0ZIM86pum2s1u7b4G0pZWuGo2q6HRPy"
            + "9iEhZx8WbtjDwnU7Lly1R4QrdiqGaoc0IWOnHn1C+NBObdku3DaeUe3s140NWulWY6Wh15tekJaSHtNO"
            + "CX47hR2LAjvPvrNDCzQYO7S0hIvBSHzra1+VZsf6Yyge0nN5OxTqKdSaI4U6DNCH68kbezTB60DO2K7q"
            + "uNa8Mal++4X9+3fthByD5J7Jcq0Z6a7b3P2+0MjHtgwO7Lo/UmcOln3m7sTp7oVoiUnAGtUrnst82C6O"
            + "rreLY1jGsUys54E/KO/j4yuOnheXFMfOiYuL42fF+uLEGbEm2a8appY8q/VgtItip8SP9Y8ecd4rgMnA"
            + "WoClEYuDJcxGsTjWKnbQ6uoMZo3XVCOhGbN/NvpVm9UYHWp13xh8pxi/vuCuGD3ZxoHJUvIrWZuv+Lha"
            + "qnhMMcAD2/rCS9FdOxsgpz+UxBDmZYv7CNnRwgqTpQsdpo67qrwCQ+8wdzeNOj5oPfHbbaH24coPRHsE"
            + "leKoE6sAbiS9DOat4MXi9gVvCQ0P7s2Pio590nIwFMkC5rG2B6FkOXrK+GaSqVYLBtXE6q0l/N7z+r2a"
            + "YviWR7zDIBsTPRFN57Hrnwye9yxjzFqKRl9Go2Vo4BXLOmuygq4fx2pmE1aL66esp1C9NSD1/M2f3dt9"
            + "R9NPWuHNVthaZXWxEiofsVo+lT491e+rR7MgTtvEZtCdnG2SvOwXqP9Dru/hqs1cFbWW2Oxb/LZONDU9"
            + "FZDcicoadwLF8PHuRps1cv3LXKlWvB3Dc5DvDDvu8vswlAqGgmEw7xvivTndlOvbqQ4UwwAY4c1cST8t"
            + "+vAU/kWoqb4ybBbg2ibXxpdFQf8qnOx/eke4voMf/GybFPz3c/98jJ/86/u79nXt7Tq4Vnw7uV0dLI7+"
            + "ZjB8vTh2ossaLY5jPVacwHo8gW8U4++TxrMXjDcmq8cGINd5N9VTmJnqKdzBUp7K48vi7hR7vDA7hZvl"
            + "3hTuRmNp2jg12b2sauKBXHlmCvd/JSv6ynf4XW6kslQ8F6+kxd8nj2l5e74V5EbmG43NN3hovsHD8w0O"
            + "zzd45b831RcBtAGsAagBaAYIASwEWAawGiAAxpp0IMgri1dFrPTOHennQk+F8tjiIbyaejK0NcQ7Solc"
            + "EpPamMJDFwqNVIZFK8frt/ET4YFGCEIHvAyj4FTFDgg6KDrO/KPq8U2//NOXzqw59lp68oi6YKzx+xt7"
            + "92Qa2nwvPvHozt/9RLgbfeRnKc+KuzWxU92+4pbKT0Vb0429GfekXwhm8I3vzXTCOIbyOLY/DjjYspZC"
            + "G77LCu0mmyksN43TGeHZTvi5rxa332xXnTjDz/aLviBu9pfYy1jgs0udeIN5Cv34KsRJ0F+/ozfNZnxy"
            + "ylff2vyrlSvRk1bHP5fGH9PsVasPbR5jN61tyb9m3K4mq6+kn8SP2+itUVwM/7x1L8/bnW1mT2WBuLBU"
            + "EbsbjDczdukP6zYOHMxVHRk3M7hgrNFLKl9ebqbrMQYmdraavpav1YmzrA2jy2n3lVHj2ivWoyX9Klce"
            + "DIdXr25qM5e3rhzGOWZyVx58wQUOCBCNRl05Wonihf1MjvjiAihRQZLTR6B9U3vvoPIN8Pv9rl6gHe3a"
            + "AzDUi23vEIAScSKK6zCBDqtIkox14sFPgiyD1+dHh30QUQYOKEoEBG4vJFDmAwlI+AW/D/v97ZFoAudK"
            + "yHEflyOyHOf6Ca4mSaiv8Iu3zOfv5fOCO6/bSsBnkQR/dXq88J5bVufBeHH+7w3IDAR0kFAUQOf8wn5Z"
            + "ltFvLKooSjSG88txWU7MtTIwWR7AKUFxzRSIuAmMgI+DdmgzhLYYKicB6enKtXQ6DcqBRGKA6yuciDsf"
            + "98flA9FNrhx1xzHvPE6h+mRc9ep8SiQiAPq6VplGf0qEL5338yVU84LzrXDNVkAfj97nhZgrxyDYG01/"
            + "4netosrS3G3Ex9C/m16MH3imZR4Pc+dFWUmg3C7FJZ53AMYzHq/qK5jJXjd+obqThjB5UR5/71xeAG7z"
            + "BGBb1Qc34sicHJddf+iQyzwfKPONh8+RZyCAsjK3DjdSbDEBcb5X3QRU+xk3SLj7J4APn9sJc/kbEgQ/"
            + "Lotvvg9f9z9MgHE5zf82pI3rBofEfUMN/PfDtjxszPLarP7htyx5JGM8lzE2ZPnXX6h8xp8vQRAEQRAE"
            + "QRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRDE58+/AAvbo4UAgAAA",

            // 06-ld r,r.gb
            "H4sICEFqrVoAAzA2LWxkIHIsci5nYgDtl2twE9cVgI/8EI4RsmWYdvEDr8FobBeKSijxFHHrEB5tiHGa"
            + "BmgoFBFgCaDa5i2wrNg4DSmZFmj+NH3wmOmkZdri0kd4ZaaShYS39jKhBbo0wawA01YDrhRYYssg9dyV"
            + "gbTJMJ1Mp/2R883ePXvvPY97zt5drQA+4QRHZpy+IUndI2B45gZoAwuMgBxb6SvfBuu73fXXLl688PqJ"
            + "E6uW11uvd1989/V9J449yv7NQ8v9K8lb8T9d+3+BOTbwW/OrCorFwVEjxPc9QfCXQU0QMv7fCyMIgiAI"
            + "giAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgviEERQ7zmc77pQ64LTp7U6Qj3Tq"
            + "7JKS6Y9PF28bnZRS4Gdn0yNxWfcq+f5m3pM/7EsWgKW0Fq1V2661yXroXG9ZW2r2xHkTF0w8ZM/pqu4a"
            + "3VVY6shf6um7MWdr39G6bX1tC5v6xPHiVXtq7PoJ6yY0Rs4E4nJzNEP1RjNVIYvJQgYLRk2qrCdzTy7y"
            + "KyYVl8DEIZlryIhyyv93XchUfuqvOiIOxGWlz6+MDCglAWVcQDnul3WlMnDPBi/jIT2gpPwBWfcIvfpW"
            + "IaJvEy7qTcIF3Suc05uFM3rIg2pbA8IpPTRjrvCeHppZK1xXFvn1rseURwPxa3nJ3OqM8LCwI2hS9ZBg"
            + "0UM4MNy64tjZJQFrrrIkEHbgYjAT86SsPY47rXUe7G6VI736uHFV0Sy1OZqNCZpxPb3K2oCQkYKIUuuX"
            + "ca29Sof/6WUbNqxcAREGwbUdiSzVWZE9veKKkMfnZjbUr7w3k602JMzqKt+RikfQEouAZ1RPmk7zaT3W"
            + "MkmPtWLbjq1tEk/8fruCty/WckIsiLUeF0fEth8Tc2JtR8XMYJ1fUQPBY4EqzHa457D4vnzzc6lLUVAZ"
            + "aMOw5WFLYbOzFmwpbTzbopWXsW7lNb/iCyiV3UqdX2eZSqk/vW8UvlOUX5w0VoyedGVzRzw4v1vnKz7g"
            + "jydNqmjlic1ettq9ckUuROQHPXEc1mWmcQtZX3SMysLRUlXGXZUYg6mXqqtsLSkzFB761ZxxxU3Jl0S9"
            + "GZW8qONJAm4kOQHqtfzOWO2wt4Xc+9fqzVhKb9dSmIpDA2bSavMhrqXkkPJ4kPm1UZiUjeVoBfzatPdu"
            + "Zsx+zSQOMOjyiCZnQOa5yx9Mno+MZkz7FBp9Go1Go0GGmJCZTcs3/KS0kaxNG2X4ScghVC+0OqouW7rW"
            + "VQwE5HbNPl2za+O1chZH5Ze1UR8qnxyqM+egWT6GtbF+dOfqsjky2H7U38H1TVx1JFdFrQKdPcUvs0U1"
            + "IIesDiNQIsCdQMx+oCJPZ3lc/zRXyhKve/A56C2zp4zl12AqSUwF02AZb4h3h3RDhu9UeiJmB8AMr0bi"
            + "8hHRjE/hn4TM9CtDZ1aurXJtfFlE5c9Ce93CJfacUv7gdxU58v/1uV/q4U/+hQ3l68vXlW+ZIP4xWOtv"
            + "iLX8ssF+IdZ6qFxriW3Hc2usDc/bffhGUf7aoSw+qbzRkX5sACJlg6GqaH9PVXQAW6KnF18Wgz3savRO"
            + "D26Wuz24G5WjYeVwR8XotIkJIon+Htz/yS7RnBjgV5Hm5FHxuDcZFn8X3Bfo1R+2gkjzw2Y9D5vc+rDJ"
            + "bQ+bbHrY5JmPbyrXADwOMAPgCYCZALMAZgPMAfgSwJcBngSYC/AUQC3APIA6gKcBvgLwDMBXAZ4FmA+w"
            + "AGAhwNcAngNYBPB1gMUASwC+AbAUwAWwDOB5gOUAKwBWAkgAqwBeAFgNsAZgLYAb4JsA9QANAI0A6wDW"
            + "A2wA2AiwCWALgAdgK8A2gCYAL0AzgA/gRQArKErYms9PGj/F8CSb3wqxnuitHlZgWjDF9gNP2a2eeBDv"
            + "d14ocatH2XVSrvlCu/nNrl//vOEf63646Yr72JRB+5FFxyad5rIjd1ph1c7zXq7TnruxcfTB+gbbX5JJ"
            + "IT/rM1w2rdnx0qs7phTArB4T13nt8q7wxebdk1O7i37G5YLfbysb50+Yp5Xsb/nizKIfcZ0F47+l/Pnw"
            + "OwNczuu/NDcWHBDEvNt71q0+PTu2P7OW61TOuTyVy9239++a/rz94E+eCWyadWNTzc7a5Qvaj09MGutB"
            + "OXXnH8ZO+U7HysJ3Us9O9v9t/o0nd0zicz9+q/Vavft7jx2+HqwsCe078FyDpPx28O7G6yNvfrfRsvQG"
            + "l1wvZBozmOk5XGGOzUzuFvWArOztNF6LJ6d34s/jpM4y2I5V3YxypzWFko2KFuGLP1qssv5oiapc7RQW"
            + "l8H3zVn4rN4pzxb7+YvwBXM+vhlWsxexwUe3bPEiM0Xr8HcDg6C/upRsq5TNrpA5p3DkwbFj0VMgm39b"
            + "KNFTbI9WgzZPsKvanGBOesim1cTldvwSaLnWgovh3wIVJb16WZFalRwmPhJPihW5ytlOPf7mxM/Xb4mk"
            + "HSk2GReMZ/QS6k2UqOEczIGJZYWqedTkbPEOK8LsIoF7yqhx/hVtRlw+x5Ub7PbKSluRWlI4tglj9EfO"
            + "3P/cETgggNvtNvrupBsPHGcup9krgOQWHK7wy1A8tbi6QZoGFovF0LMWo12xFRqrUVbjXpecKadkOPSh"
            + "wzQOBz40fIcP4XJBhtmCDmvAKdVvliQnCNxe8GGfT/jAZxEsZhy3FDvdPozlc3nNvO90ubxc38fVHA7U"
            + "l/jBJTNbqnlcMOIa0gE8ikOwpMPjgdfcMh0H88X4a+pdDAR04JMkQOf8wHGXy4V+PW5JktwejO/yuly+"
            + "IekC5nLVY0iQDDMJnEYBnWDmoB3aNKItpsrxQbgveT4cDoO02eer5/oSx2nE4/54f7N7qtF3G/NYd56n"
            + "kL4zhno6nuR0CoC+zif70J/k5Evn43wJ6bpgvDGG2Rio4dmbM8Bj9D2QX+0Of+DbNul3OYYunWaG/o3y"
            + "Yv7AK+3i+TAjLvYlH/aLHV4HrzsA4xX3pvUlrGS1kb+Q3kmNWDw3z796qC4A13kBUKb1wcjYOdT3ugx/"
            + "6JD3eT2wzzce3kdeASv2paF1GJmixAJ4+V41CpAeZ9zAZ+wfK958bicM1a9RECy4LL753ttreVAAZfAU"
            + "/zRzTJ3oXiGun7A+l3+s6ZqJtWoZOst58Msf/E2n8mqnsqybfysJyY/4n0AQBEEQBEEQBEEQBEEQBEEQ"
            + "BEEQBEEQBEEQBEEQBEEQBEEQBEEQxH/GPwFSTQqXAIAAAA==",

            // 07-jr,jp,call,ret,rst.gb
            "H4sICDZqrVoAAzA3LWpyLGpwLGNhbGwscmV0LHJzdC5nYgDtnG1sFMcZgOfO9uGY4/ypdjEGr8FcjWWX"
            + "JbTBUpeRk6KgqnGd5geRoiAWBZaEnGxjEJzx3fVsV2laVSVRq6Tpj9CoKf1QC6VSS8D9cevz7Xl7LMUp"
            + "0AVhey/BoJyIewfaYh9w13f2DKRt5B/9rvo+upnZmXk/5n13dnb/2KJGbMR/UftfT7TaeeZDWT69hCwu"
            + "2kuGiJssIaWV9S9/nXgun+66OjFx8Y3h4V3PdXmun564/Mbh4ZPr6V9ZCN+/kru+/G9d+z+BzZUk4qlo"
            + "rqrjb9cs4W/5oyTSQNqjxPmfXhiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiC"
            + "IAiCIMj/GVF+5EKJcKdeIGccZ8eIdmLMolN6USSzkf+T3cnrVRF6rjCS0ayAXhEJst7H/H2/xhGaN8Pm"
            + "gDloDmlW7Px0w1D+8dYvtW5pPeotTbQlliZq64WKbf6ZDzf3zbzTeXBm6On+GX41f8WbX9nbsqelJzmu"
            + "ZLRgymkEUkUGV0w1zkmjKYehWbmy0WciusOAJVB+vi2z26Qej3xgcUX6jyLNJ/i5jKbPRPRqRV+u6KsU"
            + "/VREs/Q1yj0duMzELEXPRxTN8nPTVh+XtA5yE1Y/d9EKcOetIDduxfwg1qdwcSv22Be5G1ZsUwd3XX8m"
            + "YiU26OuVzNXyXFmbU12kClGHYcU4txWDgcWeHSfPbVU8ZfpWRRVgMRCJa23xq8KdgU4/dPu05LS1alVz"
            + "qtgIpkogQBesZ1p/UeGceZLUOyIarHVaH4k8uX3v3p07SJKS6Isj2WJDbCrZ2PQ+V87mNnV37bw3U2J0"
            + "Z13GrtCJpodAE5IANYjnHGfYtJUOr7XSA1AGoQytZYHfL+/D7UuHh/mq9MApfkl68CRfmh56hy+KdkZ0"
            + "Q4meVJoh2sX+4/wt7ea6/FSKGJSYi6CUQ8lD8dIwlLy5mh4wGxsOJfRvR/SQoncl9M6IRYv0+khh3+hs"
            + "p+g/G7VXDJYsff9IJnokYbEVvxXJ5BwG72GBPb79Bd/OHWUkqT3o8asgL5vsW0iHUysMqqbqDQ12VXYF"
            + "hF5v7KoM512k9ugvNq+q6899lbeCIBQAGX+OwEbSssS4WjGW7lh0liu7f23cTOetY2YeQhFMQh1mRwXJ"
            + "mHktpj8apRGzBoKqpKVmFbt2vHm3KO296uDnKEn4eYeoaCx27aPBs5GllJqfAKVPgtJSUHDyWY1WmhW2"
            + "nbxZTYfMGttOVouBeK1HaH7PndjTNKdox0zvRtNrrjYbaQaEXzJr/iZ9WqzTVQpqFeC2ks6COSlRKTjp"
            + "90H+a0zewUSrmShIVVn0CXZZwhuKFvMItqOswoyQtPetpnKLljP5M0yomL/uh+dgusGbt5ffDqHkIBQI"
            + "gzqP8HfnZWO27XxhIu0lBCK8ksxoJ3gXPIW/54oKR4ZFPUzaYNJwWKS0T5NjnU9v9ZbWswc/sUyo+Mvn"
            + "fpufPfkX9zb2Nu5pPNDCvxvtiHSnwz/v9l5MDxxtNMPpQagH0kNQD4bgRNGvjejPjupHRgqPDSHJhtux"
            + "5tTsZHNqDkp2choOi9uTNJC6Mwmb5e4k7Eb9KVU/PtK0tKDiIMns7CTs/1yCd2Xn2FUymHuKPxXIqfyv"
            + "ooeVaWuhFSSDC836F5rsW2jy4EKT/QtNjv/9qhrnEGFPNzlEwSG2OcSRoBoNqr8Nqu8G1UtBdTSong6q"
            + "elA9F1QvB9WISMZEoonkrEgMkVwUSZyQ3xHyB0KmCLlGyB8JuUVInhB2RDhIFa1KhaboZ1LBKbolFZii"
            + "/lT/VCxTSibWhacqb0+ej85OijdLnRPreqb0Q6NZ5wSri+w6ZI8E7Tpg1/12TSa6s46JXXN9akUnf1i7"
            + "/vZ3+kbJay2jO15ZfyBedu7AD9yJvmuho5cuF93qPPwB31t1ybFpd8eh8T7hiZ9uuLH79dSWbQ83xce/"
            + "IJfcCpef+taj7739vdbWb/b+sHr/j3fknv8Nu37ytcZPLVRijhW3i/zHm1zpTblXeEvR9Pfj9tkyOhiH"
            + "d8y+eAMZ9BB9GNpvePLQ0prUMjg9U3UGnU0tN/Q1Y9yzDeS7rmLY8HcaS/hZdpo876qAx+sF+hUo5ONL"
            + "CT9BHalOOHzBCdjrzGuVXWMuKeYqra3+ycqVYEkpYS9ofW2cvmq2g87n6RVzc7RjzB6qNNsz2jF4nYav"
            + "hmEx7IXatHzaalhmNOcW8Q9lcnxTmb50zMr8uvWzXQeSBUP6ljFYMNRgJTadXW6opRAD5RtqDVfNwyX8"
            + "HboMoksq94RB4sLL5mMZ7TwT7vZ616ypXGYsr13ZDz5mk+P3vxk4BuGIz+ez+76cD34wTiXRFeCI7OME"
            + "SX2J1D1S19Ytf4643W5bzlMHenUe0tMGbVsPIbKYF2XbYAgMFhAECerQ/c8SSSJOlxsMthNR7tovyyLh"
            + "mD4Xgj6bCJGQm3O7YNxdJ/pC4CskBVysL0pSgMmHmJgggLzMfqylLncb80tsv3YrEOZF4NwF9/CDa6ZZ"
            + "8APxgv/dXRIlHBgIyTIB4+wH45IkgV2/T5Zlnx/8SwFJCs23EqGS1AUuiWyryUS0EygSFwP0QKcHdCFU"
            + "RoioM7kLqqoSeX8o1MXkZYZo+2P2WH+/7xG777PnIe8sTq5wZ2zxgj9ZFDkCti7kZsCeLLKls3G2hEJe"
            + "wN8KW20FaWfRu5zEb/f9pKLNp37kAzEXkYT5S9FFwb6dXoifsExLLB5q+4W+HIJ+nRAQWN7hVGEZDxTk"
            + "Zchkmx0/V9hJPZA8H4u/bT4vhFxnCYC2IE/siMX5fkCy7YFB1mf5gD7beHAfWQY80Jfn12FHCi0kIMD2"
            + "qp2AwjhlCiF7/3jg5jM9bj5/PRznhmWxzXfjTfeDBOjtcfZ9I2xo3d3bsrun5bntPl9L7859Lb1795Wx"
            + "rx/LdNAB02nR0gev0uhsXD8b13+ZYB8fXO5/5R9rIQiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiC"
            + "IAiCIAiCIAjyj/Jny5BlCwCAAAA=",

            // 08-misc instrs.gb
            "H4sICClqrVoAAzA4LW1pc2MgaW5zdHJzLmdiAO2WX2wUxxnAZ8/2cTHH2We7zWIMXmNzMhYuRwOpqx4j"
            + "J0VBVeu6qlSQImgWBZZCLrbBFZzhfDnbVZLmJThRldJWSaNIiao2UB5a/vVh1+c7e3UsgjagRcL2XsBU"
            + "XRHrDrSNfcBtv9kzkCaRH6qqfeD7aWdmZ+b7M9+3s7NLyCNOotp1/hNJOreELC7pJUPES5YQj7/+1Z8T"
            + "39VzXTcmJq4cPXt29/NdvpvnJq4efefs6Sfo5yzEH9xJYu3/dO3/BTb7ieyrbKmqE+7ULBE+jSSI3EDa"
            + "E8T1/14YgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiDII0ZCGLlcFrxb"
            + "HyTnuQvjRD05btEprUTObRT+6XRsrUqmHxVHcqoV1SrlftZTv2hL5Qm1jbgxYAwaQ6qVvDTdMGQ/0/r9"
            + "1i2txwKedFt6abq2Plj5XGTmk819M6c6D80MbT08I6wSrgfslfvX7FvTk7mo5NR+06VHzRKdL6Uq76IJ"
            + "k9NVq1A++qyscTosgQrzbbnTZrQx+R8WX6J9ILecFOZyqjYja9WKtlzRGhXtjKxa2mrlvg7c5pKWotmy"
            + "oloRftrq4zPWIX7COsxfsaL8Jaufv2glIyDWp/BjVvLp7/K3rOSmDv6m9qxspb+hPaHkblQUyttcqUWp"
            + "YILTrSTvtZIwsNi38/RH2xVfubZdSQVhMRCJe23pcPDuQGcEun1qZtpqbGwxS/V+swwCdMN6prUXFN5l"
            + "k4zWIauw1mltRP7Bjt7eXTtJhpLECyP5Uj3UXLax+RpfweY2dXftuj9Tpnfn3fru2Mnmx0ATkgA1iBe4"
            + "82zaysbXWtkBKINQhtaywB+Ua/D4svGzQlV24IywJDt4WvBkh04JJYlOWdOVxGmlBaJdHDkhfKreXmdP"
            + "mUSnxFgEpQKKDSVA41BsYxU9aDQ19KS1N2Utpmhb0lqnbNESrV4u7huN7RTtD6POisGSpR0YySVeSVts"
            + "xe/KuQKnCz4W2DM79oR37SwnGfVhT2iEvGxyHiFNmyt0mjLrdRV2VX4FhF6v7/bHbTepPfbHzY11hws/"
            + "E6x+EIqCTKRAYCOpeaLfqBzPdiy6wJc/uNdvZ23ruGFDKEGDUM7oqCQ5w1aT2lMJKhs1EJSfeowqds+9"
            + "fa8kG7jBCXOUpCMCF1JUFrv62eDZyFJKja+C0uOgtBQUXEJepX6j0rFjG9V0yKhx7OTVJIjX+oItH3vT"
            + "+5rnFPW4EdhoBIxVRhPNgfDLRs0X0qcmO90eUKsEt346C+bEtD/oor8F+VeYPMdEq5koSFVZ9HvstkzQ"
            + "FTXpCzqO8gozQrKBd5srLFrB5M8zoVLhZgTeg+mGgO0svx1CKUAoEAZ1vS/cm5dNOrbt4kQ2QAhEeD2T"
            + "U08KbngL/8aXFI8Mi/qYtM6k4bAw1a+R451btwc89ezFTy8LVv77e/9chL35V3qb9jftazq4RvhrokPu"
            + "zsY/7A5cyQ4cazLi2UGoB7JDUA/G4ETR/j6ibRvV3h8pvjaEZBruJFvM2ckWcw5KfnIaDos7k3TQvDsJ"
            + "m+XeJOxGbX1KOzHSvLSowpFMfnYS9n8hLbjzc+wu019YL5yJFlLCnxLvKNPWQivI9C80G1losm+hyUML"
            + "TR5eaPLif66qZoeJMUxuEfIxIflh2xy2PcM218j5G7mGRm5dI2cRkiTkEiHThOQIUQi5CDknxEe076R8"
            + "lawyWJWFSqUF84hNr4IVesZ8w6bVZnyKbjVfmqK/NmNTdMrsn3JXJbnhLf6//KohO5nzkIl1sanE7KTH"
            + "NbGuZ0qrSOZdE9rro/kSpz5is3rYqd9w6vgUq19y6phT9zs1mejOcxO759QO+ytvvfjBhl2fbz/M//ip"
            + "C5E+5bh0dP2SzFtHuy90b0/Uv/faSO43t5XE1Yjceo17fbRi0y9W7v3mjuTvf3Rnas0Pk9yKOyWRE83u"
            + "7KbCEcFSVC095pwho3vH4FuybayBDEIa3oP2NZ8NLa0xl8EpadbpdNZcrmv+cX5bA/mluxQ29t2mMmGW"
            + "nRo/cVfCa7SHvgSFfHkpEyYoZ3bCIQtOwF6nrfq3jLvFpNtTW/27lSvBklLGPsTa42N02GgHnW/T68bm"
            + "xIZxZ8hvtOfU4/DZjN+Iw2LYh7N5+bTVsExvKSwSHssVhGb4bI5buT+3bug6mCka0ug4LBhqsJKczi/X"
            + "Ux6IgQoNtbq75utlwl26DKLLKPeFQeLyq8bTOfUSE+4OBFav9i/Tl9euPAw+ZjMXH/wb8AzCk3A47PTD"
            + "hTBcME7FkDvKEynMB8XUy6Tuybq2bulbxOv1OnK+OtCr85GeNmjbegiRQnZIcgzGwGCRYFCEOvbg90MU"
            + "icvtBYPtJCR1HZCkEOGZPh+DPpuIkZiX97ph3FsXCsfAV0yMulk/JIpRJh9jYsEgyEvsYi11e9uYX+L4"
            + "ddogYV6CvLfoHi64Z5pFPxAv+N/bJVLCg4GYJBEwzi4YF0UR7EbCkiSFI+BfjIpibL4VCRXFLnBJJEdN"
            + "IiEngSHiZoAe6PSALoTKiJHUTOFyKpUi0oFYrIvJS4yQ44/ZY/0D4SedftiZh7yzOPnik3HEi/6kUIgn"
            + "YOtyYQbsSSG2dDbOllDMC/hb4aitIO0sereLRJx+hFS2hVOf+REsyGJw/jbkpmDfSS/ET1imRRYPdfxC"
            + "X4pBvy4YDbK8E0JZxqNFeQky2ebEzxd3Ug8kL8zib5vPCyE3WQKgLcoTJ+LQfD8qOvbAIOuzfECfbTx4"
            + "jiwDPuhL8+twIoUWEhBle9VJQHGcMoWYs3988PCZHj+fvx6e98Ky2Oa79bb3YQK05jH2HxNsa31xT+/z"
            + "wp6u3p/u7y1nvzeWwdEBw2VRz8NvZcIY006NaW+m2d8FX/iSP2sEQRAEQRAEQRAEQRAEQRAEQRAEQRAE"
            + "QRAEQRAEQRAEQRAEQRAEQR5F/gUGIOhCAIAAAA==",

            // 09-op r,r.gb
            "H4sICB9qrVoAAzA5LW9wIHIsci5nYgDtnHt0FNUdx7+bxxLCZvMAy+QBmZAQwkJkiYpRluEhgq1iPLU8"
            + "ig/Woy5SY4JEdJHNmocKSvEBekCpPAqKoIKgiJCqu1k2yeQyOaYFurSCs2hi3QO4Kx1NAu723llEWz30"
            + "nJ6e9g/v58yd+/o97u83d2Zv/gnwE8c3MKHjlMNxMA0DEmvQCBPSkJKZv/wJmD86WNV97NjRF5qa5t9Z"
            + "ZT558NhHL2xo2neZ9C8W6i60HDf//H+69v8C0zPhMWdYsvLEs4PSxK+dPngKMMmHhP/3wjgcDofD4XA4"
            + "HA6Hw+FwOBwOh8PhcDgcDofD4XA4HA6Hw+FwOBwOh8PhcDgcDofD4XA4HA7nJ4ZPbD6SbD2Xb0WH4cM2"
            + "yHvbNOljJdETmSB+pXdiSpZHOhQficiaS8nw1LKe/ENbsgApptap9WqD2ihr/sNdBY2xaaU3ls4q3VGc"
            + "QspJNsnJt2bMc54+NX3J6XcrHj7dOHvpaXG4+GlxbNii0fePXhjs9Ebk2lBCwBVKDAhJkiwkSL6QISBr"
            + "0dQDcz2KIUCXIInn61S9Diqtns81IVHZ6rHsFXsjsnLaowz0KkO8SqFX2e+RNWWk91sd2oz4Na8S83hl"
            + "zSl0aUuEoPawcExbKhzVXMJhrVbo1PxOKrbEK7Rq/inXC19q/qkzhJPKXI9GrlQu80a606Op5Qkt/Vqs"
            + "PkNA8wsmzU8HBpjv2nfoNq85VbnN22Kli6GRGMckPWs9V1/hpN0lcrBLKyy0hJICtaFkGqCRrqdLudcr"
            + "JMQQVGZ4ZLrWLqXZc9MdNTV334WgBN+9zX1JAVtJ8oSST4R0Nje1uurub2eSA9V9xsB8996S/lSTJoHe"
            + "qXjU0MGmtXDdGC1cT0sDLY1jWOAXyif08YXrmsSscP1+MS3csE9MCTe+Kyb6KjxKwOvb57XQaAc4d4lf"
            + "y2fGxj4OISBB7UdLOi0xWoqlOlpi6nDpIbWoYFqHstqjuL3K2A6lwqNJiUq+J75vFLZTlNcP6CumljTl"
            + "weaI77YOja14kycSNQREMwts2h0LKu++KxVB+bueWEjzMlV/hNJfQkMDUmsoPyDTXdU3lIaeH5ifWRcz"
            + "ImfHm9ML85ZGHxW1WirkojLOKOhGkvsQ6M5oC8/o96GQeqEdOBOOaTvVGA3FqkIyqDMyEFFjsl+Z7JM8"
            + "6iAaVKaUomaxtmH9N4nh4m6D2CuBOEWDzSuz2OXvB89GsiVJ/RlVGkyVsqlCgtgnS5lqhm4npg6UGtVB"
            + "up0+2U/Fc8xWywkTub+k1yvvVIsnqMXqcLVIilDhx9VBP0if7K8wplC1DOo2U+qh5uwk05ogbaTyy5i8"
            + "gYkOZKJUKkuTbmDNZDHglf1mq+6oz8uMIFy8qSRdk9KZfAcTShJPOul70FVQHNOXP4mGEqWh0DCkhFfE"
            + "b87L+nXbsfhEuBigEX4ajMh7RSN9C/8kJMY/GZpkZtIBJk0/FiH5UuysmH1bcUo+e/FJrjXjn9/7eU72"
            + "5h+tKVpUdH/RQ6PFP/pmeKrDdW9UFx8N1+8oUuvCDfReH26k9wY3/aIonzUrtx5QXmmOvzZAsOCs3xLq"
            + "OW4J9dLSd7yLfizOHpf2hM4dp5vlm+N0NyorW5VdzSXZcRUDgn09x+n+jxLR2NfLWsHa6EpxvyvaKu7x"
            + "bfB2aRdbQbD2YrPOi00uudjkwxebXHqxyc7/XJV9wMcAVwITgTeBXcBu4C3gbWAPsBd4F9gH7AeagD8A"
            + "7wEf6P/tpB5oABqBR4HHgGXAcuAJ4ElgBfBbYCXwNPAM8CywClgNPAc8D6wB1gIvAC8C64DfAS8BG4CN"
            + "wCbg98BmYAvwMrAVeBXYBmwHXgNeB94AdgLJQBpwCTAEGA6UAhOAJMAEDALygCJgNGAD+gGDgXQgHyD0"
            + "MoAkgCSCJIEkg/QDSQHpD5IKMgDEBJIGkg6SAZIJkgUyEGQQyCUgg0EEkGyQHJBckDyQISD5ICJIAcgw"
            + "kEKQIpDhICNASkBGglhARoGMBikFGQNiBRkLUgZyGcjlIFeAXAlSDnIVyNUg40FsIBNAJsIMZW2rOUxv"
            + "ckFb67QuevN3+S3TLRWW2ZZ5h9ktGPHRDZzuD3oLa6OdohrUq0MyDOkZ+Y/UhWMXGq6Z7yXvb32984H7"
            + "Gn6RbrT4+9ase8lXttEJ74YFns7mTTPTJrb1r12zqsNhnfX8GcOmW9b86oZZ5zY+MaVnqaXblTjpWrv/"
            + "1b0fvPnFxodzK84Oe/sy44Q9rbYthwu97y9eoa5b22RefLS7YesJh+Ks+NtTh0ql5bfOae0V5+179dP2"
            + "29v+uvy5zWnL7Cv2jxoxeXnjrtTJ64+sXZm9d2DL4Q8aOx7YgMv73R4O5tzz9P7Htm6cZnx5cW/jdVlf"
            + "5Ca2nV0SWofKbWm3k9mTc6837U4JfHJNv+Dqy2sKZWFMeW/n+NfmftDSk3iTaUvZvUZ39NSKQ5eMnHZf"
            + "UdUtl+SU7Vj25kfi69ePutPe6l21eHt1++Cb5/qdoTsMGY3HUgLO9gFa/qqZKYZHQso6y/aaweqGD/d/"
            + "VrNowt7nx4zf6rxpXH3SLetfPL7lWMHudeM2/Lmt7Drzua6ZvpsXbzm1TV3v3Drk3b+7jjxjSVxWHzj8"
            + "zJ0JB37Z/P5jb5W9uL70k7PDpj91d0rNork1z586+euX2ubfE7n+1OTD91y1+My5/l+kiaOu2bxg+xMV"
            + "jy5bOmdK6qwvcs3Z7dW5vYND79ywO+eAZ9eJ3dfe+Gzqyh2Dx87cnLtgUSDx85krl40Yd2f3Y1mdX5at"
            + "mbGnL2XbGsvc5m9Kt4yY7Tr53sH52QXTDilHyMxDJ9afKbt0e0/TW19dfTDlrhXN8+Y4ds5/a+pox6LP"
            + "xk+8ZZlp8JwTb29cKNR3T5mV1dE0cJrt/blnb/Ybhp5NdO4qMYanRp8RNa+svNKu/zweuLadHpOubC9A"
            + "A92Iblo/aY7RWhoUyqUHgFBeQOoJDQkoJ9uFWwuw1phEv9nnipLFHvaDeI8xg/5CLJAeoQU/XpLFY5Ih"
            + "VEHPD9QJtVcRkzPHEqPdb0zJGbht2DBqyZvMzpjKl7L0rDqJ6lwjfapO92UQfShTnRSRd9ITYV13HV0M"
            + "OxOWDOnSCnIDlmg/sX8kKpakKh+1a5F3Sq+oeigYN6RkE7pgeqdW/F19QwItKTQGSSzICRgHlSWL56Rc"
            + "Gl3Q+60wlTiyXJ0SkQ8z4eri4pEjM3MDQ3KGLaU+eoKdF469AgMCKisr9X5ltJJedFyy24wuAY5KwWpv"
            + "eRx54/LKqx3jYTKZdDlzHtXLM2NhOa3LFwIOW8zm0A26qcE4Vqud3t0XTtZ2OxKMJmpwEmyOqgcdDhsE"
            + "pi+4aZ9NuOE2CSYjHTfl2Srd1Jfb7jKyvs1udzF5NxOzWqm8g12sloymcuYXul+9toJ5sQqmuHt60TbT"
            + "jPuh8VL/v6mySxCoAbfDAWqcXXTcbrdTu85Kh8NR6aT+7S673X2+tkOy26uoSzh0NQdsegJtMDKoHtVZ"
            + "SHVpqAw3Wk5Hj7S0tMDxoNtdxeQdDJvuj9lj/Qcrx+n9Sn2e5p3FKcSfjC4e9+ew2QRQW0eip6k9h40t"
            + "nY2zJcTzQv0N1dWGYhKL3pgAp953IqO8suV7f+NEPXbr+abNKFH7enpp/GCZtrN4JN0v7TvctJ9ndVlZ"
            + "3gGJZdwVl3fQTJbr8QvxnbSQJq+SxV9+Pi/ASZYAWsfloUdsO9932XV71CDrs3zQPtt49DmyDJhp33F+"
            + "HXqktKYJcLG9qicgPi4xBbe+f8z04TM94Xz+FgqCiS6Lbb4v15u+S4CS2M6O6NarSqsXiotGL0plh3ZN"
            + "NUj1aoImpXx3AvQ1tSur25UFHezMLER/5O9FDofD4XA4HA6Hw+FwOBwOh8PhcDgcDofD4XA4HA6Hw+Fw"
            + "OBwOh8PhcDgczr/nHw/BVY8AgAAA",

            // 10-bit ops.gb
            "H4sICBRqrVoAAzEwLWJpdCBvcHMuZ2IA7Zd7dBNVGsC/pG0spaQtFBz6oNNSaqlg4wMpS5xTEKmipT5A"
            + "EUXCKwiGtjyEQNtQWtcHi0cePuiKgMoilJcvKFBdkoYUhq8joAWDUpgARclCTeiOQKHJ3jvh4a4e/vDs"
            + "2f3D+zu5c1/f437f3JncAfiD4+yi/eqc2dzQCTqGzYRKiIZOEBmX8uproD/aUHi6qelIVW3t5AmF+rMN"
            + "TUerVtXuuFv4Dwvl11vmxAP/07X/F8iLA7s+NqtzEn85vhN/weoEeyrkOkH7/14Yg8FgMBgMBoPBYDAY"
            + "DAaDwWAwGAwGg8FgMBgMBoPBYDAYDAaDwWAwGAwGg8FgMBgMBoPBYPzBcPJ1hyMMV1IM8JXmwF4Qa/Yq"
            + "wnEpzO6/j/9Z7QSlznahMTTiF5VSKdZeRnvir22JHAhBuVxeIFfIlaLiOtScWhkc2nd43yf7bs6IxBzs"
            + "jgkphtix1pZzeXNbthfMa6l8qqSF78WfygimzegzvU+x56DDL5Z5te5Sb5ibCxdETis4vRq3qASido+2"
            + "Sxo3WYLAX62j1Noj7bGfUbgw6SN7Vg1/yS9KLXapi0NKdkg9HdJOu6hIvR3XdEjT71IcUtDuEBUr16zM"
            + "5TzKPK5JKeGOKKXcIaWMO6i4rERsroPbo7gGP8ydV1xD8rmz0mi7gv2lux3+0zGBqBxt/S31BqfGrbi4"
            + "aMVFBjrqJ+5oHOPQR0ljHPUGshgSiS47fInhyoICK+nOFT3NSs+eWd5wd5k3ggSoI+tpll5wcNogeKR8"
            + "u0jW2izV2R8dN3PmpIngEcD5Ql1buNuYGXFf5kkuhs4NKSqcdG0mwl3UpnNPttVkdiCaJAnkSsQDmq/o"
            + "tOIrz1Z8C0ipIKUymwZ+vZwkt89XXst39i3YyXfyVezgI32V2/kwZ4FdcjucOxxZJNqO1k/4C2LrncHj"
            + "XnALIN9CSgwpQVIyhHJSgnIvYY6cntp/v7TMLtkcUup+qcCuCGFSij20byS6U6SNu9UVE0uKNLvO7xy+"
            + "X6Erft/uD2jcvJ4GNnTcFMukiVHgEW/0+J4kL0PUWygM9vZwC3u9KW6R7Kq2HiT0FPfkuPKgDhI2f5zX"
            + "M6kk8BKvlBGhUiJjDQDZSGIbuE/H7vXl33KAi7redrf6gsoWOUhCMcggaOT8WPDLQdElDXIKdjmeBBUn"
            + "RMqdaVuzsj3Ml3Faw18SAK28xugQaeziL4OnI90FQe5GlG4lSt2JgpZvE4U4OVa1E5S7CJVyvGqnTXQR"
            + "8QS9IetENE7PvOQQt8gZ98kZci85XfAT4Zfl+F+lT3QV6CKJWixxGydcJOZMGGfQCquJ/CtUXkNFu1BR"
            + "ItVZER6hzQje7RBdeoPqqM1BjYAv4/3MGEWIofJfUaFw/qyVPAfNqRlBdfm5JJQACYWEIWjX8u1XZV2q"
            + "7WBowpcBQCI85fGLNbyOPIXfcGGhV4Yi6Km0m0qTl4VXvAO2FDw1JiMyhT74mGiI/ffnfqyVPvlHZqbP"
            + "SJ+ePqcP/7Uz317kK99UlHHEt2BzulzuqyDXBb5Kcq2wkTeK9EOd9OxuaW1d6LEB8KRedmV5Lx7L8l4i"
            + "pe1YM3lZXD4mJHqvHCObpf0Y2Y1S6x7pk7rM7iEVDXjaLh4j+z+AvK7tEm15ygKt/M7SwB5+q3OVo1m5"
            + "2Qo8ZTebtd5scu7NJufdbLLkZpMHf7+qiLmAgwAHA94POATwAcA8wAcBHwIcBvgw4COA+YAFgI8CPgb4"
            + "OOATgCMARwI+BTgK8GnA0YDPAD4LOAZwLKAJcBzgeMAJgBMBJwFOBnwecArgVMAXAC2A0wCLAIsBpwPO"
            + "AJwJOAvwRcA55CkDnAs4D7AEsBSwDHA+YDngAsAKwErAlwD/DPgK4KuArwEuBPwL4CLA1wHfAFwMuARw"
            + "KeAywDcB3wJ8B3A5YBXgXwHfBVwB+B7gKsDVgO8DfgD4IeAawL8BfgS4DnA9YDXgBsCNgJsAtwB+DPgJ"
            + "4KeAnwF+DrgVsAZwO+AOwJ2AtYBfAH4JuAvQDugArAN0Au4GdAHuAdwLKALuA0TABkAJcD/gAcCDgF8D"
            + "fgPYCHgI8FtAN+ARwO8Avwc8CtgEeBxQBvQAngA8CXgKsBnwB8AfAc8AegH/AXgW8BzgT4A+QD/gecBW"
            + "wH8CKoAXAC8CXgJsA7wMeAWwHTAIepACe/Q+chFT79g7tJlcXM2urLysgqynssYeoheP30kekxiXx9Gz"
            + "LJDDyx61ahRBow2PjOVzy4M3WkMfG9atkft0+LmtXb2zzGahrnX+1An7xqfO6HGqcuqTg5KXLP4+Z0SU"
            + "JV270vvMtI+w/POOrTG5OS/WbjHc3vzlOwfursT2qNrUZW9lZn/wc+LYvOYP7xgwwSJra63+ndhn1UMy"
            + "bYdnrm9/7ts7liVaI+Ga/JRb+bedn297zDHcGTHjQK/lQ3xVNdQ29fVA7O3vvWw8fhv1WbOtwFQse6qv"
            + "rat89qBJ1QMb+1xb76bg6vtLHtGt6XpXy5D71z4/sGjM3xu/ezHxp3fzq9546Mf1yUriqD8t7dX3sZxn"
            + "x0c0rZ+w6aQ1u//irt+PH3Xix361jtFeXSCzumDMxF2V++d/emn94l1Num0Tey2U09ty3+qyZNyyTlPW"
            + "6Ttubj+e0LjcHTfuTDfzlxUpfU7MypjT5dTUXt8eW1iQUpL3xXff3/nD7bo+A7peSeqYMHB1zMhmd9I0"
            + "y/zt8ZtWbFm6zZQ7+/S07JX7Hpg24p28fi0DHp2Qt2jhBu7CtNiatbOi49JXKBPHXO42Kenp+h0nDY8n"
            + "n9q+8sPskqIwPn3QIxfSti/96MWTyeefa69Z03rPrgv9901Oa8w/llA16tNJSzqfXzQpcsqJd795c9F0"
            + "vzut9xM/Tj0yUkgf/FNO543afi+V9am7Z+OJh6vlO99Yfb7LuoKS5dpuAxtf7zBqTXDovlsjq63aYftn"
            + "eNaNfPy1i+2Hh6f98EZ9ub/W2CFx2DDd++0jTu1y3xbfc/Diz0pGxs9Ja1l6pv/sRWX7a/Z8vfjWB2sf"
            + "/6J79ZJVRztVi+l7Hzi+4bPKdStjqsdn77Zn7rzryaop4pyHx78Q3NY0eOg8+O7M5PyhK7YuDd/6c5jG"
            + "+EFebtdhh8MHfJF9xjPg6Xenrun8Ts8qw8q1xZv2JB5Oiej69TO9Y0Y3RFV98PyYUedW7KiQZrk0PS6H"
            + "WT/J1PmGBBbzikOU3kb1OLC7H5Jj4W2YChXkkSgk9UJ9kNRCvDeRHHi8SW7hojfZLR1F7tlUWK4LJ/9R"
            + "V9Ij+Iv0APC8Lpb8I04R5pMCv10i+CZB4y0g5yXihNgrCIpxqQ06k0sXmdBlfVoaseSIoGdq6cQ+YYmc"
            + "S3TuF07JeU5oUIfi5Fy/uIWcgMtPl5PF0DNwZnKzkprozgrcwnfwB/jMKKkBFf+2vv0K53hChqQODWTB"
            + "5EqsuJrbkt31kSQGgU9NcOvi74rgr5B/x0S3x3FNmEgcflUe7BcPUeGijIzeveMS3ckJaSXEx0XPwevH"
            + "fI4CHFgsFrVvCVjIj4wLJqOulAOzhTOY6l+GpHuTcorMAyE6OlqV0ycRvSQ9FOeQOqcYwGwMGs2qQRsx"
            + "GMJgMJGr7fqXhMkEWl00MZgLRnPhbLPZCBzV52ykTydsYIvmonVkPDrJaLERXzZTqY72jSZTKZW3UTGD"
            + "gcib6Y/Wgi46h/oF1a9aG4B6MXDRIffkR9pUM+SHxEv8Ty00CcARAzazGYhx+iPjJpOJ2LVazGazxUr8"
            + "m0pNJtvV2gSCyVRIXIJZVTODUU2gEXQUokd0iokuCZVig/qWwOH6+nowz7bZCqm8mWJU/VF7tD/bcq/a"
            + "t6jzJO80Ti50Z1TxkD+z0cgBsXU40ELsmY106XScLiGUF+Kvh6rWA3Jp9DotWNW+FWJzLPW/+KYL2E2G"
            + "q02jTiD21fSS+IFm2kTjEVS/pG+2kX6SodRA8w4g0IyXhuTNJJM5avxcaCcVk+RZaPw5V/MCcJYmgNQh"
            + "eVAjNl7tl5pUe8Qg7dN8kD7deOQ+0gzoSd98dR1qpKQmCSile1VNQGhcoAo2df/oyc2netzV/BVzXDRZ"
            + "Ft1851dG30iA1LqPfpLcaeg7fsosvqh4ZhT9SlFkjbBA1ipC5I0jr3MDSpUojd5PPxK4wG98IDMYDAaD"
            + "wWAwGAwGg8FgMBgMBoPBYDAYDAaDwWAwGAwGg8FgMBgMBoPBYPwO/gUCZe6XAIAAAA==",

            // 11-op a,(hl).gb
            "H4sICAFqrVoAAzExLW9wIGEsKGhsKS5nYgDtnH+QE9UdwL/J3YVwhJDAjV2O+7HhjhhuQOIverZhe1AG"
            + "2lK8dlrFoVrjFBfF9O6QEYPk4v2waK3jj6lDi39ArRXL1B5SRxAYneyFhNvuLXqtMnszEjZIzprBm0RZ"
            + "PI4j6fdtTtTqMJ1Op/3D72f2vbfvve+P9/3u2827fw7gK058lvXYB6I4OB2mlW2CXnDAdLC76x/5JTjf"
            + "GWwbOXFieMfhw+t/1uY8M3jinR27Dh+8VvgXC12X7sS92v907f8FVroh5nQ1zazhL1RN5z8OxyHmgZY4"
            + "WP/fCyMIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgviKEef7j1f4J+r9"
            + "cMzy5gDIBwYM4aRaFssv4c+ZnaI6Mya8VRrJy0ZEdcU6WU/+oi2ZA6God+ndeo/eKxuJtzOe3uKKhTcu"
            + "vHlhn9euNCuzlep6v+v28OgHK7eMvtr6wGjvmq2j/Dz+tLc4994FGxd0pIekvNyZtWqRbJnGlQsyZxXi"
            + "WYsmG4XKI2tjqkXDJQj8ZFtptmn1aOx9gytTX4g1HeDP52V1NKbOktRaSW2Q1EMx2VDnS5/o4G0+YUhq"
            + "MSbJRpjLGFu4tPEAd8LYyg0bEe5to5MbMhJhFNsicUeNxLJV3IdGYvlq7oy6NmYoX1evlfIjMwqVzdbk"
            + "lKQ/btGMBOcwEjgwzbnu4Fu3Sc5K9TYp6cfFYCS2ReVP+Se6W8PY3SKnM0ZDQ1O2XOvMVmCANlxPRr1H"
            + "4qxFSKurYzKuNaP2x35wx6ZNd66DtADxe/rHy7WAr2KJ711uBptb3t525yczFVr7uE1bHz3gm4qamASs"
            + "UbxgOcamjVzXIiPXjaUHS+8iFvil8i4+vlzXYX5mrvsQPz3Xc5C353pf5cvirTFVk+IHpSaMdlp4H/+x"
            + "/NHVxZNZ0ATQp2CZgaWIxSt0YSnq84T79UbP0kH11zE1KqnzB9XWmCGUqfWx0r5R2U5RXzxirhgtGerm"
            + "/nz8lkGDrfjZWL5g0XgnC2zFHXeH7lxXCWn50x7fgHlZbj5CoS5bpwlHs/WajLtqvA5Dr9fWu7uKNqju"
            + "e2llQ83WwkO80YlCEZQJFwA3kjwO2ohrILd6yptc5aV77aNc0dirFzEUvw6CRV/tgrxelBPq0rgQ06sw"
            + "KLdg12eye8vOi2U574iFPy+AEuYtAUlmscufDZ6NzBYE/QpU+hoqzUYFKz8uC27dZdop6rOEXr3KtDMu"
            + "J1C82ulvOuVQNvrOS/Je3btE9+rz9EYhj8Lb9KovpE9OtNrsqOZCt25hDM0FFbffKvwO5R9m8hYmOouJ"
            + "otRMQ/g+u63gNUlOOP2mo3GJGYGc91nfDEOYweSPMaFy/kwY34OMx1s0l9+CoRQwFAxDsO7mL07KJkzb"
            + "xdJEzguAEZ5O5+UDvA3fwr9zZaVPhiE4mbTGpPFjkZWvgr2ta27z2uvZi6/M8bs+/97fHmZv/vCmxnsb"
            + "Nzbev4D/W3x1rD3X9ed273Cuu69R78r1YN2d68W6J4pfFPW9fvXWI+ru/tJrA5D2XEg0ZcdSTdnzWMZT"
            + "GfxYXEgJanYiJSSzF1O4G9Wnk+q+ft/skooF0uNjKdz/BYW3jZ9nd+nOwtP8oUghyb8S3yVljMutIN15"
            + "udnw5Sa3XG7ygctNbr3c5NB/ripXAlQD+0cfMwGaAL4BMBfgGoD9AK8DbAN4HGA7wE6A3QB9ANcDXAeg"
            + "2EBxgnIFKHWgeEG5CpTFoOALsgKUG0G5GZSfgiKC0gbKZlCioGwD5XFQtoOyE5TdoPSBsh+U10FJgnIM"
            + "lOOgpEAZAWUUlHOgFOBKACeoe5JOF6t0VuWwkj3WoysyWCUyiSbL2VT2bKrJbaSyRirqOZfKnkvl42Op"
            + "jKE+cSTdiZUUxmoLlvGzKVYbZn2O1WmpobPg4g+kzeZPMlhmuOof7MoVreV2vuXzPT0z9YULvul9Lx5+"
            + "Snv51N4jS16xTmlt+0m0z/pe1TvDr/88/509y9bbC2tsG/bsu/j8Q7meicY9f6w9fGqs4bGUs3bdUIXe"
            + "uPRH96286eywa0PzFT5tyPec/vuKVwsvnTWeWHVD2UuPTjn05LwdL4RHnhn6rvyX5zKLr1PW/mbZxqk3"
            + "PDzvzI9HEy9PvNL+2jRtRfuGK7t3jTWf2njTbE/vql/sWvVsuCp4i314jfux3md2WYae/EfPtLWv3bfN"
            + "c7L3/Ya6/Q8OfY97/vE33vjrD79V9oft8fqdjzhO1u9LWOoulIX3+Wy55YUneUOS1Z0D5mfxyJIB/Hlc"
            + "NOCBHsz8ZmwfdRaxFaqyc/DDn63RhLFsraaeHuBu9cBvbeX4rk40VvBj7EN4l82FX4a7hQexwJeXCv6E"
            + "YMm24u8GOkF7rUXZPV+2BRM2e/WsPXPnoiWpgp0t1OxR4Sm9BXW+LZzWV8btpSG33pKX9+JJoGukCxfD"
            + "zgK+2ozhmaM1FabwU/MF3lepvjVg5PcvvL7t/nTJkOqWccFYo5VEZrxWS9oxBoH3VGu2qmsq+AlhDkaX"
            + "lj4RRonjj+jL8vLbTLjd650/3z1Hq62euxV9jKWHLh13OAZwEAqFzH6oEMILx4VgwBbhQAxx/mByG9Qs"
            + "rmluF78JDofDlHPWoF6NEzqasW3uABADxYBoGoyiwRJ+fxDr6KUTVTAIVpsDDbZAQGzbLIoB4Jg+F8U+"
            + "m4hC1ME5bDjuqAmEougrGozYWD8QDEaYfJSJ+f0oL7KLtYLN0cz8gunXbP3AvPg5R8k9XnjPNEt+MF70"
            + "v6EtKACHBqKiCGicXTgeDAbRbjgkimIojP6DkWAwOtkGQQgG29AliKaaCAEzgQGwMVAPdTpQF0NlRCE5"
            + "WjieTCZB3ByNtjF5kREw/TF7rL85tNjsh8x5zDuLkys9GVO85E8MBDhAW8cLo2hPDLCls3G2hFJe0F+d"
            + "qVYHLSx6mxXCZj8MruZQ8jNn20Is6J+8DdgEtG+mF+MHlukgi0cw/WJfjGK/xh/xs7wDCCzjkZK8iJls"
            + "NuPnSjupA5MXYvE3T+YF4AxLALYleTAjDkz2I0HTHhpkfZYP7LONh8+RZcCJfXFyHWak2GICImyvmgko"
            + "jQtMIWruHyc+fKbHTeavg+McuCy2+T7c6fg0AeqFo+xodvXVC9s7+DsW+O4Kza9k5zVDtwjdutUQ7J/+"
            + "+MdfHlB/NaCuG2THJa7wJX8qEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEATxb/BP"
            + "bPVeXACAAAA=",

            // instr_timing.gb
            "H4sICH1ArVoAA2luc3RyX3RpbWluZy5nYgDtlm9oG+cdxx/dSRfHkRU7CeUW/8lJaVXF2LPask5s6mHS"
            + "0CyMxGULKZQVX1l8mRdV9pwQy7Wk2fHYuu3FVvZmfbMyBh17s6x7MdcJhJNPUnx1nrBAbS5QrxKtPCpc"
            + "IzXc4qZB2u+5k9PCMtolhWzs9/Gdfve9e57fv3t4znpPljD0u7T/8+g7uSsfqOrlFrKNP0VmiJe0kKa2"
            + "PS/9lPjevhxfXVm59sqFCye+G/etXV55+5VXL8w9Jh868u2j3xo8eujwoSMHwcPUbV/n/v70favjLjnY"
            + "RjRfa/eODunjXS3SjYROND/p1wl3vxNDEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARBEARB"
            + "EARBEARBEAT5P0OX5pc94Vt7wuSK668LxJhdsOR3KK9Vn5D+YYs63aHJbzl3qoaVpK1aiinjX30ZIpHr"
            + "hanCdOFsYcawcksl/0z9qd4jvcd6/xhsWowsfmlx955w62Bi/YODE+tvDLy4PvPM5Lr0kPResB4Y6/lB"
            + "z2jxaqZqpMqcmSzzpuiWDZGT9bLLNKxac/ZZjbpMSEGWGrbZtkV6SXvfEnn6e617VvqoatB1je7M0M4M"
            + "3Zuh5zXDovsym3PgspqzMrSuZQwrIZasCbFovSiuWJPiNSspLlkp8aqVS8CwiYx4ycrt/6b4oZU7cFhc"
            + "o89q1uJX6WOZ6ur2WnOEy2/Jh3WXaeVEr5WDG9t8x+feei7ja6bPZfJhSAYqEfrcL4dvTQ8kQE4YxZK1"
            + "d2932W2myh4oUIB8SvRkRuTqpEgPawbkWqLz2tPPnzo1dJwUZaKfnL/pNqMhzxOhd8Xt7NmBkfjQ5hOP"
            + "OXJTME+kZ0NbYSY0AX5heM11hT22KlN9VmUazrNwzvSxwm+f78Lrq0xdkHZUps9LLZWzc1JTZeYNidcH"
            + "NGpm9LlMN1S7LfG6dMO4/kj9nTIxZVLYAud2OOtwBuUpOOuFh+TxwoP+XyzQX2k0naHjC3RAs2Se7tGc"
            + "dUPZSqFEtzMGTxY9M1/VX1uwWMa/1ao1lyn5WGFPPT8cGzreTIrGJ0raC3054LzC65X6qtsOLcieRipr"
            + "BY8l72MRKttX3fNzGWbenIO+W+cKnopnVtqoGkv0/fnJ5earxgOk4ln2/GRxBxyD9vP2WaiPvjZPRd15"
            + "DwTifUOnv9P1VzN0aZ4W5mcX7Cdd3NHhF4bGpOMjQ6fiD5+WpPGRsZPS6NjI6NBYbIIUoWNfJsE30zWS"
            + "Pab3SJaR6lu9JAk3ibnq1lKrTfmNQfoj/QFyMeR+hv5Nn+xjMrh4ceH8AkhjqRQ0jqT8khloCW2TSUvI"
            + "IwcDshmYpO5s8arhJ5eZ5z+B5xtGF6FX9IvS1q66bV12v57cL0Hq4KmU8vvNgLxYlkw217BSbO1/DZZf"
            + "FTop98JFml1IcOHU7IKWD8h6oBw05Z7ywzA5Ww6Z401dZs4VNNuCpj9oQi+qJV0yrz/SZRY/grYsi0bO"
            + "1fUxn3g9JFQO1H4pWRmDrmVZva3Zn2fBfSrrJ2d9hGbB/sxXByvvKrfDMip3mPJGudOkj+bE7/jJrwX3"
            + "eI9060GPtGHAmvqe0Aqralj+IZzkzqdHWpFd5QFYChAE/A3UjbbxnKDkhKbdO/8QCICnjIftVDSSlV8u"
            + "9MOcJ+X3Cgf1Yzn7Vluhv2qcg31lanUKkmE7S6izZPnbze7aFmlrtSaFmmkgZ1X/0vuV+HjRcUQHc5Aw"
            + "/IKXXOlmp5lvghpkyb/bFHY96pFuye1QXTGzORhGLL9U2A/Ljw0eCQb37WtrNzt3ByYhxga8080tU2QQ"
            + "kcRiMVvHajE44L6sRIWkSNSYGFbyPyYdj3dERtSvE6/Xa4/zdcC8Dh8ZjYCNjBKiRutR1XaYBocO4bAC"
            + "v+nb+7OiEE7wgsN+ElXjZ1Q1SkQ2X0yDZg/SJO0VvQLc93ZEY2mIlVaSAtNRRUmy8Wk2LByG8So7mJUF"
            + "b4TFJXZc24YJixIWvU54OOCazXTiQL0Q//txRSYiOEirKgHn7ID7iqKA30RMVdVYAuIrSUVJN6xCZEWJ"
            + "Q0ii2tNUErUbGCUCA+bBnFGYC6Uy0iS/XlvO5/NEPZNOx9l4lRG14zF/TJ+JPW7rmP0c+s7qFJ03Yw93"
            + "4qnRqEjA13JtHfypUZY6u89ScPoC8brsaV2kn1UvcCRh6wRpjcTyn/pS1jQl3LiMCjL4t9sL9RPWaYXV"
            + "I9txQatp0B3hZJj1nRCZdTzpjFehkxG7ftFZSaPQvBirP9LoCyFrrAFgnfHErjja0EnF9gcOmWb9AM0W"
            + "HrxH1gEfaLWRh10pWGhAkq1VuwHOfZlNSNvrxwcvn80TG/0bFUUvpMUW34e/8X7SAHooyzau4fip02OD"
            + "p4dfGI6faGa7v1VwydMFzpKb2OfDZJt7VXfl6LUs/fMC+/CItTv843EPuHgXwDkGLGlorqG5z9D3H57n"
            + "7Tx4wvMNu6mJnScUySwP1Tp5N7SLaRfPcawOD+dY0tB8Q3MNzX1K2xEb2uHzW86GcP/pvH/rj+fdvJuD"
            + "P6hXAMuD3dQEfnn4rINyu9ysfqZdTPOcm9Xv/qz6+TtoVj//X1K/h3e7BajH43bDpgGWB7upP0f9Tj4N"
            + "c9eWv0d7r/Hv3n6hmwmCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCfMH8E+G5QWwA"
            + "gAAA"
    };
}
