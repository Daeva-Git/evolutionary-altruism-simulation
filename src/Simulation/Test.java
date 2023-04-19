package Simulation;

import Simulation.utils.Utils;

public class Test {
    public static void main(String[] args) {
        Utils.random.setSeed(Utils.random.nextInt());

        testOnSeed(-1121576342);
    }

    public static void testOnSeed (long seed) {
        Utils.random.setSeed(seed);

        final Simulation simulation = new Simulation();
        final SimulationData data = new SimulationData();

        simulation.setData(data);
        simulation.printSpentTime = false;
        simulation.printDailyData = true;
        simulation.simulate();
    }
}
