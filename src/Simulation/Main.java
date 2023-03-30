package Simulation;

import Simulation.utils.Utils;

public class Main {
    public static void main(String[] args) {
        final Simulation simulation = new Simulation();
        long startTime = System.currentTimeMillis();
        int iterations = 10;

        final int[][] altruistCountDailyData = new int[iterations][];
        final int[][] egoistCountDailyData = new int[iterations][];

        for (int i = 0; i < iterations; i++) {
            Utils.random.setSeed(Utils.random.nextInt());

            simulation.setData(20, 20, 60);
            simulation.printSpentTime = false;
            simulation.printDailyData = false;
            simulation.simulate();

            altruistCountDailyData[i] = simulation.altruistCountDailyData.clone();
            egoistCountDailyData[i] = simulation.egoistCountDailyData.clone();
        }

        System.out.println("Time taken for " + iterations + " iterations: " + (System.currentTimeMillis() - startTime) / 1000 + "(secs)");
    }
}
