package Simulation;

import java.util.Comparator;
import java.util.Random;

public class Utils {
//    public static final Random random = new Random();
    public static final ThreadLocal<Random> random = ThreadLocal.withInitial(() -> new Random(50L));

    public static int getRandomNumber(int min, int max) {
        return random.get().nextInt(max - min) + min;
    }

    public static int getRandomNumberInclusive(int min, int max) {
        return getRandomNumber(min, max + 1);
    }

    public static Comparator<Entity> entityComparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
            return Float.compare(o1.dangerNotifyChance, o2.dangerNotifyChance);
        }
    };
}
