package com.nhom67.platformfighter.scene;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import javafx.scene.shape.Polygon;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import com.nhom67.platformfighter.util.SoundManager;
import static com.almasb.fxgl.dsl.FXGL.*;
import java.util.ArrayList;
import java.util.List;

public class MenuController {

    @FXML private Text titleText;
    @FXML private VBox mainMenuBox;
    @FXML private Group btnPlay;
    @FXML private Group btnSettings;
    @FXML private Group btnHowToPlay;
    @FXML private Group btnExit;
    
    @FXML private StackPane overlayContainer;
    @FXML private VBox settingsBox;
    @FXML private VBox howToPlayBox;
    @FXML private Slider musicSlider;
    @FXML private Slider sfxSlider;

    private MenuScene scene;

    // Lưu shadow và accent polygon để có thể đổi màu theo map
    private final List<DropShadow> buttonShadows = new ArrayList<>();
    private final List<Polygon> accentPolygons = new ArrayList<>();

    public MenuController(MenuScene scene) {
        this.scene = scene;
    }

    @FXML
    public void initialize() {
        setupButtonHover(btnPlay, () -> {
            getSceneService().pushSubScene(new SelectScene());
        });
        
        setupButtonHover(btnSettings, () -> {
            showOverlay(settingsBox);
        });
        
        setupButtonHover(btnHowToPlay, () -> {
            showOverlay(howToPlayBox);
        });
        
        setupButtonHover(btnExit, () -> {
            getGameController().exit();
        });

        // Breathing title animation
        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), titleText);
        tt.setByY(-15);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.play();

        // Slide menu in animation
        TranslateTransition boxSlide = new TranslateTransition(Duration.seconds(1.2), mainMenuBox);
        boxSlide.setToX(0);
        boxSlide.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        
        FadeTransition boxFade = new FadeTransition(Duration.seconds(1.2), mainMenuBox);
        boxFade.setToValue(1.0);
        
        boxSlide.play();
        boxFade.play();

        // Overlay Escape to close
        overlayContainer.setOnMouseClicked(e -> hideOverlay());
        overlayContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE && overlayContainer.isVisible()) {
                        hideOverlay();
                    } else {
                        scene.handleGlobalKeyPress(e);
                    }
                });
            }
        });

        // Sliders
        musicSlider.setValue(SoundManager.getMusicVolume());
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setMusicVolume(newVal.doubleValue());
        });

        sfxSlider.setValue(SoundManager.getSfxVolume());
        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setSfxVolume(newVal.doubleValue());
        });
    }

    private void setupButtonHover(Group btn, Runnable action) {
        // Mỗi button tạo shadow riêng — lưu lại để applyMapTheme() có thể đổi màu
        DropShadow shadow = new DropShadow(15, Color.web("#a8e6cf", 0.8));
        buttonShadows.add(shadow);

        // Lưu polygon accent (child index 1) để đổi fill theo theme
        if (btn.getChildren().size() > 1 && btn.getChildren().get(1) instanceof Polygon) {
            accentPolygons.add((Polygon) btn.getChildren().get(1));
        }

        TranslateTransition hoverMove = new TranslateTransition(Duration.seconds(0.15), btn);
        
        btn.setOnMouseEntered(e -> {
            btn.setEffect(shadow);
            hoverScale(btn, 1.05);
            hoverMove.setToX(20);
            hoverMove.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            hoverScale(btn, 1.0);
            hoverMove.setToX(0);
            hoverMove.play();
        });
        
        btn.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            action.run();
        });
    }

    /**
     * Thay đổi màu accent của tất cả button theo theme của map đang hiển thị.
     * Được gọi từ MenuScene mỗi khi slide background hoàn thành.
     *
     * @param accentHex màu hex của map (ví dụ: "#ff7675" cho Volcano Valley)
     */
    public void applyMapTheme(String accentHex) {
        Color accent = Color.web(accentHex);
        Color accentGlow = Color.web(accentHex, 0.8);

        // Đổi màu chữ tiêu đề theo accent của map
        titleText.setStyle("-fx-fill: " + toRgbaString(accentHex, 1.0) + ";"
                + "-fx-effect: dropshadow(three-pass-box, black, 20, 0, 0, 0);");

        // Cập nhật màu shadow hover
        for (DropShadow shadow : buttonShadows) {
            shadow.setColor(accentGlow);
        }

        // Cập nhật màu fill của accent polygon (thanh dọc bên trái button)
        for (Polygon polygon : accentPolygons) {
            polygon.setFill(accent);
        }

        // Cập nhật màu stroke của background polygon (child index 0)
        List<Group> buttons = List.of(btnPlay, btnSettings, btnHowToPlay, btnExit);
        for (Group btn : buttons) {
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

    private void hoverScale(Group btn, double scale) {
        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(Duration.seconds(0.15), btn);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    private void showOverlay(VBox contentBox) {
        overlayContainer.setVisible(true);
        overlayContainer.setOpacity(0);
        FadeTransition ftOverlay = new FadeTransition(Duration.seconds(0.3), overlayContainer);
        ftOverlay.setToValue(1.0);
        ftOverlay.play();

        contentBox.setVisible(true);
        contentBox.setOpacity(0);
        contentBox.setTranslateY(50);
        
        FadeTransition ftBox = new FadeTransition(Duration.seconds(0.4), contentBox);
        ftBox.setToValue(1.0);
        
        TranslateTransition ttBox = new TranslateTransition(Duration.seconds(0.4), contentBox);
        ttBox.setToY(0);
        ttBox.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        
        ftBox.play();
        ttBox.play();
    }

    private void hideOverlay() {
        FadeTransition ftOverlay = new FadeTransition(Duration.seconds(0.3), overlayContainer);
        ftOverlay.setToValue(0.0);
        ftOverlay.setOnFinished(e -> {
            overlayContainer.setVisible(false);
            settingsBox.setVisible(false);
            howToPlayBox.setVisible(false);
        });
        ftOverlay.play();
    }
    
    public boolean isOverlayVisible() {
        return overlayContainer.isVisible();
    }
}
