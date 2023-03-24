package Simulation;

import com.sun.source.tree.SynchronizedTree;
import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

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
    private int initialCowardCount;
    private int[] altruistCountDailyData;
    private int[] cowardCountDailyData;

    // day counters
    private int days;
    private int currentDay;
    private int simulationEndDay;

    // current milestones
    // TODO: 3/22/2023 make entity variables change by next generation
    // TODO: 3/22/2023 entities have something like perception, altruist should deduce if "opponent" is altruist or coward
    // TODO: 3/22/2023 fully implement nutrients parameter for entities

    private void resetData () {
        this.currentDay = 0;
        this.entities = new ArrayList<>();

        // initialise data containers
        this.altruistCountDailyData = new int[days + 1];
        this.cowardCountDailyData = new int[days + 1];
    }

    public void setData (int initialAltruistCount, int initialCowardCount, int days) {
        this.initialAltruistCount = initialAltruistCount;
        this.initialCowardCount = initialCowardCount;
        this.days = days;

        resetData();
    }

    public void simulate (long seed) {
//        Utils.random.get().setSeed(seed);
        simulate();
    }

    public void simulate () {
        System.out.println("\nStarting simulation for " + days + " days\n");

        // initialise entities
        for (int i = 0; i < initialAltruistCount; i++) {
            summonAltruist();
        }

        for (int i = 0; i < initialCowardCount; i++) {
            summonCoward();
        }

        // simulate
        long startTime = System.currentTimeMillis();

        for (int currentDay = 1; currentDay <= days; currentDay++) {
            // update current date
            this.currentDay = currentDay;

            final int previousDay = currentDay - 1;
            this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[previousDay];
            this.cowardCountDailyData[currentDay] = this.cowardCountDailyData[previousDay];

            // copy entities array to modify further
            final ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);

            // shuffle entities to make couples
            Collections.shuffle(entitiesCopy, Utils.random.get());

            // create and start threads
            final int couples = (int) Math.ceil(entitiesCopy.size() / 2.0f);
            final int leftEnemyCount = entitiesCopy.size() % 2;
            final ExecutorService executorService = Executors.newFixedThreadPool(couples + leftEnemyCount);

            System.out.println("couples " + (couples + leftEnemyCount));

            // loop over entities with couples
            for (int coupleIndex = 0; coupleIndex < couples - leftEnemyCount; coupleIndex++) {
                final int firstEntityIndex = coupleIndex * 2;
                final int secondEntityIndex = coupleIndex * 2 + 1;

                executorService.execute(() -> {
                    // get a couple
                    final Entity firstEntity = entitiesCopy.get(firstEntityIndex);
                    final Entity secondEntity = entitiesCopy.get(secondEntityIndex);

                    // handle event
                    if (metDanger(enemyMeetingChance)) {
                        if (shouldNotify(firstEntity)) {
                            // second entity survived

                            // check if first survives
                            handleDanger(firstEntity);

                            // call it a day
                            return;
                        }

                        // first entity didn't notify
                        // TODO: 3/21/2023 figure out whether second entity should notify if first didn't
                        handleDanger(firstEntity);
                        handleDanger(secondEntity);

                        // call it a day
                        return;
                    }

                    // no danger met -> get food
                    firstEntity.currentNutrients++;
                    secondEntity.currentNutrients++;

                    // try to reproduce
                    handleReproduction(firstEntity);
                    handleReproduction(secondEntity);
                    System.out.print(" | ");
                });
            }

            // case if there is an enemy with no couple
            if (leftEnemyCount > 0) {
                executorService.submit(() -> {
                    final Entity entity = entitiesCopy.get(entitiesCopy.size() - 1);

                    if (metDanger(enemyMeetingChance)) {
                        handleDanger(entity);
                    } else {
                        handleReproduction(entity);
                    }
                    System.out.print(" | ");
                });
            }

            executorService.shutdown();

//            this.entities.sort(Utils.entityComparator);

            System.out.println();
            System.out.println("Day " + currentDay + " passed");

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
        System.out.println("Elapsed time is " + (endTime - startTime) + " mills");
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
            else summonCoward();

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
        entity.survivalRate = 0.1f;
        entity.dangerNotifyChance = 1.0f;
        entity.reproductionCountMin = 1;
        entity.reproductionCountMax = 1;

        // update data
        this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] + 1;
        this.entities.add(entity);
    }

    public void summonCoward () {
        // create entity
        final Entity entity = new Entity();
        entity.survivalRate = 0.2f;
        entity.dangerNotifyChance = 0.0f;
        entity.reproductionCountMin = 1;
        entity.reproductionCountMax = 1;

        // update data
        this.cowardCountDailyData[currentDay] = this.cowardCountDailyData[currentDay] + 1;
        this.entities.add(entity);
    }

    public void killEntity (Entity entity) {
        // remove from entities
        this.entities.remove(entity);

        // update data
        if (isAltruist(entity))
            this.altruistCountDailyData[currentDay] = this.altruistCountDailyData[currentDay] - 1;
        else this.cowardCountDailyData[currentDay] = this.cowardCountDailyData[currentDay] - 1;
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
        final float random = Utils.random.get().nextFloat();
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
            System.out.println("Current day " + currentDay);
            System.out.println("Current Altruists Count is " + this.altruistCountDailyData[currentDay]);
            System.out.println("current Cowards Count is " + this.cowardCountDailyData[currentDay]);
            System.out.println();
        }
    }
}
