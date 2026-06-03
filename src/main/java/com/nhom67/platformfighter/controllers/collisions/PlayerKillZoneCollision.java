package com.nhom67.platformfighter.controllers.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import com.nhom67.platformfighter.entity.EntityType;
import com.nhom67.platformfighter.entity.component.PlayerComponent;

public class PlayerKillZoneCollision implements CollisionHandlerInterface {
    @Override
    public void register() {
        onCollisionBegin(EntityType.PLAYER, EntityType.KILL_ZONE, (player, killZone) -> {
            PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
            if (playerComponent != null) {
                // Tụt sạch máu và gọi loseLife() (mất mạng)
                playerComponent.takeDamage(playerComponent.getMaxHealth());
            }
        });
    }
}
