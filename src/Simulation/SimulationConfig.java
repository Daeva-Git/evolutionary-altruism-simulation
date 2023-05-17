package Simulation;

public class SimulationConfig {
    public final float enemyMeetingChance = 0.7f;
    public final int initialAltruistCount = 9000;
    public final int initialEgoistCount = 900;
    public final int days = 100;
    public final int iterations = 1000;


    public final boolean usePerception = true;
    public final float perceptionIncreaseCount = 0.05f;
    public final float perceptionDecreaseCount = 0.001f;

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
    public final float altruistPerception = 0;//0.1089f;

    public final float altruistMaxPerception = 0.2045f;
    public final float altruistMinPerception = 0.0f;

    public final int minReproductionWeight = 8596;
    public final int maxReproductionWeight = 1404;

}
