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

    public SelectScene() {
        // Tạo một lớp nền mờ đè lên
        Rectangle bg = new Rectangle(1920, 1080, Color.color(0, 0, 0, 0.7));

        // UI hiển thị ảnh preview map
        mapPreview = new ImageView();
        mapPreview.setFitWidth(800);
        mapPreview.setFitHeight(450);
        mapPreview.setPreserveRatio(true);
        
        mapNameText = getUIFactoryService().newText("", Color.WHITE, 40);

        Button btnPrev = getUIFactoryService().newButton("< Previous");
        Button btnNext = getUIFactoryService().newButton("Next >");

        btnPrev.setOnAction(e -> {
            currentMapIndex--;
            if (currentMapIndex < 0) {
                currentMapIndex = maps.length - 1;
            }
            updatePreview();
        });

        btnNext.setOnAction(e -> {
            currentMapIndex++;
            if (currentMapIndex >= maps.length) {
                currentMapIndex = 0;
            }
            updatePreview();
        });

        HBox carouselBox = new HBox(50, btnPrev, mapPreview, btnNext);
        carouselBox.setAlignment(Pos.CENTER);

        Button btnGo = getUIFactoryService().newButton("Go (Start Game)");
        
        btnGo.setOnAction(e -> {
            // Lưu map được chọn vào biến toàn cục
            FighterApp.selectedMap = maps[currentMapIndex];
            // Khi nhấn Go, đóng SelectScene và gọi bắt đầu game
            getSceneService().popSubScene();
            getGameController().startNewGame();
        });

        VBox box = new VBox(30, mapNameText, carouselBox, btnGo);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(1920, 1080); // Đặt size bằng kích thước màn hình để căn giữa

        getContentRoot().getChildren().addAll(bg, box);

        // Hiển thị map đầu tiên
        updatePreview();

        // Lắng nghe phím ESC để quay lại Menu
        getInput().addAction(new UserAction("Back") {
            @Override
            protected void onActionBegin() {
                // Nhấn ESC -> đóng SelectScene
                getSceneService().popSubScene();
            }
        }, KeyCode.ESCAPE);
    }

    private void updatePreview() {
        MapRegistry currentMap = maps[currentMapIndex];
        mapNameText.setText(currentMap.getDisplayName());
        
        // Load ảnh preview
        try {
            Image img = image(currentMap.getBgPath());
            mapPreview.setImage(img);
        } catch (Exception e) {
            System.out.println("Could not load preview image: " + currentMap.getBgPath());
        }
    }
}
