package com.nhom67.platformfighter.scene;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import com.nhom67.platformfighter.app.FighterApp;
import com.nhom67.platformfighter.core.WinnerInfo;
import com.nhom67.platformfighter.util.SoundManager;
import static com.almasb.fxgl.dsl.FXGL.*;

public class GameOverController {

    @FXML private Rectangle overlayBg;
    @FXML private Text titleText;
    @FXML private Text winnerText;
    @FXML private StackPane btnReturn;
    @FXML private Polygon btnBg;
    @FXML private Polygon btnAccent;

    private final WinnerInfo winner;

    public GameOverController(WinnerInfo winner) {
        this.winner = winner;
    }

    @FXML
    public void initialize() {
        winnerText.setText("\uD83C\uDFC6 " + winner.getMessage());

        // Đọc accent color của map vừa chơi
        String accentHex = (FighterApp.selectedMap != null)
                ? FighterApp.selectedMap.getAccentColor()
                : "#a8e6cf"; // fallback
        Color accent = Color.web(accentHex);
        Color accentGlow = Color.web(accentHex, 0.8);

        // Đổi màu title "BATTLE CONCLUDED"
        titleText.setStyle("-fx-fill: " + toRgbaString(accentHex, 1.0) + ";"
                + "-fx-effect: dropshadow(three-pass-box, black, 20, 0, 0, 0);");

        // Đổi màu nền overlay: dark tint của accent (RGB * 0.08, alpha 0.88)
        overlayBg.setFill(Color.color(
                accent.getRed()   * 0.08,
                accent.getGreen() * 0.08,
                accent.getBlue()  * 0.08,
                0.88
        ));
        // Đổi màu accent polygon và border button
        btnAccent.setFill(accent);
        btnBg.setStyle("-fx-stroke: " + toRgbaString(accentHex, 0.7) + ";");

        // Hover animation với glow theo accent
        DropShadow shadow = new DropShadow(15, accentGlow);
        ScaleTransition hoverScale = new ScaleTransition(Duration.seconds(0.15), btnReturn);
        
        btnReturn.setOnMouseEntered(e -> {
            btnReturn.setEffect(shadow);
            hoverScale.setToX(1.05);
            hoverScale.setToY(1.05);
            hoverScale.play();
        });

        btnReturn.setOnMouseExited(e -> {
            btnReturn.setEffect(null);
            hoverScale.setToX(1.0);
            hoverScale.setToY(1.0);
            hoverScale.play();
        });
        
        btnReturn.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            FighterApp.isIngame = false;
            getSceneService().popSubScene();
            getGameController().gotoMainMenu();
            getSceneService().pushSubScene(new SelectScene());
        });
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
}
