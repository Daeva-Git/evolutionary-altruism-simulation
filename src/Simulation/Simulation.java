package Simulation;

import Simulation.utils.EntityArray;
import Simulation.utils.pool.EntityPool;
import Simulation.utils.Utils;

/*
 * Evolutionary Biological Altruism Simulation
 * created by Daeva
 */
public class Simulation {

    // world properties
    private final float enemyMeetingChance = 0.7f;
    private EntityArray entities;
    private final EntityPool entityPool = new EntityPool();

    // statistics
    private int initialAltruistCount;
    private int initialEgoistCount;
    public int[] altruistCountDailyData;
    public int[] egoistCountDailyData;
    public float[] dayCompleteDuration;

    // day counters
    private int days;
    private int currentDay;
    private int simulationEndDay;

    // flags
    public boolean printSpentTime;
    public boolean printDailyData;

    // current milestones
    // TODO: 3/22/2023 make entity variables change by next generation
    // TODO: 3/22/2023 entities have something like perception, altruist should deduce if "opponent" is altruist or egoist
    // TODO: 3/22/2023 fully implement nutrients parameter for entities

    private void resetData () {
        this.currentDay = 0;
        this.entities = new EntityArray();

        // initialise data containers
        this.altruistCountDailyData = new int[days + 1];
        this.egoistCountDailyData = new int[days + 1];
        this.dayCompleteDuration = new float[days + 1];
    }

    public void setData (int initialAltruistCount, int initialEgoistCount, int days) {
        this.initialAltruistCount = initialAltruistCount;
        this.initialEgoistCount = initialEgoistCount;
        this.days = days;

        resetData();
    }

    public void simulate () {
        System.out.println("\nStarting simulation for " + days + " days (Seed " + Utils.getSeed() + ")\n");

        // initialise entities
        for (int i = 0; i < initialAltruistCount; i++) {
            summonAltruist(false);
        }

        for (int i = 0; i < initialEgoistCount; i++) {
            summonEgoist(false);
        }

        // print first day data
        if (printDailyData) {
            printDay(currentDay);
        }

        // simulate
        final long startTime = System.currentTimeMillis();

        for (int currentDay = 1; currentDay <= days; currentDay++) {
            final long dayStartTime = System.currentTimeMillis();

            // update current date
            this.currentDay = currentDay;

            final int previousDay = currentDay - 1;
            this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[previousDay];
            this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[previousDay];

            // optimization stats
            long reproductionTime = 0;
            long killingEntityTime = 0;
            long gettingEntitiesTime = 0;
            long checkingDangerMetTime = 0;
            long checkingNotifyTime = 0;
            long start;

            // shuffle entities to make couples
            entities.shuffle(Utils.random);

            long total = System.nanoTime();

            // loop over entities with couples
            for (int entityIndex = 0; entityIndex < entities.size() - 1; entityIndex += 2) {
                // get a couple of entities
                start = System.nanoTime();
                final int coupleIndex = entityIndex + 1;
                final Entity entity = entities.get(entityIndex);
                final Entity couple = entities.get(coupleIndex);
                gettingEntitiesTime += System.nanoTime() - start;

                // handle event
                start = System.nanoTime();
                if (metDanger(enemyMeetingChance)) {
                    checkingDangerMetTime += System.nanoTime() - start;
                    start = System.nanoTime();
                    if (shouldNotify(entity)) {
                        checkingNotifyTime += System.nanoTime() - start;
                        // altruistic behavior (first entity yells endangers his life, second one runs)

                        // check if first survives
                        start = System.nanoTime();
                        handleDanger(entity, entityIndex);
                        killingEntityTime += System.nanoTime() - start;
                    } else {
                        // egoist behavior (first entity runs and saves his life, second one dies)
                        start = System.nanoTime();
                        killEntity(couple, coupleIndex);
                        killingEntityTime += System.nanoTime() - start;
                    }
                } else {
                    // no danger met -> get food, return to repopulate
                    entity.currentNutrients++;
                    couple.currentNutrients++;

                    // check reproduction
                    start = System.nanoTime();
                    handleReproduction(entity);
                    handleReproduction(couple);
                    reproductionTime += System.nanoTime() - start;
                }
            }

            // last entity without couple case
            final boolean enemyWithoutCoupleLeft = entities.size() % 2 == 1;
            if (enemyWithoutCoupleLeft) {
                start = System.nanoTime();
                final int leftEntityIndex = entities.size() - 1;
                final Entity leftEntity = entities.get(leftEntityIndex);
                gettingEntitiesTime += System.nanoTime() - start;

                if (metDanger(enemyMeetingChance)) {
                    start = System.nanoTime();
                    handleDanger(leftEntity, leftEntityIndex);
                    killingEntityTime += System.nanoTime() - start;
                } else {
                    start = System.nanoTime();
                    handleReproduction(leftEntity);
                    reproductionTime += System.nanoTime() - start;
                }
            }

            start = System.nanoTime();
            entities.removeScheduled();
            killingEntityTime += System.nanoTime() - start;

            start = System.nanoTime();
            entities.addScheduled();
            reproductionTime += System.nanoTime() - start;

            if (printSpentTime) {
                System.out.println("time spent on reproduction     for day " + currentDay + ": " + reproductionTime);
                System.out.println("time spent on getting entities for day " + currentDay + ": " + gettingEntitiesTime);
                System.out.println("time spent on killing entities for day " + currentDay + ": " + killingEntityTime);
                System.out.println("time spent on danger met check for day " + currentDay + ": " + checkingDangerMetTime);
                System.out.println("time spent on notify check     for day " + currentDay + ": " + checkingNotifyTime);
                System.out.println("time spent on total            for day " + currentDay + ": " + (System.nanoTime() - total));
                System.out.println("time spent on sum              for day " + currentDay + ": " + (reproductionTime + gettingEntitiesTime + killingEntityTime + checkingDangerMetTime + checkingNotifyTime));
            }

            // calculate day complete duration
            final long dayEndTime = System.currentTimeMillis();
            final float dayLastDuration = (dayEndTime - dayStartTime) / 1000.0f;
            this.dayCompleteDuration[currentDay] = dayLastDuration;

            // print current day data to keep track of the simulation
            if (printDailyData) {
                printDay(currentDay);
            }

            // if no entities left end the simulation
            if (entities.size() == 0) {
                endSimulation(startTime, days);
                return;
            }
        }

        endSimulation(startTime, days);
    }

    public void endSimulation (long startTime, int simulationEndDay) {
        final long endTime = System.currentTimeMillis();
        this.simulationEndDay = simulationEndDay;

        System.out.println();
        System.out.println("Simulation ended on day " + currentDay );
        System.out.println("Elapsed time is " + ((endTime - startTime) / 1000) + " seconds");
        System.out.println();
    }

    // handling events
    public void handleReproduction (Entity entity) {
        final int reproductionCount = getReproductionCount(entity);

        if (reproductionCount <= 0) return;

        final boolean isAltruist = isAltruist(entity);

        for (int i = 0; i < reproductionCount; i++) {
            if (isAltruist)
                summonAltruist(true);
            else summonEgoist(true);

        }
        entity.currentNutrients -= entity.nutrientsNecessaryForReproduction;
    }

    public void handleDanger (Entity entity, int entityIndex) {
        if (survivedDanger(entity)) return;

        killEntity(entity, entityIndex);
    }

    // creating or destroying entities
    public void summonAltruist (boolean schedule) {
        // create entity
        final Entity entity = entityPool.obtain();
        entity.survivalRate = 0.9f;
        entity.dangerNotifyChance = 1.0f;
        entity.reproductionCountMin = 1;
        entity.reproductionCountMax = 2;

        // update data
        this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] + 1;
        this.entities.add(entity, schedule);
    }

    public void summonEgoist(boolean schedule) {
        // create entity
        final Entity entity = entityPool.obtain();
        entity.survivalRate = 1.0f;
        entity.dangerNotifyChance = 0.0f;
        entity.reproductionCountMin = 1;
        entity.reproductionCountMax = 2;

        // update data
        this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[currentDay] + 1;
        this.entities.add(entity, schedule);
    }

    public void killEntity (Entity entity, int entityIndex) {
        // remove from entities
        this.entities.remove(entityIndex, true);
        this.entityPool.free(entity);

        // update data
        if (isAltruist(entity))
            this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] - 1;
        else this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[currentDay] - 1;
    }

    // checking events
    public boolean shouldNotify (Entity entity) {
        // TODO: 3/21/2023 somehow check if "opponent" is altruist or not and add perception parameter respectively
        return Utils.eventOccurred(entity.dangerNotifyChance);
    }

    public boolean metDanger (float dangerChance) {
        return Utils.eventOccurred(dangerChance);
    }

    public boolean survivedDanger (Entity entity) {
        return Utils.eventOccurred(entity.survivalRate);
    }

    // entity utils
    public int getReproductionCount (Entity entity) {
        if (entity.nutrientsNecessaryForReproduction > entity.currentNutrients) return 0;
        return Utils.getRandomNumberInclusive(entity.reproductionCountMin, entity.reproductionCountMax);
    }

    public boolean isAltruist (Entity entity) {
        return entity.dangerNotifyChance == 1.0f;
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
