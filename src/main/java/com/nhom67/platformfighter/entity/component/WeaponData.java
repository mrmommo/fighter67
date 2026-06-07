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
                new BulletData(10, 700, 8, 12, 600, 5),
                7, 0.3, true, 2);
    }

    public static WeaponData shotgun() {
        return new WeaponData(
                WeaponType.SHOTGUN,
                new BulletData(40, 400, 50, 50, 1500, 0.12), // shotgun biến mất sau 0.1s
                6, 1, false, 1);
    }

    public static WeaponData rifle() {
        return new WeaponData(
                WeaponType.RIFLE,
                new BulletData(40, 1500, 8, 20, 1200, 5),
                5, 2, false, 0);
    }

    public static WeaponData uzi() {
        return new WeaponData(
                WeaponType.UZI,
                new BulletData(10, 800, 8, 12, 600, 5),
                40, 0.05, false, 20);
    }
    public static WeaponData ak() {
        return new WeaponData(
                WeaponType.AK,
                new BulletData(12, 800, 8, 12, 600, 5),
                30, 0.1, false, 8);
    }
}
