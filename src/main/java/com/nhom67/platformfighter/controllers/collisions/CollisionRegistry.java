package com.nhom67.platformfighter.controllers.collisions;

import java.util.ArrayList;
import java.util.List;

public class CollisionRegistry {
    private final List<CollisionHandlerInterface> collisions = new ArrayList<>();

    public void addCollision(CollisionHandlerInterface collision) {
        collisions.add(collision);
    }

    public void registerAll() {
        collisions.forEach(CollisionHandlerInterface::register);
    }
}