package Simulation;

import Simulation.utils.EntityArray;
import Simulation.utils.pool.EntityPool;
import Simulation.utils.Utils;

/*
 * Evolutionary Biological Altruism Simulation
 * created by Daeva
 */
public class Simulation {
    private SimulationData data;

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
        this.altruistCountDailyData = new int[data.days + 1];
        this.egoistCountDailyData = new int[data.days + 1];
        this.dayCompleteDuration = new float[data.days + 1];
    }

    public void setData (SimulationData data) {
        this.data = data;

        reset();
    }

    public void simulate () {
        if (data == null) throw new RuntimeException("No data given");

        System.out.println("Starting simulation for " + data.days + " days (Seed " + Utils.getSeed() + ")");

        // initialise entities
        for (int i = 0; i < data.initialAltruistCount; i++) {
            summonAltruist(false, null);
        }

        for (int i = 0; i < data.initialEgoistCount; i++) {
            summonEgoist(false);
        }

        // print first day data
        if (printDailyData) {
            printDay(currentDay);
        }

        // simulate
        final long startTime = System.currentTimeMillis();

        for (int currentDay = 1; currentDay <= data.days; currentDay++) {
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
            while (i < entities.getLastIndex() && entitiesHandled < entities.size() - 1) {
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
            if (entitiesHandled != entities.size()) {
                while (entities.getEntityAt(i) == null) i++;
                handleInteraction(i);
            }

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

            // if no entities left end the simulation
            if (entitiesCount == 0) {
                endSimulation(startTime, data.days);
                return;
            }
        }

        endSimulation(startTime, data.days);
    }

    private void handleInteraction (int entityIndex) {
        final Entity entity = entities.getEntityAt(entityIndex);

        if (metDanger(data.enemyMeetingChance)) {
            if (!survivedDanger(entity)) {
                killEntity(entity, entityIndex);
            }
        } else {
            handleReproduction(entity);
        }
    }

    private void handleInteraction (int firstEntityIndex, int secondEntityIndex) {
        final Entity firstEntity = entities.getEntityAt(firstEntityIndex);
        final Entity secondEntity = entities.getEntityAt(secondEntityIndex);

        if (metDanger(data.enemyMeetingChance)) {
            // handle
            if (shouldNotify(firstEntity, secondEntity)) {
                // altruistic behavior (first entity yells endangers his life, second one runs)
                if (data.usePerception) {
                    if (secondEntity.isAltruist) {
                        // if opponent is altruist decrease perception
                        firstEntity.perception -= data.perceptionDecreaseCount;
                    }
                }

                // check if first survives
                final boolean survived = survivedDanger(firstEntity);
                if (survived) {
                    if (data.usePerception) {
                        // if entity survived and the opponent was egoist increase perception
                        if (!secondEntity.isAltruist) {
                            // TODO: 11.04.23 note perception can get higher 1
                            firstEntity.perception += data.perceptionIncreaseCount;
                        }
                    }
                } else {
                    killEntity(firstEntity, firstEntityIndex);
                }
            } else {
                // egoist behavior (first entity runs and saves his life, second one dies)
                killEntity(secondEntity, secondEntityIndex);
            }
        } else {
            // no danger met -> get food, return to repopulate
            firstEntity.currentNutrients++;
            secondEntity.currentNutrients++;

            // check reproduction
            handleReproduction(firstEntity);
            handleReproduction(secondEntity);

            if (data.usePerception) {
                // TODO: 11.04.23 note perception can get lower than 0
                if (firstEntity.isAltruist) {
                    // if entity is altruist decrease perception
                    firstEntity.perception -= data.perceptionDecreaseCount;
                }
                if (secondEntity.isAltruist) {
                    // if opponent is altruist decrease perception
                    secondEntity.perception -= data.perceptionDecreaseCount;
                }
            }
        }
    }

    public void endSimulation (long startTime, int simulationEndDay) {
        final long endTime = System.currentTimeMillis();
        this.simulationEndDay = simulationEndDay;

        System.out.println("Simulation ended on day " + currentDay );
        System.out.println("Elapsed time is " + ((endTime - startTime) / 1000) + " seconds");
    }

    // handling events
    public void handleReproduction (Entity entity) {
        final int reproductionCount = getReproductionCount(entity);

        if (reproductionCount <= 0) return;

        for (int i = 0; i < reproductionCount; i++) {
            if (entity.isAltruist) {
                summonAltruist(true, entity);
            } else {
                summonEgoist(true);
            }
        }
        entity.currentNutrients -= entity.nutrientsNecessaryForReproduction;
    }

    // creating or destroying entities
    public void summonAltruist (boolean schedule, Entity parent) {
        // create entity
        final Entity entity = entityPool.obtain();
        entity.isAltruist = true;
        entity.survivalChance = data.altruistSurvivalRate;
        entity.reproductionCountMin = data.altruistReproductionCountMin;
        entity.reproductionCountMax = data.altruistReproductionCountMax;
        // keep parent perception if exist
        if (data.usePerception && parent != null) {
            entity.perception = parent.perception;
        } else {
            entity.perception = data.altruistPerception;
        }

        // update data
        this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] + 1;
        this.entities.add(entity, schedule);
    }

    public void summonEgoist (boolean schedule) {
        // create entity
        final Entity entity = entityPool.obtain();
        entity.isAltruist = false;
        entity.survivalChance = data.egoistSurvivalRate;
        entity.reproductionCountMin = data.egoistReproductionCountMin;
        entity.reproductionCountMax = data.egoistReproductionCountMax;

        // update data
        this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[currentDay] + 1;
        this.entities.add(entity, schedule);
    }

    public void killEntity (Entity entity, int entityIndex) {
        // remove from entities
        this.entities.remove(entityIndex, true);
        this.entityPool.free(entity);

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
            if (data.usePerception) {
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

    // entity utils
    public int getReproductionCount (Entity entity) {
        if (entity.nutrientsNecessaryForReproduction > entity.currentNutrients) return 0;
        return Utils.getRandomNumberInclusive(entity.reproductionCountMin, entity.reproductionCountMax);
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
