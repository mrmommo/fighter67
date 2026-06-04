package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BulletViewComponent extends Component {
    private final WeaponType weaponType;
    private final BulletData bd;
    private final boolean facingRight;

    public BulletViewComponent(WeaponType weaponType, BulletData bd, boolean facingRight) {
        this.weaponType = weaponType;
        this.bd = bd;
        this.facingRight = facingRight;
    }

    @Override
    public void onAdded() {
        String imagePath;
        if (weaponType == WeaponType.SHOTGUN) {
            imagePath = "/assets/bullet/shotgun_bullet.png";
        } else if (weaponType == WeaponType.RIFLE) {
            imagePath = "/assets/bullet/rifle_bullet.png";
        } else {
            imagePath = "/assets/bullet/normal_bullet.png";
        }

        try {
            Image img = new Image(getClass().getResource(imagePath).toExternalForm());
            ImageView bulletView = new ImageView(img);
            bulletView.setPreserveRatio(true);
            
            // Bạn có thể chỉnh số này (ví dụ 2.0, 3.0) để vệt sáng to ra mà KHÔNG làm to hitbox
            double visualScale = 2.5; 
            
            bulletView.setFitHeight(bd.hitboxHeight() * visualScale);
            
            // Căn giữa chiều cao của ảnh đạn so với hitbox
            bulletView.setTranslateY(-(bd.hitboxHeight() * visualScale - bd.hitboxHeight()) / 2);
            
            // Tính toán chiều dài thực tế của ảnh sau khi scale chiều cao
            double actualW = img.getWidth() * ((bd.hitboxHeight() * visualScale) / img.getHeight());
            
            if (facingRight) {
                // Đạn bay sang phải: phần đầu (bên phải ảnh) phải khớp với hitbox.
                bulletView.setTranslateX(-(actualW - bd.hitboxWidth()));
            } else {
                // Đạn bay sang trái: ảnh lật ngược (ScaleX = -1).
                bulletView.setScaleX(-1);
            }

            entity.getViewComponent().addChild(bulletView);
        } catch (Exception e) {
            System.out.println("Could not load bullet image: " + imagePath);
        }
    }
}
