package Simulation;

import java.util.Random;

public class Utils {
    public static final Random random = new Random();

    public static int getRandomNumber(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static int getRandomNumberInclusive(int min, int max) {
        return getRandomNumber(min, max + 1);
    }
}
