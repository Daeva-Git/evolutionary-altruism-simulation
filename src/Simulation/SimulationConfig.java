package Simulation;

public class SimulationConfig {

    public final boolean usePerception = true;
    public final boolean printSpentTime = false;
    public final boolean printDailyData = false;
    public final int iterations = 100;
    public final int days = 300;

    public final float enemyMeetingChance = 0.7f;
    public final int initialAltruistCount = 100000;
    public final int initialEgoistCount = 100000;


    // reproduction weights
    public final int minReproductionWeight = 70;
    public final int maxReproductionWeight = 24;

    // egoists
    public final float egoistSurvivalRate = 0.9f;
    public final int egoistReproductionCountMin = 0;
    public final int egoistReproductionCountMax = 1;
    public final int egoistDeathAge = 5;

    // altruist
    public final float altruistSurvivalRate = 0.9f;
    public final int altruistReproductionCountMin = 0;
    public final int altruistReproductionCountMax = 1;
    public final int altruistDeathAge = 5;

    // altruist perception
    public final float altruistPerception = 0.1818f;
    public final float altruistMaxPerception = 1f;
    public final float altruistMinPerception = 0.0f;
    public final float perceptionIncreaseCount = 0.0052f;
    public final float perceptionDecreaseCount = 0.0012f;
}
