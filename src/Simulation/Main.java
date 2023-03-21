package Simulation;

public class Main {
    public static void main(String[] args) {
        final Simulation simulation = new Simulation();
        simulation.setData(20, 20, 20);
        simulation.simulate();
        simulation.printData();
    }
}
