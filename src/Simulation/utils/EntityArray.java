package Simulation.utils;

import Simulation.Entity;

import java.util.*;

public class EntityArray {
    private Entity[] entities;

    // indices
    private final ArrayDeque<Integer> removedIndices;
    private int latestIndex;

    // capacity
    private static final int DEFAULT_CAPACITY = 16;
    private int currentCapacity;

    // scheduling operations
    final ArrayDeque<Entity> entitiesToAdd = new ArrayDeque<>();
    final ArrayDeque<Integer> entitiesToRemove = new ArrayDeque<>();

    public EntityArray() {
        this(DEFAULT_CAPACITY);
    }

    public EntityArray(int initialCapacity) {
        currentCapacity = initialCapacity;

        entities = new Entity[currentCapacity];
        removedIndices = new ArrayDeque<>();
    }

    public void set (int index, Entity entity) {
        entities[index] = entity;
    }

    // returns entity at given index
    public Entity getEntityAt (int index) {
        return entities[index];
    }

    // returns nth entity
    public Entity get (int index) {
        if (index < 0 || index > size()) {
            final long seed = Utils.getSeed();
            System.out.println(seed);
            throw new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds for length " + size());
        }

        return entities[getTrueIndex(index)];
    }

    public int getTrueIndex (int index) {
        int skipNotNullCount = getRemovedCount(index);
        while (skipNotNullCount != 0 || entities[index] == null) {
            if (entities[index] != null)
                skipNotNullCount--;
            index++;
        }
        return index;
    }

    public int getRemovedCount (int tillIndex) {
        int count = 0;
        for (int removedIndex : removedIndices) {
            if (removedIndex < tillIndex) count++;
        }
        return count;
    }

    public int size() {
        return latestIndex - removedIndices.size();
    }

    public int getLastIndex () {
        return this.latestIndex;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void shuffle(Random rnd) {
        int size = size();
        for (int i = size - 1; i > 0; i--) {
            // find first
            while (entities[i] == null) {
                i--;
                // last entity reached, no swaps left
                if (i == 0) return;
            }

            // find second
            int second = rnd.nextInt(i);
            while (entities[second] == null) {
                second--;
                // last entity reached, no swaps left
                if (second == -1) return;
            }

            // swap
            swap(i, second);
        }
    }

    public void swap (int first, int second) {
        // shuffle entities to make couples
        final Entity firstEntity = entities[first];
        final Entity secondEntity = entities[second];

        if (firstEntity == null || secondEntity == null) return;

        set(first, secondEntity);
        set(second, firstEntity);
    }

    private Object[] grow() {
        this.currentCapacity *= 2;
        return grow(currentCapacity);
    }

    private Object[] grow (int newCapacity) {
        return entities = Arrays.copyOf(entities, newCapacity);
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

    // removes nth element from the array
    public void remove (int index) {
        removedIndices.add(index);
        entities[index] = null;
    }

    public void add (Entity entity, boolean schedule) {
        if (schedule) scheduleAddition(entity);
        else add(entity);
    }

    public void remove (int index, boolean schedule) {
        if (schedule) scheduleRemoval(index);
        else remove(index);
    }

    private void scheduleAddition (Entity entity) {
        entitiesToAdd.add(entity);
    }

    private void scheduleRemoval (int index) {
        entitiesToRemove.add(index);
    }

    public void addScheduled () {
        while (!entitiesToAdd.isEmpty()) {
            add(entitiesToAdd.pop());
        }
    }

    public void removeScheduled () {
        while (!entitiesToRemove.isEmpty()) {
            remove(entitiesToRemove.pop());
        }
    }
}