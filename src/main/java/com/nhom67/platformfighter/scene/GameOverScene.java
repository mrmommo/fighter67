package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameOverScene extends SubScene {

    public GameOverScene() {
        Rectangle bg = new Rectangle(1920, 1080, Color.color(0.8, 0, 0, 0.7)); // Nền đỏ mờ

        Button btnOK = getUIFactoryService().newButton("OK (Game Over)");
        
        btnOK.setOnAction(e -> {
            // Đóng GameOverScene
            getSceneService().popSubScene();
            
            // Dừng trò chơi và quay lại Menu chính
            getGameController().gotoMainMenu();
            
            // Lập tức mở lại SelectScene để tạo thành loop
            getSceneService().pushSubScene(new SelectScene());
        });

        VBox box = new VBox(btnOK);
        box.setTranslateX(1920 / 2.0 - 100);
        box.setTranslateY(1080 / 2.0);

        getContentRoot().getChildren().addAll(bg, box);
    }
}
