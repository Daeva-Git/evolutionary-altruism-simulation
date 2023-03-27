package Simulation;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Evolutionary Biological Altruism Simulation
 * created by Daeva
 */
public class Simulation {

    // world properties
    private final float enemyMeetingChance = 0.7f;
    private ArrayList<Entity> entities;

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

    // current milestones
    // TODO: 3/22/2023 make entity variables change by next generation
    // TODO: 3/22/2023 entities have something like perception, altruist should deduce if "opponent" is altruist or egoist
    // TODO: 3/22/2023 fully implement nutrients parameter for entities

    private void resetData () {
        this.currentDay = 0;
        this.entities = new ArrayList<>();

        // initialise data containers
        this.altruistCountDailyData = new int[days + 1];
        this.egoistCountDailyData = new int[days + 1];
        this.dayCompleteDuration = new float[days + 1];
    }

    public void setData (int initialAltruistCount, int initialegoistCount, int days) {
        this.initialAltruistCount = initialAltruistCount;
        this.initialEgoistCount = initialegoistCount;
        this.days = days;

        resetData();
    }

    public void simulate () {
        System.out.println("\nStarting simulation for " + days + " days\n");

        // initialise entities
        for (int i = 0; i < initialAltruistCount; i++) {
            summonAltruist();
        }

        for (int i = 0; i < initialEgoistCount; i++) {
            summonegoist();
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

            // copy entities array to modify further
            final ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);

            // shuffle entities to make couples
            Collections.shuffle(entitiesCopy, Utils.random);

            // loop over entities with couples
            for (int i = 0; i < entitiesCopy.size() - 1; i += 2) {
                // get a couple
                final Entity firstEntity = entitiesCopy.get(i);
                final Entity secondEntity = entitiesCopy.get(i + 1);

                // handle event
                if (metDanger(enemyMeetingChance)) {
                    if (shouldNotify(firstEntity)) {
                        // altruistic behavior (first entity yells endangers his life, second one runs)

                        // check if first survives
                        handleDanger(firstEntity);
                    } else {
                        // egoist behavior (first entity runs and saves his life, second one dies)
                        killEntity(secondEntity);
                    }

                } else {
                    // no danger met -> get food, return to repopulate
                    firstEntity.currentNutrients++;
                    secondEntity.currentNutrients++;

                    // check reproduction
                    handleReproduction(firstEntity);
                    handleReproduction(secondEntity);
                }

            }

            // calculate day complete duration
            final long dayEndTime = System.currentTimeMillis();
            final float dayLastDuration = (dayEndTime - dayStartTime) / 1000.0f;
            this.dayCompleteDuration[currentDay] = dayLastDuration;

            // print current day data to keep track of the simulation
            printDay(currentDay);

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
                summonAltruist();
            else summonegoist();

        }
        entity.currentNutrients -= entity.nutrientsNecessaryForReproduction;
    }

    public void handleDanger (Entity entity) {
        if (survivedDanger(entity)) return;

        killEntity(entity);
    }

    // creating or destroying entities
    public void summonAltruist () {
        // create entity
        final Entity entity = new Entity();
        entity.survivalRate = 0.9f;
        entity.dangerNotifyChance = 1.0f;
        entity.reproductionCountMin = 1;
        entity.reproductionCountMax = 2;

        // update data
        this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] + 1;
        this.entities.add(entity);
    }

    public void summonegoist () {
        // create entity
        final Entity entity = new Entity();
        entity.survivalRate = 1.0f;
        entity.dangerNotifyChance = 0.0f;
        entity.reproductionCountMin = 1;
        entity.reproductionCountMax = 2;

        // update data
        this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[currentDay] + 1;
        this.entities.add(entity);
    }

    public void killEntity (Entity entity) {
        // remove from entities
        this.entities.remove(entity);

        // update data
        if (isAltruist(entity))
            this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] - 1;
        else this.egoistCountDailyData[currentDay] = this.egoistCountDailyData[currentDay] - 1;
    }

    // checking events
    public boolean shouldNotify (Entity entity) {
        // TODO: 3/21/2023 somehow check if "opponent" is altruist or not and add perception parameter respectively
        return eventHappened(entity.dangerNotifyChance);
    }

    public boolean metDanger (float dangerChance) {
        return eventHappened(dangerChance);
    }

    public boolean survivedDanger (Entity entity) {
        return eventHappened(entity.survivalRate);
    }

    private boolean eventHappened (float chance) {
        final float random = Utils.random.nextFloat();
        return random <= chance;
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
