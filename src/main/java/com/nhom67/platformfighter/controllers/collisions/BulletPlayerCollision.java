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
                    int damage = bulletComp.getData().damage();
                    boolean willDie = playerComp.getCurrentHealth() <= damage;

                    playerComp.takeDamage(damage);

                    // Chỉ đẩy lùi nếu viên đạn không giết chết người chơi (để tránh lỗi bị đẩy lùi
                    // sau khi hồi sinh)
                    if (!willDie && !playerComp.isDead()) {
                        double knockbackForce = bulletComp.getData().knockback();
                        double dirForce = bulletComp.isFacingRight() ? knockbackForce : -knockbackForce;

                        // Nếu người chơi đang di chuyển (speedX > 5)
                        if (Math.abs(playerComp.getCurrentSpeedX()) > 5) {
                            playerComp.applyStun(0.05); // Đứng yên trong 0.05s
                            playerComp.setCurrentSpeedX(0); // Mất quán tính hiện tại

                            // Đợi 0.05s (stun xong) rồi mới áp dụng 90% knockback
                            com.almasb.fxgl.dsl.FXGL.getGameTimer().runOnceAfter(() -> {
                                if (player.isActive() && !playerComp.isDead()) {
                                    playerComp.applyKnockback(dirForce * 0.9);
                                }
                            }, javafx.util.Duration.seconds(0.05));
                        } else {
                            // Nếu đang đứng yên, văng đi lập tức
                            playerComp.applyKnockback(dirForce);
                        }
                    }

                    // Hiển thị chữ "HIT" tại vị trí va chạm
                    com.nhom67.platformfighter.entity.component.BulletViewComponent.spawnHitEffect(
                            bullet.getX(), bullet.getY()
                    );

                    bullet.removeFromWorld();
                }
            }
        });
    }
}
