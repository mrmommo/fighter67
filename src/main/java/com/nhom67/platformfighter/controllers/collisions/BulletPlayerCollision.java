package com.nhom67.platformfighter.controllers.collisions;

import com.almasb.fxgl.entity.Entity;
import com.nhom67.platformfighter.entity.EntityType;
import com.nhom67.platformfighter.entity.component.BulletComponent;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import static com.almasb.fxgl.dsl.FXGL.*;

public class BulletPlayerCollision implements CollisionHandlerInterface {

    @Override
    public void register() {
        onCollisionBegin(EntityType.BULLET, EntityType.PLAYER, (bullet, player) -> {
            BulletComponent bulletComp = bullet.getComponent(BulletComponent.class);
            PlayerComponent playerComp = player.getComponent(PlayerComponent.class);

            if (bulletComp != null && playerComp != null) {
                // Đảm bảo không tự bắn trúng mình
                if (bulletComp.getOwner() != player) {
                    playerComp.takeDamage(bulletComp.getData().damage());
                    
                    double knockbackForce = bulletComp.getData().knockback();
                    if (bulletComp.isFacingRight()) {
                        playerComp.applyKnockback(knockbackForce);
                    } else {
                        playerComp.applyKnockback(-knockbackForce);
                    }

                    bullet.removeFromWorld();
                }
            }
        });
    }
}
