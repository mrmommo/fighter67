package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
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
        Text hitText = new Text("HIT!");

        // 1. Đổi sang font "Impact" dày và mạnh mẽ hơn Arial, kích thước lớn hơn một chút
        hitText.setFont(Font.font("Impact", FontWeight.BOLD, 15));

        // 2. Tạo màu Gradient đổ từ Cam xuống Vàng (nhìn như hiệu ứng rực cháy)
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.ORANGE),
                new Stop(1, Color.YELLOW)
        );
        hitText.setFill(gradient);

        // Đường viền dày màu đỏ đậm để làm nổi bật chữ
        hitText.setStroke(Color.DARKRED);
        hitText.setStrokeWidth(1);

        // 3. Thêm hiệu ứng bóng đổ (DropShadow) giúp chữ tách biệt hoàn toàn khỏi nền game
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.BLACK);
        dropShadow.setRadius(2.0);
        hitText.setEffect(dropShadow);

        // Góc nghiêng ngẫu nhiên nhẹ nhàng (từ -20 đến 20 độ) để tránh bị quá méo chữ
        double randomAngle = (Math.random() - 0.5) * 40;
        hitText.setRotate(randomAngle);

        // Tính toán tọa độ hiển thị trên màn hình
        Entity hitEntity = FXGL.entityBuilder()
                .at(worldX, worldY) // Đặt trực tiếp tọa độ thế giới (world coords) ở đây
                .view(hitText)      // Dùng chữ HIT làm hình ảnh hiển thị
                .buildAndAttach();  // Tạo và gắn vào map

        // Căn giữa tương đối dựa trên font size mới



        // --- THAY ĐỔI CẤU HÌNH HOẠT ẢNH Ở ĐÂY ---

        // Chia thời gian: Phóng to thật nhanh (0.15 giây) rồi mờ dần (0.6 giây)
        Duration scaleDuration = Duration.seconds(0.15);
        Duration fadeDuration = Duration.seconds(0.6);

        // 1. Hoạt ảnh PHÌNH TO RA tại chỗ (chạy trước)
        ScaleTransition scale = new ScaleTransition(scaleDuration, hitText);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.8);
        scale.setToY(1.8);

        // 2. Hoạt ảnh mờ dần (chạy sau)
        FadeTransition fadeOut = new FadeTransition(fadeDuration, hitText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> FXGL.getGameScene().removeUINode(hitText));

        // Sử dụng SequentialTransition để ép buộc scale xong xuôi mới tới fadeOut
        javafx.animation.SequentialTransition sequential = new javafx.animation.SequentialTransition(scale, fadeOut);
        sequential.play();
    }
}
