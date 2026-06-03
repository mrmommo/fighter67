package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.nhom67.platformfighter.core.WinnerInfo;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameOverScene extends SubScene {

    public GameOverScene(WinnerInfo winner) {
        Rectangle bg = new Rectangle(1920, 1080, Color.color(0.8, 0, 0, 0.7)); // Nền đỏ mờ

        Text winnerText = new Text("🏆 " + winner.getMessage());
        winnerText.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 48));
        winnerText.setFill(Color.web("#F1C40F"));

        Button btnOK = getUIFactoryService().newButton("OK (Game Over)");
        
        btnOK.setOnAction(e -> {
            // Đóng GameOverScene
            getSceneService().popSubScene();
            
            // Dừng trò chơi và quay lại Menu chính
            getGameController().gotoMainMenu();
            
            // Lập tức mở lại SelectScene để tạo thành loop
            getSceneService().pushSubScene(new SelectScene());
        });

        VBox box = new VBox(20, winnerText, btnOK);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.setTranslateX(1920 / 2.0 - 150);
        box.setTranslateY(1080 / 2.0 - 50);

        getContentRoot().getChildren().addAll(bg, box);
    }
}
