package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

/**
 * Màn hình khởi động (Startup/Splash) — chỉ hiển thị icon.png tĩnh.
 * FXGL tự động chuyển sang Main Menu khi engine init xong.
 */
public class CustomStartupScene extends StartupScene {

    public CustomStartupScene(int appWidth, int appHeight) {
        super(appWidth, appHeight);

        // Nền đen
        Rectangle bg = new Rectangle(appWidth, appHeight, Color.BLACK);

        // Load icon.png
        ImageView logoView = new ImageView();
        try {
            Image logo = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream("/assets/textures/ui/icon1.png")));
            logoView.setImage(logo);
        } catch (Exception e) {
            System.err.println("[CustomStartupScene] Không tải được icon.png: " + e.getMessage());
        }

        logoView.setPreserveRatio(true);
        logoView.setFitHeight(appHeight * 0.1);
        logoView.setSmooth(true);

        StackPane root = new StackPane(bg, logoView);
        root.setPrefSize(appWidth, appHeight);
        StackPane.setAlignment(logoView, Pos.CENTER);

        getContentRoot().getChildren().add(root);
    }
}
