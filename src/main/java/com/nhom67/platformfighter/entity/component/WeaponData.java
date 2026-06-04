package com.nhom67.platformfighter.entity.component;

public record WeaponData(
        WeaponType type,
        BulletData bulletData,
        int maxAmmo, // số đạn tối đa trong băng
        double fireRate, // giây giữa 2 phát bắn
        boolean isDefault // true = Pistol
) {
    // Factory methods tiện lợi
    public static WeaponData pistol() {
        return new WeaponData(
                WeaponType.PISTOL,
                new BulletData(10, 600, 8, 8, 150, 3.0),
                12, 0.3, true);
    }

    public static WeaponData shotgun() {
        return new WeaponData(
                WeaponType.SHOTGUN,
                new BulletData(25, 400, 50, 50, 1500, 0.12), // shotgun biến mất sau 0.1s
                6, 0.8, false);
    }

    public static WeaponData rifle() {
        return new WeaponData(
                WeaponType.RIFLE,
                new BulletData(15, 900, 8, 12, 100, 3.0),
                20, 0.1, false);
    }
}
