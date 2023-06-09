package Simulation;

import Simulation.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main (String[] args) throws IOException {
        final Simulation simulation = new Simulation();
        final SimulationConfig config = new SimulationConfig();

        final long startTime = System.currentTimeMillis();

        final int[][] altruistCountDailyData = new int[config.iterations][];
        final int[][] egoistCountDailyData = new int[config.iterations][];

        // init file writer
        final File file = new File("data");
        final FileWriter writer = new FileWriter(file);
        writer.close(); // clear

        // simulate for some iterations
        for (int i = 0; i < config.iterations; i++) {
            Utils.random.setSeed(Utils.random.nextInt());

            System.out.println("Iteration: " + i);

            simulation.setConfig(config);
            simulation.printSpentTime = config.printSpentTime;
            simulation.printDailyData = config.printDailyData;
            simulation.simulate();

            altruistCountDailyData[i] = simulation.altruistCountDailyData.clone();
            egoistCountDailyData[i] = simulation.egoistCountDailyData.clone();

            System.out.println();

            // write iteration data
            final FileWriter iterationWriter = new FileWriter(file, true);
            iterationWriter.write("altruists for iteration: " + i + " ");
            iterationWriter.write(Arrays.toString(altruistCountDailyData[i]));
            iterationWriter.write("\negoists for iteration: " + i + " ");
            iterationWriter.write(Arrays.toString(egoistCountDailyData[i]));
            iterationWriter.write("\n\n");
            iterationWriter.close();
        }
        System.out.println("Time taken for " + config.iterations + " iterations: " + (System.currentTimeMillis() - startTime) / 1000 + "s");

        // write average ratio
        final FileWriter averageWriter = new FileWriter(file, true);
        averageWriter.write("\nAverage Ratio\n");
        final float[] averageRatio = new float[config.days];
        for (int day = 0; day < config.days; day++) {
            for (int i = 0; i < config.iterations; i++) {
                final int altruistsCount = altruistCountDailyData[i][day];
                final int egoistCount = egoistCountDailyData[i][day];
                final int entityCount = altruistsCount + egoistCount;
                final float altruistPercent;
                // if no entity exists percentage is 0.5
                if (entityCount == 0) {
                    altruistPercent = 0.5f;
                } else {
                    altruistPercent = altruistsCount * 1.0f / entityCount;
                }
                averageRatio[day] += altruistPercent;
            }
            averageRatio[day] /= config.iterations;
            averageRatio[day] = Utils.floor(averageRatio[day], 3);
        }
        averageWriter.write(Arrays.toString(averageRatio));
        averageWriter.close();
    }
}
