package Simulation;

import java.util.ArrayList;

public class Simulation {
    // Basic Biological Altruism simulation

    // Initial distribution
    private final int initialAltruistCount;
    private final int initialCowardCount;

    // Current distribution
    private int currentAltruistCount;
    private int currentCowardCount;

    // World properties
    private final float enemyChance;

    private ArrayList<Entity> entities;

    // TODO: 21.03.23 keep data in arrays so we could plot it

    public Simulation (int initialAltruistCount, int initialCowardCount) {
        this.initialAltruistCount = initialAltruistCount;
        this.initialCowardCount = initialCowardCount;

        this.currentAltruistCount = initialAltruistCount;
        this.currentCowardCount = initialCowardCount;

        // TODO: 06.03.23 add as arg
        this.enemyChance = 0.65f;

        this.entities = new ArrayList<>();
    }

    public void simulate (int days) {
        // Initialise entities
        for (int i = 0; i < initialAltruistCount; i++) {
            this.entities.add(new Altruist());
        }

        for (int i = 0; i < initialCowardCount; i++) {
            this.entities.add(new Coward());
        }

        // Simulate
        for (int currentDay = 1; currentDay <= days; currentDay++) {
            // If no entities left end the simulation
            if (entities.size() == 0) {
                System.out.println("\nSimulation ended on day " + currentDay);
                return;
            }

            final ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);
            // TODO: 21.03.23 loop over entity couples
            for (   Entity entity : entities) {
                // Approach to food source
                // TODO: 06.03.23 later

                // TODO: 21.03.23 random one from couple is altruist other has 1 survival rate
                // TODO: 21.03.23 perception changes this 1,
                //  like if perception is 0.2 if opponent is not altruist there is a 0.2 that it will not shout out

                // Handle event
                final float enemySpawnChance = (float) Math.random();
                if (enemyChance > enemySpawnChance) {
                    // Enemy spawned
                    final float survivalRate = (float) Math.random();
                    if (entity.getSurvivalRateWhenEnemyMet() > survivalRate) {
                        // Survived
                        // TODO: 06.03.23
                    } else {
                        // Died
                        entitiesCopy.remove(entity);
                        if (entity instanceof Coward) {
                            currentCowardCount--;
                        } else if (entity instanceof Altruist) {
                            currentAltruistCount--;
                        }
                        continue;
                    }
                } else {
                    // No enemy
                    // TODO: 06.03.23
                }

                // Try getting food

                // Return
                // Repopulate
                final int entityReproductionCount = entity.getReproductionCount();
                for (int i = 0; i < entityReproductionCount; i++) {
                    if (entity instanceof Coward) {
                        entitiesCopy.add(new Coward());
                        currentCowardCount++;
                    } else if (entity instanceof Altruist) {
                        entitiesCopy.add(new Altruist());
                        currentAltruistCount++;
                    }
                }
            }
            entities = entitiesCopy;

            System.out.println("Current day " + currentDay);
            System.out.println("Current Altruists Count is " + currentAltruistCount);
            System.out.println("current Cowards Count is " + currentCowardCount);
            System.out.println();
        }
    }

    // TODO: 21.03.23 if make variables change by next generation
    // TODO: 21.03.23 like if random didn't shout out
    // TODO: 21.03.23 perception like something, like altruist should deduce if "opponent" is altruist or coward
    public class Altruist extends Entity {
        public float getSurvivalRateWhenShout () {
            return 0.1f;
        }

        @Override
        public float getSurvivalRateWhenEnemyMet() {
            return 0.2f;
        }

        @Override
        public int getReproductionCount() {
            return Utils.getRandomNumberInclusive(1, 2);
        }
    }

    public class Coward extends Entity {
        @Override
        public float getSurvivalRateWhenEnemyMet() {
            return 0.2f;
        }

        @Override
        public int getReproductionCount() {
            return Utils.getRandomNumberInclusive(1, 2);
        }
    }

    public abstract class Entity {
        public abstract float getSurvivalRateWhenEnemyMet();
        // Number of entities born on reproduction
        public abstract int getReproductionCount();
    }
}
