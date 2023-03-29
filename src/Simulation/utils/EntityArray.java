package Simulation.utils;

import Simulation.Entity;

import java.util.*;

public class EntityArray {
    private Entity[] entities;
    private final ArrayDeque<Integer> removedIndices;
    private int latestIndex;
    private static final int DEFAULT_CAPACITY = 16;
    private int currentCapacity;

    public EntityArray() {
        this(DEFAULT_CAPACITY);
    }

    public EntityArray(int initialCapacity) {
        currentCapacity = initialCapacity;

        entities = new Entity[currentCapacity];
        removedIndices = new ArrayDeque<>();
    }

    public void add (Entity entity) {
        if (removedIndices.isEmpty()) {
            entities[latestIndex] = entity;
            latestIndex++;
            if (latestIndex == currentCapacity) {
                grow();
            }
        } else {
            final Integer pop = removedIndices.pop();
            entities[pop] = entity;
        }
    }

    public void remove (int index) {
        removedIndices.add(index);
        entities[index] = null;
    }

    public Entity set (int index, Entity entity) {
        entities[index] = entity;
        return entity;
    }

    public Entity get (int index) {
        if (index < 0 || index > size()) {
            final long seed = Utils.getSeed();
            System.out.println(seed);
            throw new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds for length " + size());
        }

        int trueIndex = getRemovedCount(index) + index;
        while (removedIndices.contains(trueIndex)) {
            trueIndex++;
        }
        return entities[trueIndex];
    }

    public int getRemovedCount (int tillIndex) {
        int count = 0;
        for (Integer removedIndex : removedIndices) {
            if (removedIndex < tillIndex) count++;
        }
        return count;
    }

    public int size() {
        return latestIndex - removedIndices.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void shuffle(Random rnd) {
        int size = size();
        for (int i = size; i > 1; i--){
            swap(i - 1, rnd.nextInt(i));
        }
    }

    public void swap (int i, int j) {
        set(i - 1, set(j, get(i - 1)));
    }

    private Object[] grow() {
        this.currentCapacity *= 2;
        return grow(currentCapacity);
    }

    private Object[] grow (int newCapacity) {
        return entities = Arrays.copyOf(entities, newCapacity);
    }
}