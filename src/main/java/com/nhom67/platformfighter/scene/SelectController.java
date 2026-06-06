package com.nhom67.platformfighter.scene;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import com.nhom67.platformfighter.app.FighterApp;
import com.nhom67.platformfighter.core.GameMode;
import com.nhom67.platformfighter.map.MapRegistry;
import com.nhom67.platformfighter.util.SoundManager;
import static com.almasb.fxgl.dsl.FXGL.*;
import java.util.ArrayList;
import java.util.List;

public class SelectController {

    @FXML private Text mapNameText;
    @FXML private ImageView mapPreview;
    @FXML private StackPane btnPrev;
    @FXML private StackPane btnNext;
    @FXML private Text modeNameText;
    @FXML private StackPane btnModePrev;
    @FXML private StackPane btnModeNext;
    @FXML private StackPane btnGo;

    private int currentMapIndex = 0;
    private final MapRegistry[] maps = MapRegistry.values();
    
    private int currentModeIndex = 0;
    private final GameMode[] modes = GameMode.values();

    private SelectScene scene;

    // Lưu shadow và accent polygon để có thể đổi màu theo map
    private final List<DropShadow> buttonShadows = new ArrayList<>();
    private final List<Polygon> accentPolygons = new ArrayList<>();

    public SelectController(SelectScene scene) {
        this.scene = scene;
    }

    @FXML
    public void initialize() {
        setupButtonHover(btnPrev);
        setupButtonHover(btnNext);
        setupButtonHover(btnModePrev);
        setupButtonHover(btnModeNext);
        setupButtonHover(btnGo);

        btnPrev.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            if (MenuScene.instance != null) {
                MenuScene.instance.slideFromSelectScene(-1);
            } else {
                currentMapIndex--;
                if (currentMapIndex < 0) currentMapIndex = maps.length - 1;
                updatePreview();
            }
        });

        btnNext.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            if (MenuScene.instance != null) {
                MenuScene.instance.slideFromSelectScene(1);
            } else {
                currentMapIndex++;
                if (currentMapIndex >= maps.length) currentMapIndex = 0;
                updatePreview();
            }
        });

        btnModePrev.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            cycleMode(-1);
        });

        btnModeNext.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            cycleMode(1);
        });

        btnGo.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            FighterApp.selectedMap = maps[currentMapIndex];
            FighterApp.gameMode = modes[currentModeIndex];
            MenuScene.isStartingGame = true;
            SoundManager.stopMusic();
            getSceneService().popSubScene();
            getGameController().startNewGame();
        });

        // Khởi tạo hiển thị và áp dụng theme của map đầu tiên
        MapRegistry initialMap = maps[currentMapIndex];
        mapNameText.setText(initialMap.getDisplayName());
        try {
            mapPreview.setImage(image(initialMap.getPreviewImagePath()));
        } catch (Exception ex) {}
        updateModeLabel();
        applyMapTheme(initialMap.getAccentColor());
    }

    private void setupButtonHover(StackPane btn) {
        DropShadow shadow = new DropShadow(15, Color.web("#a8e6cf", 0.8));
        buttonShadows.add(shadow);

        // Lưu polygon accent (child index 1) để đổi fill theo theme
        if (btn.getChildren().size() > 1 && btn.getChildren().get(1) instanceof Polygon) {
            accentPolygons.add((Polygon) btn.getChildren().get(1));
        }

        ScaleTransition hoverScale = new ScaleTransition(Duration.seconds(0.15), btn);
        
        btn.setOnMouseEntered(e -> {
            btn.setEffect(shadow);
            hoverScale.setToX(1.05);
            hoverScale.setToY(1.05);
            hoverScale.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            hoverScale.setToX(1.0);
            hoverScale.setToY(1.0);
            hoverScale.play();
        });
    }

    /**
     * Đổi màu accent của tất cả button và tiêu đề map theo theme map hiện tại.
     */
    public void applyMapTheme(String accentHex) {
        Color accent = Color.web(accentHex);
        Color accentGlow = Color.web(accentHex, 0.8);

        // Đổi màu tên map (mapNameText)
        mapNameText.setStyle("-fx-fill: " + toRgbaString(accentHex, 1.0) + ";"
                + "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);");

        // Cập nhật shadow hover
        for (DropShadow shadow : buttonShadows) {
            shadow.setColor(accentGlow);
        }

        // Cập nhật màu accent polygon
        for (Polygon polygon : accentPolygons) {
            polygon.setFill(accent);
        }

        // Cập nhật stroke của background polygon (child index 0)
        List<StackPane> buttons = List.of(btnPrev, btnNext, btnModePrev, btnModeNext, btnGo);
        for (StackPane btn : buttons) {
            if (btn.getChildren().get(0) instanceof Polygon bgPoly) {
                bgPoly.setStyle("-fx-stroke: " + toRgbaString(accentHex, 0.7) + ";");
            }
        }
    }

    /** Chuyển hex + alpha thành chuỗi rgba() hợp lệ cho JavaFX inline style */
    private String toRgbaString(String hex, double alpha) {
        Color c = Color.web(hex);
        return String.format("rgba(%d,%d,%d,%.2f)",
                (int)(c.getRed()   * 255),
                (int)(c.getGreen() * 255),
                (int)(c.getBlue()  * 255),
                alpha);
    }

    public void syncWithMenu(int index) {
        if (currentMapIndex != index) {
            currentMapIndex = index;
            updatePreview();
        }
    }

    private void cycleMode(int delta) {
        currentModeIndex += delta;
        if (currentModeIndex < 0) currentModeIndex = modes.length - 1;
        else if (currentModeIndex >= modes.length) currentModeIndex = 0;
        updateModeLabel();
    }

    private void updateModeLabel() {
        modeNameText.setText(modes[currentModeIndex].getDisplayName());
    }

    private void updatePreview() {
        MapRegistry currentMap = maps[currentMapIndex];
        mapNameText.setText(currentMap.getDisplayName());
        applyMapTheme(currentMap.getAccentColor());
        
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), mapPreview);
        fadeOut.setToValue(0.3);
        fadeOut.setOnFinished(e -> {
            try {
                mapPreview.setImage(image(currentMap.getPreviewImagePath()));
            } catch (Exception ex) {}
            
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), mapPreview);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
}
