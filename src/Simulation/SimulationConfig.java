package Simulation;

public class SimulationConfig {
    public final float enemyMeetingChance = 0.7f;
    public final int initialAltruistCount = 100;
    public final int initialEgoistCount = 20;
    public final boolean usePerception = false;
    public final int days = 50;
    public final int iterations = 100;

    // reproduction weights
    public final int minReproductionWeight = 3;//700;
    public final int maxReproductionWeight = 2;//235;

    // egoists
    public final float egoistSurvivalRate = 0.9f;
    public final int egoistReproductionCountMin = 0;
    public final int egoistReproductionCountMax = 1;
    public final int egoistDeathAge = 1000;

    // altruist
    public final float altruistSurvivalRate = 0.9f;
    public final int altruistReproductionCountMin = 0;
    public final int altruistReproductionCountMax = 1;
    public final int altruistDeathAge = 1000;

    // altruist perception
    public final float altruistPerception = 0.1818f;
    public final float altruistMaxPerception = 1f;
    public final float altruistMinPerception = 0.0f;
    public final float perceptionIncreaseCount = 0;//0.05f;
    public final float perceptionDecreaseCount = 0;//0.001f;
}
