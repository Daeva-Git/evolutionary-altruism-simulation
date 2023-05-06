package Simulation;

public class SimulationData {
    public float enemyMeetingChance = 0.7f;
    public int initialAltruistCount = 20;
    public int initialEgoistCount = 20;
    public int days = 50;

    public boolean usePerception = true;
    public float perceptionIncreaseCount = 0.25f;
    public float perceptionDecreaseCount = 0.05f;

    // egoists
    public float egoistSurvivalRate = 0.0f;
    public int egoistReproductionCountMin = 1;
    public int egoistReproductionCountMax = 2;

    // altruist
    public float altruistSurvivalRate = 0.9f;
    public int altruistReproductionCountMin = 1;
    public int altruistReproductionCountMax = 2;
    public float altruistPerception = 0.0f;
}
