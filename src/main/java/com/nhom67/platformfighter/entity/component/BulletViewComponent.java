package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
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
    private Point2D direction;

    public BulletViewComponent(WeaponType weaponType, BulletData bd, Point2D direction) {
        this.weaponType = weaponType;
        this.bd = bd;
        this.direction = direction;
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

            bulletView.setTranslateY(
                    -(bd.hitboxHeight() * visualScale
                            - bd.hitboxHeight()) / 2
            );

            double actualW =
                    img.getWidth()
                            * ((bd.hitboxHeight() * visualScale)
                            / img.getHeight());

            // Ảnh gốc được vẽ hướng sang phải
            bulletView.setTranslateX(
                    -(actualW - bd.hitboxWidth())
            );

            // Xoay theo hướng bay
            double angle =
                    Math.toDegrees(
                            Math.atan2(
                                    direction.getY(),
                                    direction.getX()
                            )
                    );

            bulletView.setRotate(angle);

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
