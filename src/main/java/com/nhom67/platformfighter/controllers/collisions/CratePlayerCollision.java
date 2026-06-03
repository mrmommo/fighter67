package com.nhom67.platformfighter.controllers.collisions;

import com.almasb.fxgl.entity.Entity;
import com.nhom67.platformfighter.entity.EntityType;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import com.nhom67.platformfighter.entity.component.WeaponData;
import static com.almasb.fxgl.dsl.FXGL.*;
import java.util.Random;

public class CratePlayerCollision implements CollisionHandlerInterface {

    private Random random = new Random();

    @Override
    public void register() {
        onCollisionBegin(EntityType.CRATE, EntityType.PLAYER, (crate, player) -> {
            PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
            if (playerComp != null) {
                // Random vũ khí (trừ default Pistol ra)
                WeaponData newWeapon = random.nextBoolean() ? WeaponData.shotgun() : WeaponData.rifle();
                playerComp.equipWeapon(newWeapon);

                // Xóa Crate sau khi ăn
                crate.removeFromWorld();
            }
        });
    }
}
