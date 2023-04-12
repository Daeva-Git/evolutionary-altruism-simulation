package Simulation;

public class Entity {
    // survival rate when enemy met
    public float survivalRate;

    // petition to notify danger when enemy met
    // TODO: 11.04.23 later change into boolean (altruist or no)
    public float dangerNotifyChance;

    // number of entities born on reproduction
    public int reproductionCountMin;
    public int reproductionCountMax;

    public int nutrientsNecessaryForReproduction;
    public int currentNutrients;

    public float perception;
}