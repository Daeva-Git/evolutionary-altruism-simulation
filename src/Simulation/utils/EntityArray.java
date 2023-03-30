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

    public void swap (int first, int second) {
        final Entity firstEntity = get(first);
        final Entity secondEntity = get(second);
        set(second, firstEntity);
        set(first, secondEntity);
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