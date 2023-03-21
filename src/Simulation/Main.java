package Simulation;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final Simulation simulation = new Simulation(20, 20);
        simulation.simulate(50);
    }
}
