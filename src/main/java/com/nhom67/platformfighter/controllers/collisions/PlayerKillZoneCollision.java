package com.nhom67.platformfighter.controllers.collisions;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import com.nhom67.platformfighter.entity.EntityType;

public class PlayerKillZoneCollision implements CollisionHandlerInterface {
    @Override
    public void register() {
    onCollisionBegin(EntityType.PLAYER, EntityType.KILL_ZONE, (player, KillZone) -> {
        player.removeFromWorld();
    });
}
}
