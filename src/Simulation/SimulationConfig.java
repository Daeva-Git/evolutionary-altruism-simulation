package Simulation;

public class SimulationConfig {

    public final boolean usePerception = true;
    public final boolean printSpentTime = false;
    public final boolean printDailyData = true;
    public final int iterations = 100;
    public final int days = 150;

    public final float enemyMeetingChance = 0.7f;
    public final int initialAltruistCount = 10000;
    public final int initialEgoistCount = 10000;


    // reproduction weights
    public final int minReproductionWeight = 7;
    public final int maxReproductionWeight = 4;

    // egoists
    public final float egoistSurvivalRate = 0.9f;
    public final int egoistReproductionCountMin = 0;
    public final int egoistReproductionCountMax = 1;
    public final int egoistDeathAge = 6;

    // altruist
    public final float altruistSurvivalRate = 0.9f;
    public final int altruistReproductionCountMin = 0;
    public final int altruistReproductionCountMax = 1;
    public final int altruistDeathAge = 6;

    // altruist perception
    public final float altruistPerception = 0.1818f;
    public final float altruistMaxPerception = 0.2f;
    public final float altruistMinPerception = 0.1636f;
    public final float perceptionIncreaseCount = 0.0039f;
    public final float perceptionDecreaseCount = 0.001f;
}
