package Simulation;

import Simulation.utils.EntityArray;
import Simulation.utils.pool.EntityPool;
import Simulation.utils.Utils;

/*
 * Evolutionary Biological Altruism Simulation
 * created by Daeva
 */
public class Simulation {
    private SimulationConfig config;

    // world properties
    private EntityArray entities;
    private final EntityPool entityPool = new EntityPool();

    // statistics
    public int[] altruistCountDailyData;
    public int[] egoistCountDailyData;
    public float[] dayCompleteDuration;

    // day counters
    private int currentDay;
    private int simulationEndDay;

    // flags
    public boolean printSpentTime;
    public boolean printDailyData;

    // current milestones
    // TODO: 3/22/2023 fully implement nutrients parameter for entities

    private void reset () {
        this.currentDay = 0;
        this.entities = new EntityArray();

        // initialise data containers
        this.altruistCountDailyData = new int[config.days + 1];
        this.egoistCountDailyData = new int[config.days + 1];
        this.dayCompleteDuration = new float[config.days + 1];
    }

    public void setConfig(SimulationConfig config) {
        this.config = config;

        reset();
    }

    public void simulate () {
        if (config == null) throw new RuntimeException("No data given");

        System.out.println("Starting simulation for " + config.days + " days (Seed " + Utils.getSeed() + ")");

        // initialise entities
        for (int i = 0; i < config.initialAltruistCount; i++) {
            summonAltruist(false, null);
        }

        for (int i = 0; i < config.initialEgoistCount; i++) {
            summonEgoist(false);
        }

        // print first day data
        if (printDailyData) {
            printDay(currentDay);
        }

        // simulate
        final long startTime = System.currentTimeMillis();

        for (int currentDay = 1; currentDay <= config.days; currentDay++) {
            final long dayStartTime = System.currentTimeMillis();

            // update current date
            this.currentDay = currentDay;

            final int previousDay = currentDay - 1;
            this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[previousDay];
            this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[previousDay];

            // shuffle entities to make couples
            entities.shuffle(Utils.random);

            // loop over entities with couples
            final int entitiesCount = entities.size();
            int i = 0, entitiesHandled = 0;
            while (i < entities.getLastIndex() && entitiesHandled < entitiesCount - 1) {
                // get entity index
                while (entities.getEntityAt(i) == null) i++;
                final int entityIndex = i++;

                // get opponent index
                while (entities.getEntityAt(i) == null) i++;
                final int opponentIndex = i++;

                handleInteraction(entityIndex, opponentIndex);
                entitiesHandled += 2;
            }

            // check last element
            if (entitiesHandled != entitiesCount) {
                while (entities.getEntityAt(i) == null) i++;
                handleInteraction(i);
            }

            entityPool.freeScheduled();
            entities.removeScheduled();
            entities.addScheduled();

            // calculate day complete duration
            final long dayEndTime = System.currentTimeMillis();
            final float dayLastDuration = (dayEndTime - dayStartTime) / 1000.0f;
            this.dayCompleteDuration[currentDay] = dayLastDuration;

            // print current day data to keep track of the simulation
            if (printDailyData) {
                printDay(currentDay);
            }
        }

        endSimulation(startTime, config.days);
    }

    private void handleInteraction (int entityIndex) {
        final Entity entity = entities.getEntityAt(entityIndex);

        if (metDanger(config.enemyMeetingChance)) {
            // as no opponent exists if notices runs away else dies
            if (Utils.checkChance(0.5f)) {
                killEntity(entity, entityIndex);
            }
        }

        if (entity.isAlive) {
            handleReproduction(entity);
            growUp(entity, entityIndex);
        }
    }

    private void handleInteraction (int entityIndex, int opponentIndex) {
        final Entity entity = entities.getEntityAt(entityIndex);
        final Entity opponent = entities.getEntityAt(opponentIndex);

        if (metDanger(config.enemyMeetingChance)) {
            if (shouldNotify(entity, opponent)) {
                // altruistic behavior: entity yells endangers his life
                if (survivedDanger(entity)) {
                    if (config.usePerception) {
                        // if entity survived and the opponent was egoist increase perception
                        if (!opponent.isAltruist) {
                            entity.perception = Math.min(config.altruistMaxPerception, entity.perception + config.perceptionIncreaseCount);
                        }
                    }
                } else {
                    killEntity(entity, entityIndex);
                }
            } else {
                // opponent dies
                killEntity(opponent, opponentIndex);
            }
        }

        // handle reproduction if alive
        if (entity.isAlive) {
            handleReproduction(entity);
            growUp(entity, entityIndex);
        }
        if (opponent.isAlive) {
            handleReproduction(opponent);
            growUp(opponent, opponentIndex);
        }
    }

    private void handleReproduction (Entity entity) {
        entity.currentNutrients++;

        // reproduce
        final int reproductionCount = getReproductionCount(entity);
        for (int i = 0; i < reproductionCount; i++) {
            if (entity.isAltruist) {
                summonAltruist(true, entity);
            } else {
                summonEgoist(true);
            }
        }
        entity.currentNutrients -= entity.nutrientsNecessaryForReproduction;

        if (config.usePerception) {
            // if the entity is an altruist decrease perception
            if (entity.isAltruist) {
                entity.perception = Math.max(config.altruistMinPerception, entity.perception - config.perceptionDecreaseCount);
            }
        }
    }

    public void growUp (Entity entity, int entityIndex) {
        entity.age++;
        if (checkDyingForAge(entity)) {
            killEntity(entity, entityIndex);
        }
    }

    public void endSimulation (long startTime, int simulationEndDay) {
        final long endTime = System.currentTimeMillis();
        this.simulationEndDay = simulationEndDay;

        System.out.println("Simulation ended on day " + currentDay );
        System.out.println("Elapsed time is " + ((endTime - startTime) / 1000) + " seconds");
    }

    // creating or destroying entities
    public void summonAltruist (boolean schedule, Entity parent) {
        // create entity
        final Entity entity = entityPool.obtain();
        entity.isAltruist = true;
        entity.survivalChance = config.altruistSurvivalRate;
        entity.reproductionCountMin = config.altruistReproductionCountMin;
        entity.reproductionCountMax = config.altruistReproductionCountMax;
        entity.age = 0;
        entity.isAlive = true;

        // keep parent perception if exist
        if (config.usePerception) {
            if (parent == null) {
                entity.perception = config.altruistPerception;
            } else {
                entity.perception = parent.perception;
            }
        }

        // update data
        this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] + 1;
        this.entities.add(entity, schedule);
    }

    public void summonEgoist (boolean schedule) {
        // create entity
        final Entity entity = entityPool.obtain();
        entity.isAltruist = false;
        entity.survivalChance = config.egoistSurvivalRate;
        entity.reproductionCountMin = config.egoistReproductionCountMin;
        entity.reproductionCountMax = config.egoistReproductionCountMax;
        entity.age = 0;
        entity.isAlive = true;
        entity.perception = 0;

        // update data
        this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[currentDay] + 1;
        this.entities.add(entity, schedule);
    }

    public void killEntity (Entity entity, int entityIndex) {
        // remove from entities
        this.entities.remove(entityIndex, true);
        this.entityPool.free(entity, true);
        entity.isAlive = false;

        // update data
        if (entity.isAltruist) {
            this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] - 1;
        } else {
            this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[currentDay] - 1;
        }
    }

    // checking events
    public boolean shouldNotify (Entity entity, Entity opponent) {
        if (entity.isAltruist) {
            if (config.usePerception) {
                // if entity is altruist try to figure out the opponent
                if (figureOut(entity)) {
                    // if entity could figure out, notify if opponent is altruist, else do not
                    return opponent.isAltruist;
                }
            }
            return true;
        }

        // if entity was not altruist do not notify
        return false;
    }

    public boolean figureOut (Entity entity) {
        return Utils.checkChance(entity.perception);
    }

    public boolean metDanger (float dangerChance) {
        return Utils.checkChance(dangerChance);
    }

    public boolean survivedDanger (Entity entity) {
        return Utils.checkChance(entity.survivalChance);
    }

    public boolean checkDyingForAge (Entity entity) {
        if (entity.isAltruist) {
            return entity.age >= config.altruistDeathAge;
        } else {
            return entity.age >= config.egoistDeathAge;
        }
    }

    // entity utils
    public int getReproductionCount (Entity entity) {
        if (entity.nutrientsNecessaryForReproduction > entity.currentNutrients) return 0;

        int weightA = config.minReproductionWeight;
        int weightB = config.maxReproductionWeight;
        int totalWeight = weightA + weightB;

        int rand = Utils.getRandomNumberInclusive(1, totalWeight);
        if (rand <= weightA) {
            return entity.reproductionCountMin;
        } else {
            return entity.reproductionCountMax;
        }
    }

    // simulation utils
    public void printData () {
        for (int currentDay = 0; currentDay <= simulationEndDay; currentDay++) {
            printDay(currentDay);
        }
    }

    private void printDay (int day) {
        final int altruistCountDailyDatum = this.altruistCountDailyData[day];
        final int egoistCountDailyDatum = this.egoistCountDailyData[day];
        final int totalEntities = egoistCountDailyDatum + altruistCountDailyDatum;
        final float egoistsPercent = Math.round(egoistCountDailyDatum * 1.0f / totalEntities * 100.0f);
        final float altruistsPercent = 100 - egoistsPercent;
        final float currentDayCompleteDuration = dayCompleteDuration[day];

        System.out.printf(" %-20s%12s\t\t %s%13s%n",
                "\u001B[47;30;1m Current day:", day,
                "duration secs:", currentDayCompleteDuration + " \u001B[0m");

        print("\u001B[36m", "altruists:", altruistCountDailyDatum);
        System.out.print("\t\t");
        println("\u001B[31;2m", "egoists:", egoistCountDailyDatum);

        print("\u001B[36m", "altruists:", "% " + altruistsPercent);
        System.out.print("\t\t");
        println("\u001B[31;2m", "egoists:", "% " + egoistsPercent);
    }

    private static void print (String color, Object param, Object arg) {
        System.out.printf("%-25s%10s", color + " " + param + "\u001B[0m", arg);
    }

    private static void println (String color, Object param, Object arg) {
        print(color, param, arg);
        System.out.println();
    }
}
