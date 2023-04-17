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

    public Entity set (int index, Entity entity) {
        entities[index] = entity;
        return entity;
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
        // TODO: 13.04.23 change shuffle
        int size = size();
        for (int i = size; i > 1; i--) {
            swap(i - 1, rnd.nextInt(i));
        }
    }

    public void swap (int first, int second) {
        final int firstEntityIndex = getTrueIndex(first);
        final int secondEntityIndex = getTrueIndex(second);
        final Entity firstEntity = entities[firstEntityIndex];
        final Entity secondEntity = entities[secondEntityIndex];
        set(firstEntityIndex, secondEntity);
        set(secondEntityIndex, firstEntity);
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