package Simulation.utils.pool;

import java.util.ArrayDeque;

public abstract class Pool<T> {
    private final ArrayDeque<T> freeEntities = new ArrayDeque<>();
    private final ArrayDeque<T> scheduledToFree = new ArrayDeque<>();

    public T obtain () {
        if (freeEntities.isEmpty())
            return makeObject();
        return freeEntities.pop();
    }

    public void free (T object) {
        freeEntities.add(object);

        if (object instanceof Poolable) {
            ((Poolable) object).reset();
        }
    }

    public void free (T object, boolean schedule) {
        if (schedule) {
            scheduledToFree.add(object);
        } else {
            free(object);
        }
    }

    public void freeScheduled () {
        while (!scheduledToFree.isEmpty()) {
            free(scheduledToFree.pop());
        }
    }

    protected abstract T makeObject ();
}
