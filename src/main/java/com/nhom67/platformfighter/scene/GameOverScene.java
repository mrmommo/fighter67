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
        // Nền xanh đen sâu thẳm của rừng rậm
        Rectangle bg = new Rectangle(1920, 1080, Color.color(0.05, 0.15, 0.05, 0.85)); 

        Text titleText = new Text("BATTLE CONCLUDED");
        titleText.setFont(Font.font("Georgia", javafx.scene.text.FontWeight.BOLD, 80));
        titleText.setFill(Color.web("#a8e6cf"));
        titleText.setEffect(new javafx.scene.effect.DropShadow(20, Color.BLACK));

        Text winnerText = new Text("🏆 " + winner.getMessage());
        winnerText.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 45));
        winnerText.setFill(Color.web("#F1C40F")); // Giữ màu vàng vinh quang cho người chiến thắng

        javafx.scene.layout.StackPane btnOK = createForestButton("RETURN TO LOBBY", 350, () -> {
            // Đóng GameOverScene
            getSceneService().popSubScene();
            
            // Dừng trò chơi và quay lại Menu chính
            getGameController().gotoMainMenu();
            
            // Lập tức mở lại SelectScene để tạo thành loop
            getSceneService().pushSubScene(new SelectScene());
        });

        VBox box = new VBox(30, titleText, winnerText, btnOK);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.setPrefSize(1920, 1080); // Căn giữa chính xác giữa màn hình

        getContentRoot().getChildren().addAll(bg, box);
    }

    private javafx.scene.layout.StackPane createForestButton(String text, double width, Runnable action) {
        javafx.scene.layout.StackPane btn = new javafx.scene.layout.StackPane();
        btn.setMaxSize(width, 60); 
        
        javafx.scene.shape.Polygon bg = new javafx.scene.shape.Polygon(
            20.0, 0.0,
            width, 0.0,
            width - 20.0, 60.0,
            0.0, 60.0
        );
        bg.setFill(Color.web("#1e3315", 0.9)); 
        bg.setStroke(Color.web("#a8e6cf", 0.8)); 

        javafx.scene.shape.Polygon accent = new javafx.scene.shape.Polygon(
            20.0, 0.0,
            26.0, 0.0,
            6.0, 60.0,
            0.0, 60.0
        );
        accent.setFill(Color.web("#a8e6cf"));
        javafx.scene.layout.StackPane.setAlignment(accent, javafx.geometry.Pos.CENTER_LEFT);

        Text textNode = new Text(text);
        textNode.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));
        textNode.setFill(Color.web("#e8f5d0"));

        btn.getChildren().addAll(bg, accent, textNode);
        
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow(15, Color.web("#a8e6cf", 0.8));
        javafx.animation.ScaleTransition hoverScale = new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(0.15), btn);
        
        btn.setOnMouseEntered(e -> {
            bg.setFill(Color.web("#2d4a22", 1.0));
            textNode.setFill(Color.WHITE);
            btn.setEffect(shadow);
            hoverScale.setToX(1.05);
            hoverScale.setToY(1.05);
            hoverScale.play();
        });

        btn.setOnMouseExited(e -> {
            bg.setFill(Color.web("#1e3315", 0.9));
            textNode.setFill(Color.web("#e8f5d0"));
            btn.setEffect(null);
            hoverScale.setToX(1.0);
            hoverScale.setToY(1.0);
            hoverScale.play();
        });

        btn.setOnMousePressed(e -> bg.setFill(Color.web("#0a1207")));
        
        btn.setOnMouseClicked(e -> {
            com.nhom67.platformfighter.util.SoundManager.playClickSound();
            action.run();
        });

        return btn;
    }
}
