package com.nhom67.platformfighter.scene;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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

        // Initialize display
        MapRegistry initialMap = maps[currentMapIndex];
        mapNameText.setText(initialMap.getDisplayName());
        try {
            mapPreview.setImage(image(initialMap.getPreviewImagePath()));
        } catch (Exception ex) {}
        updateModeLabel();
    }

    private void setupButtonHover(StackPane btn) {
        DropShadow shadow = new DropShadow(15, Color.web("#a8e6cf", 0.8));
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
