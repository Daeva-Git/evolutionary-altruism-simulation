package Simulation;

public class SimulationConfig {
    public float enemyMeetingChance = 0.7f;
    public int initialAltruistCount = 200;
    public int initialEgoistCount = 200;
    public int days = 100;

    public boolean usePerception = true;
    public float perceptionIncreaseCount = 0.05f;
    public float perceptionDecreaseCount = 0.005f;

    // egoists
    public float egoistSurvivalRate = 0.9f;
    public int egoistReproductionCountMin = 1;
    public int egoistReproductionCountMax = 2;
    public int egoistDeathAge = 6;

    // altruist
    public float altruistSurvivalRate = 0.9f;
    public int altruistReproductionCountMin = 1;
    public int altruistReproductionCountMax = 2;
    public int altruistDeathAge = 6;
    public float altruistPerception = 0.177f;

    public float altruistMaxPerception = 0.25f;
    public float altruistMinPerception = 0.0f;
}
