package Simulation;

public class Main {
    public static void main(String[] args) {
        Utils.random.setSeed(1000);

        final Simulation simulation = new Simulation();
        simulation.setData(20, 20, 50);
        simulation.simulate();
        simulation.printData();
    }
}
