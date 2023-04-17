package Simulation;

public class SimulationData {
    public float enemyMeetingChance = 0.7f;
    public int initialAltruistCount = 10;
    public int initialEgoistCount = 10;
    public int days = 100;

    public boolean usePerception = true;
    public float perceptionIncreaseCount = 0.3f;
    public float perceptionDecreaseCount = 0.05f;

    // egoists
    public float egoistSurvivalRate = 0.0f;
    public float egoistDangerNotifyChance = 0.0f;
    public int egoistReproductionCountMin = 1;
    public int egoistReproductionCountMax = 2;
    public float egoistPerception = 1.0f;

    // altruist
    public float altruistSurvivalRate = 0.8f;
    public float altruistDangerNotifyChance = 1.0f;
    public int altruistReproductionCountMin = 1;
    public int altruistReproductionCountMax = 2;
    public float altruistPerception = 0.0f;
}
