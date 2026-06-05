package com.nhom67.platformfighter.entity.component;

public record BulletData(
        int damage, // sát thương
        double speed, // tốc độ bay (pixels/s)
        double hitboxWidth, // chiều rộng hitbox
        double hitboxHeight, // chiều cao hitbox
        double knockback, // lực đẩy khi trúng
        double lifespan // thời gian tồn tại (giây)

) {
}