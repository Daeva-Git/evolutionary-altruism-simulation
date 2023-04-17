package Simulation;

public class Entity {
    // petition to notify danger when enemy met
    public float survivalRate;

    // survival rate when enemy met
    public boolean isAltruist;

    // number of entities born on reproduction
    public int reproductionCountMin;
    public int reproductionCountMax;

    public int nutrientsNecessaryForReproduction;
    public int currentNutrients;

    public float perception;
}