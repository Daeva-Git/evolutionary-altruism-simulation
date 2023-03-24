package Simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Main {


    public static void main(String[] args) throws InterruptedException {
        final Simulation simulation = new Simulation();
        simulation.setData(20, 20, 10);
        simulation.simulate(10);
        simulation.printData();

        // fix seed
//        final ThreadLocal<Random> random = ThreadLocal.withInitial(Random::new);
//        random.get().setSeed(50);
//        final ThreadLocalRandom random = ThreadLocalRandom.current();
//        random.setSeed(50);

//        final Random random = Utils.random;
//        random.setSeed(500);
//        final ArrayList<Integer> objects = new ArrayList<>();
//        final ArrayList<Thread> threads = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            objects.add(i);
//        }
//
//        for (int i = 0; i < 10; i++) {
//            final ArrayList<Object> clone = new ArrayList<>(objects);
//
//            for (Object object : clone) {
//                final Thread thread = new Thread(() -> {
//                    final float randomFloat = random.nextFloat();
//                    if (randomFloat > 0.5f) {
//                        objects.remove(object);
//                    }
//                });
//                threads.add(thread);
//                thread.start();
//            }
//
//            for (Thread thread : threads) {
//                thread.join();
//            }
//
//            Collections.sort(objects);
//            System.out.println("objects = " + objects);
//        }
    }
}
