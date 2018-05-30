package ch.epfl.gameboj;

        import  java.util.Random;

public final class RandomGenerator {
    public final static int RANDOM_ITERATIONS = 3000;

    public static int randomInt (int max, int min) {
        Random rand = new Random ();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    public static int randomIntBetweenOneAndZero () {
        return randomInt(1, 0);
    }
    public static int randomBit (int n) {
        int bit = 0;
        for (int i=0; i <n; i++) {
            bit = bit | (randomIntBetweenOneAndZero () << i);
        }
        return bit;
    }
    public static boolean randomBoolean () {
        int i = randomIntBetweenOneAndZero ();
        switch (i) {
        case 1:
            return true;
        case 0:
            return false;
        default:
            throw new IllegalArgumentException ();
        }
    }
}