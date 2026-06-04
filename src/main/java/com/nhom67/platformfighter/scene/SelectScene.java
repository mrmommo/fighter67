package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import com.nhom67.platformfighter.app.FighterApp;
import com.nhom67.platformfighter.map.MapRegistry;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SelectScene extends SubScene {

    private int currentMapIndex = 0;
    private final MapRegistry[] maps = MapRegistry.values();
    
    private ImageView mapPreview;
    private Text mapNameText;

    public static SelectScene instance;

    public SelectScene() {
        instance = this;

        // Tạo một lớp nền mờ đè lên
        Rectangle bg = new Rectangle(1920, 1080, Color.color(0, 0, 0, 0.7));

        // UI hiển thị ảnh preview map
        mapPreview = new ImageView();
        mapPreview.setFitWidth(800);
        mapPreview.setFitHeight(450);
        mapPreview.setPreserveRatio(true);
        
        mapNameText = new Text("");
        mapNameText.setFont(javafx.scene.text.Font.font("Georgia", javafx.scene.text.FontWeight.BOLD, 55));
        mapNameText.setFill(Color.web("#a8e6cf"));
        javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow(10, Color.BLACK);
        mapNameText.setEffect(ds);

        javafx.scene.layout.StackPane btnPrev = createSelectButton("◄ PREV", 160, () -> {
            currentMapIndex--;
            if (currentMapIndex < 0) {
                currentMapIndex = maps.length - 1;
            }
            updatePreview();
            if (MenuScene.instance != null) {
                MenuScene.instance.forceBackground(currentMapIndex);
            }
        });

        javafx.scene.layout.StackPane btnNext = createSelectButton("NEXT ►", 160, () -> {
            currentMapIndex++;
            if (currentMapIndex >= maps.length) {
                currentMapIndex = 0;
            }
            updatePreview();
            if (MenuScene.instance != null) {
                MenuScene.instance.forceBackground(currentMapIndex);
            }
        });

        HBox carouselBox = new HBox(50, btnPrev, mapPreview, btnNext);
        carouselBox.setAlignment(Pos.CENTER);

        javafx.scene.layout.StackPane btnGo = createSelectButton("🚀 START BATTLE", 300, () -> {
            // Lưu map được chọn vào biến toàn cục
            FighterApp.selectedMap = maps[currentMapIndex];
            // Đánh dấu để MenuScene không tự động phát nhạc trở lại
            MenuScene.isStartingGame = true;
            // Tắt nhạc nền menu
            com.nhom67.platformfighter.util.SoundManager.stopMusic();
            // Khi nhấn Go, đóng SelectScene và gọi bắt đầu game
            getSceneService().popSubScene();
            getGameController().startNewGame();
        });

        VBox box = new VBox(30, mapNameText, carouselBox, btnGo);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(1920, 1080); // Đặt size bằng kích thước màn hình để căn giữa
        box.setTranslateX(250); // Dịch toàn bộ cụm chọn map sang bên phải 250px để chừa chỗ cho Menu chính

        getContentRoot().getChildren().addAll(bg, box);

        // Hiển thị map đầu tiên ngay lập tức (không fade)
        MapRegistry initialMap = maps[currentMapIndex];
        mapNameText.setText(initialMap.getDisplayName());
        try {
            mapPreview.setImage(image(initialMap.getBgPath()));
        } catch (Exception ex) {}

        // Đã gỡ Timeline tự động trong SelectScene để đồng bộ nhịp với MenuScene


        // Lắng nghe phím ESC để quay lại Menu
        getInput().addAction(new UserAction("Back") {
            @Override
            protected void onActionBegin() {
                instance = null;
                getSceneService().popSubScene();
            }
        }, KeyCode.ESCAPE);
    }

    public void syncWithMenu(int index) {
        if (currentMapIndex != index) {
            currentMapIndex = index;
            updatePreview();
        }
    }

    private void updatePreview() {
        MapRegistry currentMap = maps[currentMapIndex];
        mapNameText.setText(currentMap.getDisplayName());
        
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.3), mapPreview);
        fadeOut.setToValue(0.3);
        fadeOut.setOnFinished(e -> {
            try {
                Image img = image(currentMap.getBgPath());
                mapPreview.setImage(img);
            } catch (Exception ex) {}
            
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.3), mapPreview);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private javafx.scene.layout.StackPane createSelectButton(String text, double width, Runnable action) {
        javafx.scene.layout.StackPane btn = new javafx.scene.layout.StackPane();
        btn.setMaxSize(width, 50); // Ngăn StackPane bị kéo dãn toàn màn hình bởi VBox
        
        javafx.scene.shape.Polygon bg = new javafx.scene.shape.Polygon(
            20.0, 0.0,
            width, 0.0,
            width - 20.0, 50.0,
            0.0, 50.0
        );
        bg.setFill(Color.web("#1e3315", 0.8)); 
        bg.setStroke(Color.web("#a8e6cf", 0.6)); 
        javafx.scene.shape.Polygon accent = new javafx.scene.shape.Polygon(
            20.0, 0.0,
            26.0, 0.0,
            6.0, 50.0,
            0.0, 50.0
        );
        accent.setFill(Color.web("#a8e6cf"));
        javafx.scene.layout.StackPane.setAlignment(accent, Pos.CENTER_LEFT);

        Text textNode = new Text(text);
        textNode.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));
        textNode.setFill(Color.web("#e8f5d0"));

        btn.getChildren().addAll(bg, accent, textNode);
        
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow(15, Color.web("#a8e6cf", 0.8));
        
        javafx.animation.ScaleTransition hoverScale = new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(0.15), btn);
        
        btn.setOnMouseEntered(e -> {
            bg.setFill(Color.web("#2d4a22", 0.95));
            bg.setStroke(Color.web("#a8e6cf", 1.0));
            textNode.setFill(Color.WHITE);
            btn.setEffect(shadow);
            
            hoverScale.setToX(1.05);
            hoverScale.setToY(1.05);
            hoverScale.play();
        });

        btn.setOnMouseExited(e -> {
            bg.setFill(Color.web("#1e3315", 0.8));
            bg.setStroke(Color.web("#a8e6cf", 0.6));
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
