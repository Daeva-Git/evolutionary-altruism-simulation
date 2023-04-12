package Simulation.utils;

import java.util.Random;

public class Utils {
    private static long seed;
    public static final Random random = new Random() {
        @Override
        public synchronized void setSeed(long seed) {
            super.setSeed(seed);
            Utils.seed = seed;
        }
    };
    public static long getSeed() {
        return Utils.seed;
    }

    public static int getRandomNumber(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static int getRandomNumberInclusive(int min, int max) {
        return getRandomNumber(min, max + 1);
    }

    public static boolean checkChance (float chance) {
        final float random = Utils.random.nextFloat();
        return random <= chance;
    }

    public static float floor (float number, int n) {
        if (number == 0) return 0;
        return (float) (Math.floor(number * Math.pow(10, n)) / Math.pow(10, n));
    }

    // testing utils
    public static void test (String taskName, int iterations, Runnable task) {
        final long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            task.run();
        }
        System.out.println("elapsed nanoseconds: " + taskName + " " + (System.nanoTime() - start));
    }

    public static long getProcessDuration (Runnable process) {
        final long start = System.nanoTime();
        process.run();
        return System.nanoTime() - start;
    }
}
