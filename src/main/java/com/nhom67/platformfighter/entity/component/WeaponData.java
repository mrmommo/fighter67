package com.nhom67.platformfighter.entity.component;

public record WeaponData(
        WeaponType type,
        BulletData bulletData,
        int maxAmmo, // số đạn tối đa trong băng
        double fireRate, // giây giữa 2 phát bắn
        boolean isDefault, // true = Pistol
        double spreadAngle // góc toả đạn
) {
    // Factory methods tiện lợi
    public static WeaponData pistol() {
        return new WeaponData(
                WeaponType.PISTOL,
                new BulletData(10, 600, 8, 8, 150, 1.25),
                12, 0.2, true, 10);
    }

    public static WeaponData shotgun() {
        return new WeaponData(
                WeaponType.SHOTGUN,
                new BulletData(40, 400, 50, 50, 1500, 0.12), // shotgun biến mất sau 0.1s
                7, 0.8, false, 1);
    }

    public static WeaponData rifle() {
        return new WeaponData(
                WeaponType.RIFLE,
                new BulletData(34, 2700, 8, 20, 700, 3.0),
                5, 2, false, 0);
    }
}
