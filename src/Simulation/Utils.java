package Simulation;

public class Utils {
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int getRandomNumberInclusive(int min, int max) {
        return getRandomNumber(min, max + 1);
    }
}
