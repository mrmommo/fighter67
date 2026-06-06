package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.app.scene.LoadingScene;
import com.nhom67.platformfighter.app.FighterApp;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

public class CustomLoadingScene extends LoadingScene {

    private final ImageView bg;

    public CustomLoadingScene() {
        bg = new ImageView();
        bg.setFitWidth(getAppWidth());
        bg.setFitHeight(getAppHeight());
        bg.setPreserveRatio(false);

        getContentRoot().getChildren().add(0, bg);
    }

    @Override
    public void onCreate() {
        // Lúc này selectedMap đã được set bởi người chơi
        bg.setImage(new Image(
                getClass().getResourceAsStream(
                        "/assets/textures/" + FighterApp.selectedMap.getPreviewImagePath())));
    }
}