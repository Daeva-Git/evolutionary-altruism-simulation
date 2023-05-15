package Simulation;

import Simulation.utils.Utils;

public class Test {
    public static void main(String[] args) {
        Utils.random.setSeed(Utils.random.nextInt());

        testOnSeed(-1369215164);
    }

    public static void testOnSeed (long seed) {
        Utils.random.setSeed(seed);

        final Simulation simulation = new Simulation();
        final SimulationConfig data = new SimulationConfig();

        simulation.setConfig(data);
        simulation.printSpentTime = false;
        simulation.printDailyData = true;
        simulation.simulate();
    }
}
