package Simulation.utils.pool;

import Simulation.Entity;

public class EntityPool extends Pool<Entity> {
    @Override
    protected Entity makeObject() {
        return new Entity();
    }
}
