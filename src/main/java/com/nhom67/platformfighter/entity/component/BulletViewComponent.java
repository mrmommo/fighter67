package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

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

            // Bạn có thể chỉnh số này (ví dụ 2.0, 3.0) để vệt sáng to ra mà KHÔNG làm to
            // hitbox
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

    public static void spawnHitEffect(double worldX, double worldY) {
        Text hitText = new Text("HIT");
        hitText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        hitText.setFill(Color.YELLOW);
        hitText.setStroke(Color.ORANGE);
        hitText.setStrokeWidth(1.5);

        var viewport = com.almasb.fxgl.dsl.FXGL.getGameScene().getViewport();

        // ✅ Tính screen coords, có xét đến zoom của viewport
        double zoom = viewport.getZoom();
        double screenX = (worldX - viewport.getX()) * zoom;
        double screenY = (worldY - viewport.getY()) * zoom;

        // ✅ Căn giữa text (ước lượng; Text chưa layout nên dùng offset cố định)
        hitText.setTranslateX(screenX - 16); // ~half width của "HIT" ở 18pt
        hitText.setTranslateY(screenY);

        com.almasb.fxgl.dsl.FXGL.getGameScene().addUINode(hitText);

        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(0.5), hitText);
        moveUp.setByY(-40);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), hitText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> com.almasb.fxgl.dsl.FXGL.getGameScene().removeUINode(hitText));

        moveUp.play();
        fadeOut.play();
    }
}
