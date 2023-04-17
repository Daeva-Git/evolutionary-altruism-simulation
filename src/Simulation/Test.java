package Simulation;

import Simulation.utils.EntityArray;
import Simulation.utils.Utils;

public class Test {
    public static void main(String[] args) {
        Utils.random.setSeed(Utils.random.nextInt());

        testOnSeed(476876448);
//        checkCustomEntityArray();
    }

    public static void testOnSeed (long seed) {
        Utils.random.setSeed(seed);

        final Simulation simulation = new Simulation();
        final SimulationData data = new SimulationData();

        simulation.setData(data);
        simulation.printSpentTime = false;
        simulation.printDailyData = true;
        simulation.simulate();
    }

    private static void checkCustomEntityArray () {
        final EntityArray entities = new EntityArray(10);
        for (int i = 0; i < 10; i++) {
            final Entity entity = new Entity();
            entities.add(entity);
            if (i == 8) {
                entity.dangerNotifyChance = 1.0f;
            }
        }

//        entities.remove(2);
//        entities.remove(3);
//        entities.remove(4);
//        entities.remove(5);
//
//        entities.add(new Entity());
//        final Entity fifthEntity = entities.get(4);
//        System.out.println(fifthEntity.dangerNotifyChance);
//
//        System.out.println(entities.get(4));
//        entities.add(new Entity());
//        System.out.println(entities);
//
//        entities.add(new Entity());


        for (int i = entities.size() - 1; i > 2 ; i -= 2) {
            final int entityOne = i;
            final int entityTwo = i + 1;
            if (Utils.checkChance(0.5f)) {
                entities.remove(entityOne);
            } else {
                entities.add(new Entity());
            }

            if (Utils.checkChance(0.5f)) {
                entities.remove(entityTwo);
            } else {
                entities.add(new Entity());
            }

            System.out.println(entities.size());
        }
    }
}
