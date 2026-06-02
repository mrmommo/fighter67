package com.nhom67.platformfighter.entity;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EntityFactory implements com.almasb.fxgl.entity.EntityFactory {
    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC);
        return entityBuilder(data)
                .type(EntityType.PLATFORM)
                .bbox(new HitBox(BoundingShape.box(((Number) data.get("width")).doubleValue(), ((Number) data.get("height")).doubleValue())))
                .with(physics)
                .build();
    }

    @Spawns("KillZone")
    public Entity newKillZone(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.KILL_ZONE)
                .bbox(new HitBox(BoundingShape.box(((Number) data.get("width")).doubleValue(), ((Number) data.get("height")).doubleValue())))
                .with(new CollidableComponent(true)) // Sử dụng CollidableComponent thay cho PhysicsComponent để không lỗi Sensor handler
                .build();
    }

    @Spawns("player")
public Entity newPlayer(SpawnData data) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

    // this avoids player sticking to walls
    physics.setFixtureDef(new FixtureDef().friction(0.0f));

    Color color = data.hasKey("color") ? data.get("color") : Color.GREEN;

    return entityBuilder(data)
            .type(EntityType.PLAYER)
            .bbox(new HitBox(new Point2D(5, 5), BoundingShape.circle(12)))
            .bbox(new HitBox(new Point2D(10, 25), BoundingShape.box(10, 17)))
            .with(physics)
            .view(new Rectangle(50, 50, color))
            .with(new CollidableComponent(true))
            .with(new IrremovableComponent())
            .with(new PlayerComponent())
            .build();
}
}
