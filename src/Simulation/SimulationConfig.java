package Simulation;

public class SimulationConfig {
    public final float enemyMeetingChance = 0.7f;
    public final int initialAltruistCount = 500;
    public final int initialEgoistCount = 500;
    public final boolean usePerception = true;
    public final int days = 100;
    public final int iterations = 1000;

    // reproduction weights
    public final int minReproductionWeight = 7932;
    public final int maxReproductionWeight = 2068;


    // egoists
    public final float egoistSurvivalRate = 0.9f;
    public final int egoistReproductionCountMin = 0;
    public final int egoistReproductionCountMax = 1;
    public final int egoistDeathAge = 100;

    // altruist
    public final float altruistSurvivalRate = 0.9f;
    public final int altruistReproductionCountMin = 0;
    public final int altruistReproductionCountMax = 1;
    public final int altruistDeathAge = 100;

    // altruist perception
    public final float altruistPerception = 0.1818f;
    public final float altruistMaxPerception = 0.2045f;
    public final float altruistMinPerception = 0.0f;
    public final float perceptionIncreaseCount = 0;//0.05f;
    public final float perceptionDecreaseCount = 0;//0.001f;
}
