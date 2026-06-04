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
import com.nhom67.platformfighter.entity.component.BulletData;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import com.nhom67.platformfighter.entity.component.AnimationComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EntityFactory implements com.almasb.fxgl.entity.EntityFactory {
        public static final short CATEGORY_GROUND = 0x0001; // Sàn cứng
        public static final short CATEGORY_ONE_WAY = 0x0002; // Sàn mềm
        public static final short CATEGORY_PLAYER = 0x0004; // Người chơi
        public static final short CATEGORY_CRATE = 0x0008; // Hòm vũ khí

        @Spawns("platform")
        public Entity newPlatform(SpawnData data) {
                PhysicsComponent physics = new PhysicsComponent();
                physics.setBodyType(BodyType.STATIC);

                // Gán thể loại là SÀN CỨNG
                FixtureDef fd = new FixtureDef();
                fd.getFilter().categoryBits = CATEGORY_GROUND;
                physics.setFixtureDef(fd);

                return entityBuilder(data)
                                .type(EntityType.PLATFORM)
                                .bbox(new HitBox(BoundingShape.box(((Number) data.get("width")).doubleValue(),
                                                ((Number) data.get("height")).doubleValue())))
                                .with(physics)
                                .build();
        }

        @Spawns("oneWayPlatform") // Dùng cái này cho các bục nhảy trên không
        public Entity newOneWayPlatform(SpawnData data) {
                PhysicsComponent physics = new PhysicsComponent();
                physics.setBodyType(BodyType.STATIC);

                // Gán thể loại là SÀN MỀM
                FixtureDef fd = new FixtureDef();
                fd.getFilter().categoryBits = CATEGORY_ONE_WAY;
                physics.setFixtureDef(fd);

                return entityBuilder(data)
                                .type(EntityType.ONE_WAY_PLATFORM) // Yêu cầu thêm vào enum EntityType
                                .bbox(new HitBox(BoundingShape.box(((Number) data.get("width")).doubleValue(),
                                                ((Number) data.get("height")).doubleValue())))
                                .with(physics)
                                .build();
        }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(29, 58), BoundingShape.box(6, 8)));

                // Thiết lập bộ lọc mặc định cho Player
                FixtureDef fd = new FixtureDef();
                fd.setFriction(0.0f);
                fd.getFilter().categoryBits = CATEGORY_PLAYER;
                // Mặc định: Chạm vào CẢ Sàn cứng VÀ Sàn mềm
                fd.getFilter().maskBits = CATEGORY_GROUND | CATEGORY_ONE_WAY;
                physics.setFixtureDef(fd);

        Color color = data.hasKey("color") ? data.get("color") : Color.GREEN;
        String prefix = color.equals(Color.GREEN) ? "p1" : "p2";

        return entityBuilder(data)
                .type(EntityType.PLAYER)
                .bbox(new HitBox(new Point2D(24, 11), BoundingShape.circle(8)))
                .bbox(new HitBox(new Point2D(24, 27), BoundingShape.box(16, 32)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new PlayerComponent())
                .with(new AnimationComponent(prefix))
                .build();
    }

        @Spawns("KillZone")
        public Entity newKillZone(SpawnData data) {
                return entityBuilder(data)
                                .type(EntityType.KILL_ZONE)
                                .bbox(new HitBox(BoundingShape.box(((Number) data.get("width")).doubleValue(),
                                                ((Number) data.get("height")).doubleValue())))
                                .with(new CollidableComponent(true)) // Sử dụng CollidableComponent thay cho
                                                                     // PhysicsComponent để không
                                                                     // lỗi Sensor handler
                                .build();
        }

        @Spawns("bullet")
        public Entity newBullet(SpawnData data) {
                BulletData bd = data.get("bulletData");
                boolean facingRight = data.get("facingRight");
                Entity owner = data.get("owner");

                return entityBuilder(data)
                                .type(EntityType.BULLET)
                                .bbox(new HitBox(BoundingShape.box(bd.hitboxWidth(), bd.hitboxHeight())))
                                .with(new CollidableComponent(true))
                                .with(new com.nhom67.platformfighter.entity.component.BulletComponent(bd, owner,
                                                facingRight))
                                .view(new Rectangle(bd.hitboxWidth(), bd.hitboxHeight(), Color.YELLOW))
                                .build();
        }

        @Spawns("crate")
        public Entity newCrate(SpawnData data) {
                PhysicsComponent physics = new PhysicsComponent();
                physics.setBodyType(BodyType.DYNAMIC);

                FixtureDef fd = new FixtureDef();
                fd.getFilter().categoryBits = CATEGORY_CRATE;
                fd.getFilter().maskBits = CATEGORY_GROUND | CATEGORY_ONE_WAY | CATEGORY_PLAYER;
                physics.setFixtureDef(fd);

                return entityBuilder(data)
                                .type(EntityType.CRATE)
                                .bbox(new HitBox(BoundingShape.box(30, 30)))
                                .with(physics)
                                .with(new CollidableComponent(true))
                                .with(new com.nhom67.platformfighter.entity.component.CrateComponent())
                                .view(new Rectangle(30, 30, Color.BROWN))
                                .build();
        }
}
