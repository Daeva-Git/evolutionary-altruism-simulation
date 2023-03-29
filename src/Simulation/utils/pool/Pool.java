package Simulation.utils.pool;

import java.util.ArrayDeque;

public abstract class Pool<T> {
    private final ArrayDeque<T> freeEntities = new ArrayDeque<>();

    public T obtain () {
        if (freeEntities.isEmpty()) freeEntities.add(makeObject());
        return freeEntities.pop();
    }

    public void free (T object) {
        freeEntities.add(object);
        if (object instanceof Poolable) ((Poolable) object).reset();
    }

    protected abstract T makeObject ();
}
