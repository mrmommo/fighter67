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

                // Random loại vũ khí
                WeaponData newWeapon = switch (random.nextInt(3)) {
                    case 0 -> WeaponData.shotgun();
                    case 1 -> WeaponData.rifle();
                    default -> WeaponData.uzi();
                };
                playerComp.equipWeapon(newWeapon);

                play("loot.wav");

                // Lấy tên súng từ WeaponType để hiển thị
                String weaponName = newWeapon.type().name().charAt(0)
                        + newWeapon.type().name().substring(1).toLowerCase();

                // Hiển thị tên súng nổi lên tại vị trí giữa crate
                double crateX = crate.getX() + crate.getWidth() / 2 - 15;
                double crateY = crate.getY();
                com.nhom67.platformfighter.entity.component.CrateComponent.spawnPickupEffect(
                        crateX, crateY, weaponName);

                // Xóa Crate sau khi ăn
                crate.removeFromWorld();
            }
        });
    }
}
