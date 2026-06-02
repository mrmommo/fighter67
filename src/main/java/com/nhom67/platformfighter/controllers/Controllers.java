package com.nhom67.platformfighter.controllers;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.entity.Entity;
import com.nhom67.platformfighter.controllers.collisions.CollisionRegistry;
import com.nhom67.platformfighter.controllers.collisions.PlayerKillZoneCollision;
import com.nhom67.platformfighter.controllers.input.InputHandler;

public class Controllers {
    private Entity player1;
    private Entity player2;
    private final CollisionRegistry collisionRegistry = new CollisionRegistry();
    private final InputHandler inputHandler = new InputHandler();

    public void initInput() {
        inputHandler.initInput();
    }

    public void initPhysics() {
        collisionRegistry.addCollision(new PlayerKillZoneCollision());
        collisionRegistry.registerAll();
        getPhysicsWorld().setGravity(0, 1500);
    }

    public void setPlayers(Entity p1, Entity p2) {
        this.player1 = p1;
        this.player2 = p2;
        inputHandler.setPlayers(p1, p2);
    }
}
